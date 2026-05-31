
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