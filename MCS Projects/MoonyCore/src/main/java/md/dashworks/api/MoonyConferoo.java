package md.dashworks.api;

import org.bukkit.configuration.file.FileConfiguration;

public class MoonyConferoo
{
    public FileConfiguration config;

    public void setConfig(final FileConfiguration config) { this.config = config; }

    public boolean isConfigSet() { return this.config != null; }

    public FileConfiguration getConfig() { return this.config; }
}