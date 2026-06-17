package md.dashworks;

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
        plugin = this;

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
        public static boolean HomeHelp(final CommandSender sender, final String[] args) { return true; }
        public static boolean GoHome(final CommandSender sender, final String[] args) { return true; }
        public static boolean ListHomes(final CommandSender sender, final String[] args) { return true; }
        public static boolean InspectHome(final CommandSender sender, final String[] args) { return true; }
        public static boolean DeleteHome(final CommandSender sender, final String[] args) { return true; }
        public static boolean SetMyHome(final CommandSender sender, final String[] args) { return true; }
    }

    @Override public boolean onCommand(final @NonNull CommandSender sender, final @NonNull Command command, final @NonNull String label, final @NonNull String[] args)
    {
        if (!(sender instanceof Player)) getLogger().warning("Operation not yet supported by plugin");

        final String command_id = command.getName().toLowerCase();

        return switch (command_id)
        {
            case "home-help"    -> CommandHandlers.HomeHelp(sender, args);
            case "go-home"      -> CommandHandlers.GoHome(sender, args);
            case "list-homes"   -> CommandHandlers.ListHomes(sender, args);
            case "inspect-home" -> CommandHandlers.InspectHome(sender, args);
            case "delete-home"  -> CommandHandlers.DeleteHome(sender, args);
            case "set-my-home"  -> CommandHandlers.SetMyHome(sender, args);
            default             -> true;
        };
    }

    @Override public void onDisable() { getLogger().warning("Plugin has been disabled, unfortunately :("); }
}
