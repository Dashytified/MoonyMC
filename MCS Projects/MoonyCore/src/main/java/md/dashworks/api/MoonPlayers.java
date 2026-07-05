package md.dashworks.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class MoonPlayers
{
    final MoonLogger logger = new MoonLogger();

    public Player getPlayerFromCommandSender(final CommandSender sender, boolean enforce)
    {
        if (sender instanceof Player) return (Player)sender;
        else if (enforce) logger.log_warning("Plugin does not support non-player command senders yet");

        return null;
    }

    public Player getPlayerFromCommandSender(final CommandSender sender) { return getPlayerFromCommandSender(sender, true); }

    public boolean sendPlayerMessage(final Player player, final String message)
    {
        final Component component = MiniMessage.miniMessage().deserialize(message);

        player.sendMessage(component);

        return true;
    }
}