package org.nguyendevs.commandAliases.model;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public record PluginConfig(
    boolean debug,
    Messages messages,
    String defaultPermissionLevel
) {
    public static PluginConfig load(JavaPlugin plugin) {
        var config = plugin.getConfig();
        var settings = config.getConfigurationSection("settings");
        var msgs = config.getConfigurationSection("messages");
        var perms = config.getConfigurationSection("permissions");

        boolean debug = false;
        if (settings != null) {
            debug = settings.getBoolean("debug", false);
        }

        var prefix = getMessage(msgs, "prefix", "&8[&bCAL&8]");
        var messages = new Messages(
            prefix,
            getMessage(msgs, "no-permission", "&cYou do not have permission to use this command."),
            getMessage(msgs, "invalid-arguments", "&cMissing arguments. Usage: &e/%command%"),
            getMessage(msgs, "command-not-found", "&cNo target command found for this alias. Please contact an admin."),
            getMessage(msgs, "plugin-not-found", "&cPlugin &e%plugin% &cdoes not exist or is not loaded."),
            getMessage(msgs, "reload-success", "&aReloaded successfully. (%count% aliases loaded)"),
            getMessage(msgs, "reload-failed", "&cReload failed, check console for details.")
        );

        String defaultPermLevel = "op";
        if (perms != null) {
            defaultPermLevel = perms.getString("default-level", "op");
        }

        return new PluginConfig(debug, messages, defaultPermLevel);
    }

    private static String getMessage(ConfigurationSection section, String key, String defaultValue) {
        if (section == null) return defaultValue;
        return section.getString(key, defaultValue);
    }

    private static String prefixed(String prefix, String message) {
        return prefix + " " + message;
    }

    public record Messages(
        String prefix,
        String noPermission,
        String invalidArguments,
        String commandNotFound,
        String pluginNotFound,
        String reloadSuccess,
        String reloadFailed
    ) {
        public Messages(String prefix, String noPermission, String invalidArguments,
                        String commandNotFound, String pluginNotFound,
                        String reloadSuccess, String reloadFailed) {
            this.prefix = prefix;
            this.noPermission = prefixed(prefix, noPermission);
            this.invalidArguments = prefixed(prefix, invalidArguments);
            this.commandNotFound = prefixed(prefix, commandNotFound);
            this.pluginNotFound = prefixed(prefix, pluginNotFound);
            this.reloadSuccess = prefixed(prefix, reloadSuccess);
            this.reloadFailed = prefixed(prefix, reloadFailed);
        }
    }
}
