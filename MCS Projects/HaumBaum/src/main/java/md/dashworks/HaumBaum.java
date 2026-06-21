package md.dashworks;

import md.dashworks.api.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

public final class HaumBaum extends JavaPlugin implements CommandExecutor
{
    public static JavaPlugin plugin;

    @Override public void onEnable()
    {
        getCommand("home-help"   ).setExecutor(this);
        getCommand("go-home"     ).setExecutor(this);
        getCommand("list-homes"  ).setExecutor(this);
        getCommand("inspect-home").setExecutor(this);
        getCommand("delete-home" ).setExecutor(this);
        getCommand("set-my-home" ).setExecutor(this);

        getLogger().info("Plugin has bee enabled :)");
    }

    private static class CommandHandlers
    {
        public static boolean HomeHelp(final Player player, final String[] args)
        {
            // - Make this GUI look more compact
            // - add this command
            // - test it out

            return true;
        }
        public static boolean GoHome(final Player player, final String[] args) { return true; }
        public static boolean ListHomes(final Player player, final String[] args) { return true; }
        public static boolean InspectHome(final Player player, final String[] args) { return true; }
        public static boolean DeleteHome(final Player player, final String[] args) { return true; }
        public static boolean SetMyHome(final Player player, final String[] args) { return true; }
    }

    private final MoonyCore moon = new MoonyCore();
    private final MoonLogger loggers = new MoonLogger();
    private final MoonPlayers players = new MoonPlayers();

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
