package org.nguyendevs.commandAliases.dispatch;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import org.nguyendevs.commandAliases.CommandAliases;
import org.nguyendevs.commandAliases.util.ConsoleLogger;

import java.lang.reflect.Field;

class PluginQualifiedDispatchStrategy implements DispatchStrategy {

    private final CommandAliases plugin;
    private SimpleCommandMap commandMap;

    PluginQualifiedDispatchStrategy(CommandAliases plugin) {
        this.plugin = plugin;
        this.commandMap = resolveCommandMap();
    }

    @Override
    public boolean dispatch(CommandSender sender, String commandString) {
        if (commandMap == null) return false;

        var qualified = QualifiedCommand.parse(commandString);
        if (qualified == null) return false;

        var pluginInstance = Bukkit.getPluginManager().getPlugin(qualified.pluginName);
        if (pluginInstance == null) return false;

        var command = findPluginCommand(pluginInstance, qualified.commandLabel);
        if (command == null) return false;

        if (plugin.config().debug()) {
            ConsoleLogger.debug("Plugin-qualified dispatch: " + qualified.pluginName + ":" + qualified.commandLabel + " args=" + String.join(" ", qualified.args));
        }

        return command.execute(sender, qualified.commandLabel, qualified.args);
    }

    private Command findPluginCommand(Plugin pluginInstance, String label) {
        if (commandMap == null) return null;

        for (var command : commandMap.getCommands()) {
            if (command instanceof PluginCommand pc) {
                var owning = pc.getPlugin();
                if (owning != null && owning.equals(pluginInstance)) {
                    if (command.getName().equalsIgnoreCase(label) || command.getAliases().contains(label.toLowerCase())) {
                        return command;
                    }
                }
            }
        }
        return null;
    }

    private SimpleCommandMap resolveCommandMap() {
        try {
            var server = Bukkit.getServer();
            var field = server.getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return (SimpleCommandMap) field.get(server);
        } catch (Exception e) {
            ConsoleLogger.error("Failed to access CommandMap: " + e.getMessage());
            return null;
        }
    }

    void refreshCommandMap() {
        this.commandMap = resolveCommandMap();
    }

    private record QualifiedCommand(String pluginName, String commandLabel, String[] args) {
        static QualifiedCommand parse(String commandString) {
            var colonIndex = commandString.indexOf(':');
            if (colonIndex <= 0) return null;

            var pluginName = commandString.substring(0, colonIndex);
            var rest = commandString.substring(colonIndex + 1).trim();
            if (rest.isEmpty()) return null;

            var parts = rest.split("\\s+", 2);
            var commandLabel = parts[0];
            var args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

            return new QualifiedCommand(pluginName, commandLabel, args);
        }
    }
}
