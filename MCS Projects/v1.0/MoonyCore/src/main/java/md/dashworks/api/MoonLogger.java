package md.dashworks.api;

public class MoonLogger
{
    public void log_warning(final String message) { MoonyCore.plugin.getLogger().warning(message); }
}