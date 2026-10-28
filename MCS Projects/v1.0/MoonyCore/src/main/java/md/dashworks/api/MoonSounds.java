package md.dashworks.api;


import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MoonSounds
{
    public void playSoundToPlayer(final Player player, final Sound sound, final float volume, final float pitch)
    {
        if (player.isOnline()) player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void playSoundToPlayer(final Player player, final Sound sound)
    {
        playSoundToPlayer(player, sound, 1.0F, 1.0F);
    }
}