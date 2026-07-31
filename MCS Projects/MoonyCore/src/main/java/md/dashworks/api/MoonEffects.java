package md.dashworks.api;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class MoonEffects
{
    public void playCircularParticlesForPlayer(final Player player, final int circles, final int count, final int angle, final Particle particle)
    {
        if (!player.isOnline()) return;

        final World world = player.getWorld();

        for (int a = 0; a < circles; a += 1)
        {
            double offset = (a - (circles - 1) / 2.0) * 0.6;

            for (int b = 0; b < count; b += 1)
            {
                double w = (angle * Math.PI * b) / count;
                double x = Math.cos(w) * 2;
                double z = Math.sin(w) * 2;

                world.spawnParticle(
                        particle, player.getLocation().clone().add(x, offset, z), 1, 0, 0, 0, 0
                    );
            }
        }
    }

    public void playCircularParticlesForPlayer(final Player player, final int circles, final int count, final Particle particle)
    {
        playCircularParticlesForPlayer(player, circles, count, 2, particle);
    }

    public void playCircularParticlesForPlayer(final Player player, final Particle particle)
    {
        playCircularParticlesForPlayer(player, 3, 24, 2, particle);
    }
}