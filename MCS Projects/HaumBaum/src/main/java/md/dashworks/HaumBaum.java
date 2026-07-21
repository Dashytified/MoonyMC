package md.dashworks;

import md.dashworks.api.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
    private final MoonyConferoo conferoo = new MoonyConferoo();

    // loadPlayerHomes(JavaPlugin plugin);

    // savePlayerHomes();
}

public final class HaumBaum extends JavaPlugin implements CommandExecutor
{
    public static JavaPlugin plugin;
    public static HaumBaum instance;
    public static PlayerHomes homes = new PlayerHomes();

    @Override public void onEnable()
    {
        plugin = instance = this;

        saveDefaultConfig();

        // loadPlayerHomes(plugin);

        /* Example code
        *
            FileConfiguration cfg = getConfig();

            String base = "players." + uuid + ".location";

            String worldName = cfg.getString(base + ".world", null);
            if (worldName == null) return; // no saved location for this UUID

            World world = Bukkit.getWorld(worldName);
            if (world == null) return;

            double x = cfg.getDouble(base + ".x");
            double y = cfg.getDouble(base + ".y");
            double z = cfg.getDouble(base + ".z");

            Location loc = new Location(world, x, y, z);
        *
        *
        *
            FileConfiguration cfg = getConfig();

            ConfigurationSection players = cfg.getConfigurationSection("players");
            if (players == null) return;

            for (String uuidStr : players.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                // read base = "players." + uuidStr + ".location"
            }
        *
        *
        *
            UUID uuid = player.getUniqueId();
            Location loc = player.getLocation();

            FileConfiguration cfg = getConfig();
            String base = "players." + uuid + ".location";

            cfg.set(base + ".world", loc.getWorld().getName());
            cfg.set(base + ".x", loc.getX());
            cfg.set(base + ".y", loc.getY());
            cfg.set(base + ".z", loc.getZ());

            saveConfig();
        *
        * */

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
        // savePlayerHomes();

        getLogger().warning("Plugin has been disabled, unfortunately :(");
    }

    private class CommandHandlers
    {
        private final MoonPlayers players = new MoonPlayers();

        public boolean HomeHelp(final Player player, final String[] args)
        {
            final String menu = """
                    <rainbow><italic>! The almighty horse came from far away</italic></rainbow>
                    - <yellow>/home-help</yellow>  <green>:  Renders this help text menu</green>
                    - <yellow>/go-home [name]</yellow>  <green>:  Go to one of your set homes</green>
                    - <yellow>/list-homes</yellow>  <green>:  List the home(s) you have set</green>
                    - <yellow>/inspect-home [player] [name | none]</yellow>  <green>:  Inspect a player's home at once (May not work for you)</green>
                    - <yellow>/delete-home [name]</yellow>  <green>:  Delete one of your set homes</green>
                    - <yellow>/set-my-home [name]</yellow>  <green>:  Set a home by name</green>
                    """;

            return instance.players.sendPlayerMessage(player, menu);
        }

        public boolean SetMyHome(final Player player, final String[] args)
        {
            if (args.length != 2)
            {
                final UUID uuid = player.getUniqueId();

                // homeExists(uuid);
                List<PlayerHome> homes = this.homes.containsKey(uuid) ? this.homes.get(uuid) : new ArrayList<>();

                final String name = args[1].toLowerCase();
                final Location location = player.getLocation();

                // tryAddPlayerHome(uuid, name, location);
                final PlayerHome home = new PlayerHome(name, location);

                homes.add(home);

                this.homes.put(uuid, homes);
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
