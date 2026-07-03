package org.nguyendevs.commandAliases.registry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import org.nguyendevs.commandAliases.model.AliasDefinition;
import org.nguyendevs.commandAliases.util.ConsoleLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistrar {

    private final JavaPlugin plugin;
    private final Map<String, PluginCommand> registeredCommands = new HashMap<>();
    private CommandMap commandMap;

    public CommandRegistrar(JavaPlugin plugin) {
        this.plugin = plugin;
        this.commandMap = resolveCommandMap();
    }

    public boolean isRegistered(String name) {
        if (commandMap == null) return false;
        return commandMap.getCommand(name) != null;
    }

    public boolean register(AliasDefinition def, CommandExecutor executor, TabCompleter completer) {
        if (commandMap == null) {
            ConsoleLogger.error("Cannot register alias '" + def.commandName() + "': CommandMap unavailable");
            return false;
        }

        var name = def.commandName().toLowerCase();

        if (isRegistered(name)) {
            ConsoleLogger.warn("Cannot register alias '" + def.commandName() + "': a command with that name already exists.");
            return false;
        }

        var pluginCommand = createPluginCommand(name);
        if (pluginCommand == null) return false;

        pluginCommand.setExecutor(executor);
        pluginCommand.setTabCompleter(completer);

        var usage = "/" + def.commandName();
        if (!def.declaredArgs().isEmpty()) {
            usage += " " + String.join(" ", def.declaredArgs().stream().map(a -> "<" + a + ">").toList());
        }
        pluginCommand.setUsage(usage);
        pluginCommand.setDescription("Alias for: " + def.execute());

        commandMap.register(plugin.getName(), pluginCommand);
        registeredCommands.put(name, pluginCommand);

        return true;
    }

    public void unregisterAll() {
        if (commandMap == null) return;

        try {
            var knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            var knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

            for (var name : registeredCommands.keySet()) {
                var lowerName = name.toLowerCase();
                knownCommands.remove(lowerName);
                knownCommands.remove(plugin.getName().toLowerCase() + ":" + lowerName);
            }
            registeredCommands.clear();
        } catch (Exception e) {
            ConsoleLogger.error("Failed to unregister commands: " + e.getMessage());
            if (plugin.getConfig().getBoolean("settings.debug", false)) {
                ConsoleLogger.debug("Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "none"));
            }
        }
    }

    private PluginCommand createPluginCommand(String name) {
        try {
            var constructor = PluginCommand.class.getDeclaredConstructor(String.class, org.bukkit.plugin.Plugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, plugin);
        } catch (Exception e) {
            ConsoleLogger.error("Failed to create PluginCommand for '" + name + "': " + e.getMessage());
            if (plugin.getConfig().getBoolean("settings.debug", false)) {
                ConsoleLogger.debug("Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "none"));
            }
            return null;
        }
    }

    private CommandMap resolveCommandMap() {
        try {
            var server = Bukkit.getServer();
            var field = server.getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return (CommandMap) field.get(server);
        } catch (Exception e) {
            ConsoleLogger.error("Failed to access CommandMap: " + e.getMessage());
            return null;
        }
    }

    public void refreshCommandMap() {
        this.commandMap = resolveCommandMap();
    }
}
