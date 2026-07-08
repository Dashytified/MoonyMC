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

public final class HaumBaum extends JavaPlugin implements CommandExecutor
{
    public static JavaPlugin plugin;
    public static HaumBaum instance;

    @Override public void onEnable()
    {
        plugin = instance = this;

        getCommand("home-help"   ).setExecutor(this);
        getCommand("go-home"     ).setExecutor(this);
        getCommand("list-homes"  ).setExecutor(this);
        getCommand("inspect-home").setExecutor(this);
        getCommand("delete-home" ).setExecutor(this);
        getCommand("set-my-home" ).setExecutor(this);

        getLogger().info("Plugin has bee enabled :)");
    }

    public final MoonPlayers players = new MoonPlayers();

    private class CommandHandlers
    {
        public final MoonPlayers players = new MoonPlayers();

        public static boolean HomeHelp(final Player player, final String[] args)
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

        // Startup  = LOAD
        // Shutdown = SAVE
        private final Map<UUID, List<PlayerHome>> homes = new HashMap<>();

        public boolean GoHome(final Player player, final String[] args)
        { return true; }

        public boolean ListHomes(final Player player, final String[] args)
        {
            return true;
        }

        public boolean InspectHome(final Player player, final String[] args)
        {
            return true;
        }

        public boolean DeleteHome(final Player player, final String[] args)
        {
            return true;
        }

        public boolean SetMyHome(final Player player, final String[] args)
        {
            if (args.length != 2)
            {
                final UUID uuid = player.getUniqueId();

                List<PlayerHome> homes = this.homes.containsKey(uuid) ? this.homes.get(uuid) : new ArrayList<>();

                final String name = args[1].toLowerCase();
                final Location location = player.getLocation();
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

    @Override public boolean onCommand(final @NonNull CommandSender sender, final @NonNull Command command, final @NonNull String label, final @NonNull String[] args)
    {
        final Player player = players.getPlayerFromCommandSender(sender);

        if (player == null) return false;

        return switch (command.getName().toLowerCase())
        {
            case "home-help"    -> CommandHandlers.HomeHelp(player, args);
            case "go-home"      -> CommandHandlers.GoHome(player, args);
            case "list-homes"   -> CommandHandlers.ListHomes(player, args);
            case "inspect-home" -> CommandHandlers.InspectHome(player, args);
            case "delete-home"  -> CommandHandlers.DeleteHome(player, args);
            case "set-my-home"  -> CommandHandlers.SetMyHome(player, args);
            default             -> true;
        };
    }

    @Override public void onDisable() { getLogger().warning("Plugin has been disabled, unfortunately :("); }
}
