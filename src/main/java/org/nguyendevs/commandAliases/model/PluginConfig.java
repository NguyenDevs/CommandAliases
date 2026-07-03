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

        var messages = new Messages(
            getMessage(msgs, "no-permission", "&cYou do not have permission to use this command."),
            getMessage(msgs, "invalid-arguments", "&cMissing arguments. Usage: &e/%command%"),
            getMessage(msgs, "command-not-found", "&cNo target command found for this alias. Please contact an admin."),
            getMessage(msgs, "plugin-not-found", "&cPlugin &e%plugin% &cdoes not exist or is not loaded."),
            getMessage(msgs, "reload-success", "&aCommandAliases reloaded successfully. (%count% aliases loaded)"),
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

    public record Messages(
        String noPermission,
        String invalidArguments,
        String commandNotFound,
        String pluginNotFound,
        String reloadSuccess,
        String reloadFailed
    ) {}
}
