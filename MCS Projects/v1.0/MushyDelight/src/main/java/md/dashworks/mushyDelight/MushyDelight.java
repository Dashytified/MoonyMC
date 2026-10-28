package md.dashworks.mushyDelight;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

final public class MushyDelight extends JavaPlugin
{
    static class MycelialListener implements Listener
    {
        final Set<Material> MUSH_EAT_MATERIALS = Set.of( Material.MUSHROOM_STEW, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM, Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS );
        final Set<UUID> MUSH_EAT_COOLDOWN_CACHE = new HashSet<>();
        final long MUSH_EAT_COOLDOWN_TICKS = 40;

        boolean isPlayerFungiEligible(final PlayerInteractEvent event)
        {
            final Action action = event.getAction();

            if (action != Action.RIGHT_CLICK_AIR)
                if (action != Action.RIGHT_CLICK_BLOCK)
                    return false;

            final EquipmentSlot slot = event.getHand();

            if (slot != EquipmentSlot.HAND)
                return false;

            final ItemStack item = event.getItem();

            if (item == null || !MUSH_EAT_MATERIALS.contains(item.getType()))
                return false;

            final Player player = event.getPlayer();

            return player.getFoodLevel() < 20 && player.getGameMode() != GameMode.CREATIVE;
        }

        boolean isPlayerOnFungiEatCooldown(final UUID uuid)
        {
            return MUSH_EAT_COOLDOWN_CACHE.contains(uuid);
        }

        void removePLayerFungiItem(final PlayerInteractEvent event)
        {
            final ItemStack item = event.getItem();

            if (item == null)
                return;

            final int amount = item.getAmount();
            final Player player = event.getPlayer();
            final PlayerInventory inventory = player.getInventory();

            if (amount > 1)
            {
                item.setAmount(amount - 1);
                inventory.setItemInMainHand(item);
            }

            else
            {
                inventory.setItemInMainHand(null);
            }
        }

        void setFungiEatPlayerCooldown(final UUID uuid)
        {
            MUSH_EAT_COOLDOWN_CACHE.add(uuid);

            final BukkitRunnable runnable = new BukkitRunnable()
            {
                @Override public void run()
                {
                    MUSH_EAT_COOLDOWN_CACHE.remove(uuid);
                }
            };

            runnable.runTaskLater(plugin, MUSH_EAT_COOLDOWN_TICKS);
        }

        BukkitRunnable getNomParticleRunnable(final Player player)
        {
            final PlayerInventory inventory = player.getInventory();
            final Material material = inventory.getItemInMainHand().getType();

            final BlockData particle1BlockData = Bukkit.createBlockData(Material.BROWN_MUSHROOM_BLOCK);
            final BlockData particle2BlockData = Bukkit.createBlockData(Material.RED_MUSHROOM_BLOCK);

            return new BukkitRunnable()
            {
                int currentTick = 0, maximumTicks = 12;

                @Override public void run()
                {
                    if (!player.isOnline())
                    {
                        cancel();

                        return;
                    }

                    else if (currentTick++ >= maximumTicks)
                    {
                        final int currentLevel = player.getFoodLevel();

                        if (currentLevel < 20)
                        {
                            final int newLevel = Math.min(currentLevel + 5, 20);

                            player.setFoodLevel(newLevel);
                            player.setSaturation(newLevel);
                        }

                        cancel();

                        return;
                    }

                    else
                    {
                        final ItemStack current = inventory.getItemInMainHand();

                        if (current.getType() != material)
                        {
                            cancel();

                            return;
                        }
                    }

                    final Location eyeEatLocation = player.getEyeLocation().subtract(0, 0.25, 0);
                    final World world = eyeEatLocation.getWorld();

                    world.spawnParticle(Particle.BLOCK_CRUMBLE, eyeEatLocation, 6, 0.08, 0.08, 0.08, 0, particle1BlockData);
                    world.spawnParticle(Particle.BLOCK_CRUMBLE, eyeEatLocation, 6, 0.08, 0.08, 0.08, 0, particle2BlockData);

                    world.playSound(eyeEatLocation, Sound.ENTITY_GENERIC_EAT, 0.9F, 1.0F);
                }
            };
        };

        @EventHandler public void onPlayerInteract(final PlayerInteractEvent event)
        {
            if (!isPlayerFungiEligible(event))
                return;

            final Player player = event.getPlayer();
            final UUID uuid = player.getUniqueId();

            if (isPlayerOnFungiEatCooldown(uuid))
                return;

            removePLayerFungiItem(event);

            final BukkitRunnable playNomParticle = getNomParticleRunnable(player);

            playNomParticle.runTaskTimer(plugin, 0, 3L);

            setFungiEatPlayerCooldown(uuid);

            event.setCancelled(true);
        }
    }

    // Goals next time (After spawn plugin):
    // - Mushroom should only be removed from the inventory when the particles are done particuling.
    // - Add config to this plugin, then conversely also to the spawn plugin; API should be made.
    // - I want mushrooms to be bone meal supportive.
    // - I also want mushrooms to have a chance at dropping when mobs are killed.
    // - Pen and paper for next time.

    // Then, the time after that:
    // - I want metadata for mushrooms, custom names and effects (leverage config for this, obviously).
    // - Rename certain stews; adds to the variety of mushroom stews.
    // - Allow mushroom brewing; result == custom potions.
    // - Mushrooms around a chest, make it so the chest cannot be broken.
    // - Standing nearby mushrooms, under specific conditions, gives you a regenerative effect.

    public static JavaPlugin plugin;

    @Override public void onEnable()
    {
        plugin = this;

        getServer().getPluginManager().registerEvents(new MycelialListener(), plugin);

        getLogger().info("Plugin has been enabled :)");
    }

    @Override public void onDisable() { getLogger().warning("Plugin has been disabled, unfortunately :("); }
}
