package md.dashworks.api;

import org.bukkit.plugin.java.JavaPlugin;

public final class MoonyCore extends JavaPlugin
{
    public static JavaPlugin plugin;

    @Override public void onEnable() { plugin = this;  }
    @Override public void onDisable() { }
}
