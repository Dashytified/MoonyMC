package md.dashworks.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class MoonPlayers extends JavaPlugin
{
    final MoonLogger logger = new MoonLogger();

    public Player getPlayerFromCommandSender(final CommandSender sender, boolean enforce)
    {
        if (sender instanceof Player) return (Player)sender;
        else if (enforce) logger.log_warning("Plugin does not support non-player command senders yet");

        return null;
    }

    public Player getPlayerFromCommandSender(final CommandSender sender) { return getPlayerFromCommandSender(sender, true); }
}