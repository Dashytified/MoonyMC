package md.dashworks.api;

import org.bukkit.configuration.file.FileConfiguration;

public class MoonConfiguration
{
    public FileConfiguration config;

    public void setConfig(final FileConfiguration config) { this.config = config; }

    public boolean isConfigSet() { return this.config != null; }

    public FileConfiguration getConfig() { return this.config; }
}


        /* Example code
        *
            FileConfiguration cfg = getConfig();

            String base = "players." + uuid + ".location";

            String worldName = cfg.getString(base + ".world", null);
            if (worldName == null) return; // no saved location for this UUID

            World world = Bukkit.getWorld(worldName);
            if (world == null) return;

            double x = cfg.getDouble(base + ".x");
            double y = cfg.getDouble(base + ".y");
            double z = cfg.getDouble(base + ".z");

            Location loc = new Location(world, x, y, z);
        *
        *
        *
            FileConfiguration cfg = getConfig();

            ConfigurationSection players = cfg.getConfigurationSection("players");
            if (players == null) return;

            for (String uuidStr : players.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                // read base = "players." + uuidStr + ".location"
            }
        *
        *
        *
            UUID uuid = player.getUniqueId();
            Location loc = player.getLocation();

            FileConfiguration cfg = getConfig();
            String base = "players." + uuid + ".location";

            cfg.set(base + ".world", loc.getWorld().getName());
            cfg.set(base + ".x", loc.getX());
            cfg.set(base + ".y", loc.getY());
            cfg.set(base + ".z", loc.getZ());

            saveConfig();
        *
        * */