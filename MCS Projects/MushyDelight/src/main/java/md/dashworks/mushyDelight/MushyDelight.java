package md.dashworks.mushyDelight;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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

            final EquipmentSlot slot = event.getHand();

            if (slot != EquipmentSlot.HAND)
                return false;

            final ItemStack item = event.getItem();

            return item != null && MUSH_EAT_MATERIALS.contains(item.getType());
        }

        public boolean isPlayerOnFungiEatCooldown(final UUID uuid)
        {
            return MUSH_EAT_COOLDOWN_CACHE.contains(uuid);
        }

        public void removePLayerFungiItem(final PlayerInteractEvent event)
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

            // - spawn nom particles at player mouth, as with food, for 10 ticks
            // - spawn nom sounds at player mouth, for 10 ticks
            // - spawn sphere of white particles around player, 3 blocks away
            // - remove the consumed item
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

            removePLayerFungiItem(event);



            setFungiEatPlayerCooldown(uuid);

            event.setCancelled(true);
        }
    }

    /*
package com.example.fungi; // adjust package

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FungiConsumeListener implements Listener {

    private final Plugin plugin;

    public FungiConsumeListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only care right-click main hand interactions
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;

        // Replace this with your real eligibility check
        if (!isPlayerFungiEligible(item)) return;

        consumeFungiAndEffect(plugin, player, item.getType());
    }

    // Example eligibility (replace with your MUSH_EAT_MATERIALS check)
    private boolean isPlayerFungiEligible(ItemStack item) {
        Material t = item.getType();
        return t == Material.RED_MUSHROOM || t == Material.BROWN_MUSHROOM
                || t == Material.RED_MUSHROOM_BLOCK || t == Material.BROWN_MUSHROOM_BLOCK;
    }

    public static void consumeFungiAndEffect(final Plugin plugin, final Player player, final Material mushroomBlockMaterial) {
        if (player == null) return;

        // Remove one from main hand (immediate)
        PlayerInventory inv = player.getInventory();
        ItemStack hand = inv.getItemInMainHand();
        if (hand == null || hand.getType().isAir()) return;
        int newAmount = hand.getAmount() - 1;
        if (newAmount > 0) {
            hand.setAmount(newAmount);
            inv.setItemInMainHand(hand);
        } else {
            inv.setItemInMainHand(null);
        }

        // Prepare block data for block-crack particles
        final BlockData mushroomData;
        try {
            mushroomData = Bukkit.createBlockData(mushroomBlockMaterial);
        } catch (IllegalArgumentException ex) {
            // fallback to RED_MUSHROOM_BLOCK if provided material not valid
            mushroomData = Bukkit.createBlockData(Material.RED_MUSHROOM_BLOCK);
        }

        // Mouth location: eye - ~0.6
        final Location mouth = player.getEyeLocation().subtract(0, 0.6, 0);

        // Play nom particles + sounds for 10 ticks (one tick per run)
        new BukkitRunnable() {
            int tick = 0;
            final int max = 10;
            @Override
            public void run() {
                if (tick++ >= max) {
                    cancel();
                    return;
                }
                // small block-crack bursts at mouth to mimic mushroom break bits
                mouth.getWorld().spawnParticle(Particle.BLOCK_CRACK, mouth, 6, 0.08, 0.08, 0.08, 0, mushroomData);
                // small smoke/crit to emphasize "nom"
                mouth.getWorld().spawnParticle(Particle.SMOKE_NORMAL, mouth, 4, 0.10, 0.10, 0.10, 0.01);
                mouth.getWorld().playSound(mouth, Sound.ENTITY_GENERIC_EAT, 0.9f, 1.0f);
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Instant sphere of block-crack particles radius 3 around player
        final double radius = 3.0;
        final int points = 200; // density
        Location center = player.getLocation().add(0, 1.0, 0); // torso center
        for (int i = 0; i < points; i++) {
            // uniformly sample sphere
            double u = Math.random();
            double v = Math.random();
            double theta = 2 * Math.PI * u;
            double phi = Math.acos(2 * v - 1);
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);
            Location spawn = center.clone().add(x, y, z);
            // spawn a single block-crack particle using the mushroom block data
            spawn.getWorld().spawnParticle(Particle.BLOCK_CRACK, spawn, 1, 0, 0, 0, 0, mushroomData);
        }
    }
}
    * */

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

        getServer().getPluginManager().registerEvents(new MycelialListener(), plugin);
    }

    @Override public void onDisable()
    {
        
    }
}
