package org.nguyendevs.commandAliases.dispatch;

import org.bukkit.command.CommandSender;

import org.nguyendevs.commandAliases.CommandAliases;
import org.nguyendevs.commandAliases.util.ConsoleLogger;

public class CommandDispatcher {

    private final CommandAliases plugin;
    private final PluginQualifiedDispatchStrategy qualifiedStrategy;
    private final SimpleDispatchStrategy simpleStrategy;

    public CommandDispatcher(CommandAliases plugin) {
        this.plugin = plugin;
        this.qualifiedStrategy = new PluginQualifiedDispatchStrategy(plugin);
        this.simpleStrategy = SimpleDispatchStrategy.INSTANCE;
    }

    public boolean dispatch(CommandSender sender, String commandString) {
        if (commandString == null || commandString.isBlank()) return false;

        commandString = commandString.trim();

        var colonIndex = commandString.indexOf(':');
        if (colonIndex > 0) {
            var pluginName = commandString.substring(0, colonIndex);
            var pluginInstance = org.bukkit.Bukkit.getPluginManager().getPlugin(pluginName);
            if (pluginInstance != null) {
                if (plugin.config().debug()) {
                    ConsoleLogger.debug("Attempting plugin-qualified dispatch for: " + commandString);
                }
                return qualifiedStrategy.dispatch(sender, commandString);
            }
        }

        if (plugin.config().debug()) {
            ConsoleLogger.debug("Dispatching: " + commandString);
        }

        return simpleStrategy.dispatch(sender, commandString);
    }

    public void refreshCommandMap() {
        qualifiedStrategy.refreshCommandMap();
    }

    public static String colorize(String text) {
        if (text == null) return "";
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }
}
