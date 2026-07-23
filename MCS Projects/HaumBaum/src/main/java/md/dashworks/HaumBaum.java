package md.dashworks;

import md.dashworks.api.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.*;

class PlayerHomes
{
    private class PlayerHome
    {
        public String name;
        public Location location;

        public PlayerHome(final String name, final Location location)
        {
            this.name = name;
            this.location = location;
        }
    }

    private final Map<UUID, List<PlayerHome>> homes = new HashMap<>(); // <--- need a separate class for this

    private final MoonConfiguration config = new MoonConfiguration();
    private final MoonPlayers players = new MoonPlayers();
    private final MoonLogger logger = new MoonLogger();

    public void loadPlayerConfiguration(final JavaPlugin plugin)
    {
        plugin.saveDefaultConfig();

        try
        {
            final FileConfiguration config = plugin.getConfig();
            final ConfigurationSection registered = config.getConfigurationSection("player-storage");

            if (registered == null) return;

            for (String key : registered.getKeys(false))
            {
                final UUID uuid = players.tryGetPlayerUUIDFromString(key);

                if (uuid == null) continue;

                final ConfigurationSection entries = config.getConfigurationSection("player-storage." + key);

                if (entries == null) continue;

                final List<PlayerHome> homes = new ArrayList<>();

                for (String entry : entries.getKeys(false))
                {
                    final String base = "player-storage." + key  + "." + entry + ".";
                    final String world_name = config.getString(base + "world-name");

                    if (world_name == null) continue;

                    final double world_x = config.getDouble(base + "world-x", -1);
                    final double world_y = config.getDouble(base + "world-y", -1);
                    final double world_z = config.getDouble(base + "world-z", -1);

                    if (world_x == -1 && world_y == -1 && world_z == -1) continue;

                    final World world = Bukkit.getWorld(world_name);

                    if (world == null) continue;

                    final Location location = new Location(world, world_x, world_y, world_z);
                    final PlayerHome home = new PlayerHome(entry, location);

                    homes.add(home);
                }

                this.homes.put(uuid, homes);
            }
        }

        catch (final Exception e)
        {
            logger.log_warning("[-] An exception was thrown whilst loading player configurations for HaumBaum.");
        }
    }


    public void savePlayerHomes(final JavaPlugin plugin)
    {
        //plugin.saveDefaultConfig();

        try
        {
            final FileConfiguration config = plugin.getConfig();

            config.set("player-storage", null);

            for (Map.Entry<UUID, List<PlayerHome>> entry : this.homes.entrySet())
            {
                final UUID uuid = entry.getKey();
                final List<PlayerHome> homes = entry.getValue();

                ConfigurationSection section = config.createSection("player-storage." + uuid.toString());

                for (PlayerHome home : homes)
                {
                    final String name = home.name;
                    final String base = "player-storage." + uuid.toString() + "." + name + ".";

                    config.set(base + "world-name", name);

                    final Location location = home.location;

                    config.set(base + "world-x",  location.getX());
                    config.set(base + "world-y",  location.getY());
                    config.set(base + "world-z",  location.getZ());
                }
            }

            plugin.saveConfig();
        }

        catch (final Exception e)
        {
            logger.log_warning("[-] An exception was thrown whilst saving player configurations for HaumBaum.");
        }
    }

    public boolean hasPlayerSetHomes(final UUID uuid) { return homes.containsKey(uuid) && !homes.get(uuid).isEmpty(); }

    public boolean doesPlayerHomeExist(final UUID uuid, final String name)
    {
        if (!hasPlayerSetHomes(uuid)) return false;

        final List<PlayerHome> homes = this.homes.get(uuid);

        for (PlayerHome home : homes)
            if (home.name.equals(name)) return true;

        return false;
    }

    public int getPlayerHomesCount(final UUID uuid) { return hasPlayerSetHomes(uuid) ? this.homes.get(uuid).size() : 0; }

    public boolean trySetPlayerHome(final Player player, final String name)
    {
        try
        {
            if (name == null) return false;

            String treated = name.trim().replaceAll("[^A-Za-z0-9_-]", "");

            if (treated.isEmpty()) return false;

            final UUID uuid = player.getUniqueId();
            final Location location = player.getLocation();
            final PlayerHome home = new PlayerHome(name, location);

            homes.computeIfAbsent(uuid, x -> new ArrayList<>()).add(home);
        }

        catch (Exception e)
        {
            return false;
        }

        return true;
    }
}

public final class HaumBaum extends JavaPlugin implements CommandExecutor
{
    public static JavaPlugin plugin;
    public static HaumBaum instance;

    private final static PlayerHomes homes = new PlayerHomes();

    @Override public void onEnable()
    {
        plugin = instance = this;

        homes.loadPlayerConfiguration(this);

        getCommand("home-help"   ).setExecutor(this);
        getCommand("go-home"     ).setExecutor(this);
        getCommand("list-homes"  ).setExecutor(this);
        getCommand("inspect-home").setExecutor(this);
        getCommand("delete-home" ).setExecutor(this);
        getCommand("set-my-home" ).setExecutor(this);

        getLogger().info("Plugin has bee enabled :)");
    }

    @Override public void onDisable()
    {
        homes.savePlayerHomes(this);

        getLogger().warning("Plugin has been disabled, unfortunately :(");
    }

    private class CommandHandlers
    {
        private final MoonPlayers players = new MoonPlayers();

        public boolean HomeHelp(final Player player, final String[] args) { return instance.players.sendPlayerMessage(player, "<rainbow>/home-help  /go-home <italic>[name]</italic>  /list-homes  /inspect-home <italic>[player] [name | none]</italic>  /delete-home <italic>[name]</italic>  /set-my-home <italic>[name]</italic></rainbow>"); }

        public boolean SetMyHome(final Player player, final String[] args)
        {
            if (args.length != 2)
            {
                final UUID uuid = player.getUniqueId();

                if (homes.getPlayerHomesCount(uuid) >= 2 && !player.isOp()) players.sendPlayerMessage(player, "<red>You may not set any more homes.</red>");

                else if (args[1].length() < 2) players.sendPlayerMessage(player, "<red>Your home name must be more than 2 characters long.</red>");

                else if (args[1].length() > 12) players.sendPlayerMessage(player, "<red>Your home name may not be any longer than 12 characters.</red>");

                else if (homes.trySetPlayerHome(player, args[1])) players.sendPlayerMessage(player, "<green>Your home has been set to your current location.</green>");

                else players.sendPlayerMessage(player, "<red>Could not process the setting of the by you requested home.</red>");
            }

            else
            {
                players.sendPlayerMessage(player, "<green>Usage: /set-my-home [name]</green>");
            }

            return true;
        }
    }

    private final MoonPlayers players = new MoonPlayers();

    @Override public boolean onCommand(final @NonNull CommandSender sender, final @NonNull Command command, final @NonNull String label, final @NonNull String[] args)
    {
        final Player player = players.getPlayerFromCommandSender(sender);

        if (player == null) return false;

        final CommandHandlers handlers = new CommandHandlers();

        return switch (command.getName().toLowerCase())
        {
            case "home-help"    -> handlers.HomeHelp(player, args);
            //case "go-home"      -> CommandHandlers.GoHome(player, args);
            //case "list-homes"   -> CommandHandlers.ListHomes(player, args);
            //case "inspect-home" -> CommandHandlers.InspectHome(player, args);
            //case "delete-home"  -> CommandHandlers.DeleteHome(player, args);
            case "set-my-home"  -> handlers.SetMyHome(player, args);
            default             -> true;
        };
    }
}
