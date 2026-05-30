package md.dashworks.mushyDelight;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class MushyDelight extends JavaPlugin
{
    public class MycelialListener implements Listener
    {
        private final Set<Material> MUSH_EAT_MATERIALS = Set.of( Material.MUSHROOM_STEW, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM, Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS );
        private final Set<UUID> MUSH_EAT_COOLDOWN_CACHE = new HashSet<>();
        private final long MUSH_EAT_COOLDOWN_TICKS = 20;

        public boolean isPlayerFungiEligible(final PlayerInteractEvent event)
        {
            final Action action = event.getAction();

            if (action != Action.RIGHT_CLICK_AIR)
                if (action != Action.RIGHT_CLICK_BLOCK)
                    return false;

            final ItemStack item = event.getItem();

            return item != null && MUSH_EAT_MATERIALS.contains(item.getType());
        }

        public boolean isPlayerOnFungiEatCooldown(final UUID uuid)
        {
            return MUSH_EAT_COOLDOWN_CACHE.contains(uuid);
        }

        public void setFungiEatPlayerCooldown(final UUID uuid)
        {
            MUSH_EAT_COOLDOWN_CACHE.add(uuid);

            final BukkitRunnable runnable = new BukkitRunnable()
            {
                @Override public void run()
                {
                    MUSH_EAT_COOLDOWN_CACHE.remove(uuid);
                }
            };

            runnable.runTaskLater(MushyDelight.plugin, MUSH_EAT_COOLDOWN_TICKS);
        }

        @EventHandler public void onPlayerInteract(final PlayerInteractEvent event)
        {
            if (!isPlayerFungiEligible(event))
                return;

            final Player player = event.getPlayer();
            final UUID uuid = player.getUniqueId();

            if (isPlayerOnFungiEatCooldown(uuid))
                return;

            // Remove item
            final ItemStack item = event.getItem();
            final Material type = item.getType();

            int amount = item.getAmount();

            if (amount > 1)
                item.setAmount(amount - 1);

            else
            {
                final Inventory inventory = player.getInventory().setItem;
            }

            // mushroom things; consume mushroom, add effects, play nom nom sound, spawn particles around player, creating a sphere around player.

            setFungiEatPlayerCooldown(uuid);
            event.setCancelled(true);
        }
    }

    // Goals for now:
    // - A simple list of mushrooms; each being handled in their own way.
    // - Mushrooms should become edible raw.

    // Goals next time (After spawn plugin):
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
        // Plugin startup logic

    }

    @Override public void onDisable()
    {

        // Plugin shutdown logic

    }
}
