package org.nguyendevs.commandAliases;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import org.nguyendevs.commandAliases.cooldown.CooldownManager;
import org.nguyendevs.commandAliases.command.AdminCommandExecutor;
import org.nguyendevs.commandAliases.config.AliasConfigParser;
import org.nguyendevs.commandAliases.dispatch.CommandDispatcher;
import org.nguyendevs.commandAliases.executor.AliasCommandExecutor;
import org.nguyendevs.commandAliases.executor.AliasTabCompleter;
import org.nguyendevs.commandAliases.model.AliasDefinition;
import org.nguyendevs.commandAliases.model.PluginConfig;
import org.nguyendevs.commandAliases.permission.PermissionManager;
import org.nguyendevs.commandAliases.placeholder.PlaceholderResolver;
import org.nguyendevs.commandAliases.registry.CommandRegistrar;
import org.nguyendevs.commandAliases.util.ConsoleLogger;
import org.nguyendevs.commandAliases.util.LogoPrinter;

import java.util.Collections;
import java.util.Map;

public class CommandAliases extends JavaPlugin {

    private static CommandAliases instance;

    private PluginConfig pluginConfig;
    private AliasConfigParser configParser;
    private CommandRegistrar commandRegistrar;
    private PermissionManager permissionManager;
    private CommandDispatcher dispatcher;
    private PlaceholderResolver placeholderResolver;
    private CooldownManager cooldownManager;
    private AliasCommandExecutor aliasExecutor;
    private AliasTabCompleter aliasCompleter;

    private Map<String, AliasDefinition> aliases = Collections.emptyMap();

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("aliases.yml", false);

        this.pluginConfig = PluginConfig.load(this);
        this.configParser = new AliasConfigParser(this);
        this.commandRegistrar = new CommandRegistrar(this);
        this.permissionManager = new PermissionManager();
        this.cooldownManager = new CooldownManager();
        this.dispatcher = new CommandDispatcher(this);
        this.placeholderResolver = new PlaceholderResolver();
        this.aliasExecutor = new AliasCommandExecutor(this, placeholderResolver);
        this.aliasCompleter = new AliasTabCompleter(this);

        var adminExecutor = new AdminCommandExecutor(this);
        var adminCommand = getCommand("commandaliases");
        if (adminCommand != null) {
            adminCommand.setExecutor(adminExecutor);
            adminCommand.setTabCompleter(adminExecutor);
        }

        loadAliases();
        LogoPrinter.print(this);
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&bCommandAliases&8] &aCommandAliases plugin enabled successfully!"));
    }

    @Override
    public void onDisable() {
        unloadAliases();
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&bCommandAliases&8] &cCommandAliases plugin disabled!"));
    }

    public void loadAliases() {
        unloadAliases();

        var parsed = configParser.parse();
        var loaded = 0;
        var skipped = 0;

        for (var entry : parsed.entrySet()) {
            var def = entry.getValue();

            if (commandRegistrar.isRegistered(def.commandName())) {
                ConsoleLogger.warn("Skipping alias '" + def.configKey() + "' (/"
                    + def.commandName() + "): a command with that name is already registered.");
                skipped++;
                continue;
            }

            permissionManager.register(def, pluginConfig.defaultPermissionLevel());

            if (commandRegistrar.register(def, aliasExecutor, aliasCompleter)) {
                loaded++;
            } else {
                skipped++;
            }
        }

        this.aliases = new java.util.LinkedHashMap<>(parsed);

        ConsoleLogger.info("Loaded " + loaded + " aliases" + (skipped > 0 ? " (" + skipped + " skipped)" : ""));
    }

    public void unloadAliases() {
        permissionManager.unregisterAll();
        commandRegistrar.unregisterAll();
        if (cooldownManager != null) cooldownManager.clearAll();
        this.aliases = Collections.emptyMap();
    }

    public void reload() {
        reloadConfig();
        this.pluginConfig = PluginConfig.load(this);
        loadAliases();
    }

    public Map<String, AliasDefinition> getAliases() {
        return aliases;
    }

    public PluginConfig config() {
        return pluginConfig;
    }

    public CommandDispatcher getDispatcher() {
        return dispatcher;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public static CommandAliases getInstance() {
        return instance;
    }
}
