package org.nguyendevs.commandAliases.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import org.nguyendevs.commandAliases.model.AliasDefinition;
import org.nguyendevs.commandAliases.util.ConsoleLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AliasConfigParser {

    private static final String CONFIG_KEY_COMMAND = "command";
    private static final String CONFIG_KEY_EXECUTE = "execute";
    private static final String CONFIG_KEY_PERMISSION = "permission";
    private static final String CONFIG_KEY_PERMISSION_MESSAGE = "permission-message";
    private static final String CONFIG_KEY_PERMISSION_DEFAULT = "permission-default";
    private static final String ALIASES_SECTION = "aliases";
    private static final List<String> VALID_PERMISSION_DEFAULTS = List.of("true", "false", "op");

    private final JavaPlugin plugin;

    public AliasConfigParser(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Map<String, AliasDefinition> parse() {
        var aliasesFile = new File(plugin.getDataFolder(), "aliases.yml");
        if (!aliasesFile.exists()) {
            ConsoleLogger.warn("aliases.yml not found. Saving default.");
            plugin.saveResource("aliases.yml", false);
        }

        var config = YamlConfiguration.loadConfiguration(aliasesFile);
        var section = config.getConfigurationSection(ALIASES_SECTION);
        if (section == null) {
            ConsoleLogger.info("No aliases defined in aliases.yml");
            return Collections.emptyMap();
        }

        var definitions = new LinkedHashMap<String, AliasDefinition>();
        var keys = section.getKeys(false);

        for (var key : keys) {
            var entry = section.getConfigurationSection(key);
            if (entry == null) continue;

            try {
                var def = parseEntry(key, entry);
                if (def != null) {
                    definitions.put(def.commandName().toLowerCase(), def);
                }
            } catch (Exception e) {
                ConsoleLogger.warn("Failed to parse alias '" + key + "': " + e.getMessage());
                if (plugin.getConfig().getBoolean("settings.debug", false) && e.getCause() != null) {
                    ConsoleLogger.debug("Cause: " + e.getCause().getMessage());
                }
            }
        }

        return definitions;
    }

    private AliasDefinition parseEntry(String configKey, ConfigurationSection entry) {
        var command = entry.getString(CONFIG_KEY_COMMAND);
        var execute = entry.getString(CONFIG_KEY_EXECUTE);

        if (command == null || command.isBlank()) {
            ConsoleLogger.warn("Alias '" + configKey + "' is missing a 'command' field. Skipping.");
            return null;
        }
        if (execute == null || execute.isBlank()) {
            ConsoleLogger.warn("Alias '" + configKey + "' is missing an 'execute' field. Skipping.");
            return null;
        }

        command = command.trim();
        execute = execute.trim();

        var parts = command.split("\\s+");
        var commandName = parts[0];
        var declaredArgs = new ArrayList<String>();

        for (int i = 1; i < parts.length; i++) {
            var arg = parts[i];
            if (arg.startsWith("<") && arg.endsWith(">")) {
                var argName = arg.substring(1, arg.length() - 1);
                if (argName.isEmpty()) {
                    ConsoleLogger.warn("Alias '" + configKey + "' has an empty argument declaration '" + arg + "'. Skipping.");
                    return null;
                }
                declaredArgs.add(argName);
            } else {
                ConsoleLogger.warn("Alias '" + configKey + "' has invalid argument declaration '" + arg + "'. Expected <name>. Skipping.");
                return null;
            }
        }

        for (var argName : declaredArgs) {
            var placeholder = "%" + argName + "%";
            if (!execute.contains(placeholder)) {
                ConsoleLogger.warn("Alias '" + configKey + "' declares argument '<" + argName + ">' but execute string does not contain '" + placeholder + "'. This may cause unexpected behavior.");
            }
        }

        var permission = entry.getString(CONFIG_KEY_PERMISSION);
        var permissionMessage = entry.getString(CONFIG_KEY_PERMISSION_MESSAGE);
        var permissionDefault = entry.getString(CONFIG_KEY_PERMISSION_DEFAULT);

        if (permissionDefault != null) {
            var lower = permissionDefault.toLowerCase();
            if (!VALID_PERMISSION_DEFAULTS.contains(lower)) {
                ConsoleLogger.warn("Alias '" + configKey + "' has invalid permission-default '" + permissionDefault + "'. Expected one of: true, false, op. Using 'op'.");
                permissionDefault = null;
            } else {
                permissionDefault = lower;
            }
        }

        if (permission != null && permission.isEmpty()) {
            permission = null;
        }
        if (permissionMessage != null && permissionMessage.isEmpty()) {
            permissionMessage = null;
        }

        return new AliasDefinition(
            configKey, command, execute, commandName,
            Collections.unmodifiableList(declaredArgs),
            permission, permissionMessage, permissionDefault
        );
    }
}
