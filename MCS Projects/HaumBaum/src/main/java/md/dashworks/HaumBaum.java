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

    public final MoonLogger loggers = new MoonLogger();
    public final MoonPlayers players = new MoonPlayers();

    private static class CommandHandlers {
        public static boolean HomeHelp(final Player player, final String[] args)
        {
            final String menu = """
                    <rainbow>
                    #####     ####    #####   #    #  IS THE GREATEST
                    #    #   #    #  #        #    #   ALMIGHTY HORSE
                    # )O( #  ######    ###    ######
                    #    #   #    #        #  #    #
                    #####    #    #   #####   #    #
                    </rainbow>
                    
                    <green>
                    home-help : Renders this help text menu
                    go-home [name] : Go to one of your set homes
                    list-homes : List the home(s) you have set
                    inspect-home [player] [name | none] : Inspect a player's home at once (May not work for you)
                    delete-home [name] : Delete one of your set homes
                    set-my-home [name] : Set a home by name
                    </green>
                    """;

            return instance.players.sendPlayerMessage(player, menu);
        }

        public static boolean GoHome(final Player player, final String[] args)
        {
            return true;
        }

        public static boolean ListHomes(final Player player, final String[] args)
        {
            return true;
        }

        public static boolean InspectHome(final Player player, final String[] args)
        {
            return true;
        }

        public static boolean DeleteHome(final Player player, final String[] args)
        {
            return true;
        }

        public static boolean SetMyHome(final Player player, final String[] args)
        {
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
