package org.nguyendevs.commandAliases.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import org.nguyendevs.commandAliases.CommandAliases;
import org.nguyendevs.commandAliases.dispatch.CommandDispatcher;
import org.nguyendevs.commandAliases.util.ConsoleLogger;
import org.nguyendevs.commandAliases.util.SoundUtil;

import java.util.List;

public class AdminCommandExecutor implements CommandExecutor, TabCompleter {

    private static final String SUB_RELOAD = "reload";
    private static final String SUB_LIST = "list";
    private static final String ADMIN_PERMISSION = "commandaliases.admin";
    private static final List<String> SUBCOMMANDS = List.of(SUB_RELOAD, SUB_LIST);

    private final CommandAliases plugin;

    public AdminCommandExecutor(CommandAliases plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(ADMIN_PERMISSION)) {
            SoundUtil.playError(sender);
            sender.sendMessage(CommandDispatcher.colorize(plugin.config().messages().noPermission()));
            return true;
        }

        if (args.length < 1) {
            showHelp(sender, label);
            return true;
        }

        var sub = args[0].toLowerCase();
        return switch (sub) {
            case SUB_RELOAD -> handleReload(sender);
            case SUB_LIST -> handleList(sender);
            default -> {
                showHelp(sender, label);
                yield true;
            }
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(ADMIN_PERMISSION)) return List.of();

        if (args.length == 1) {
            var partial = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                .filter(s -> s.startsWith(partial))
                .toList();
        }

        return List.of();
    }

    private void showHelp(CommandSender sender, String label) {
        sender.sendMessage(CommandDispatcher.colorize("&6&l=== CommandAliases Help ==="));
        sender.sendMessage(CommandDispatcher.colorize(""));
        sender.sendMessage(CommandDispatcher.colorize("&6/" + label + " reload"));
        sender.sendMessage(CommandDispatcher.colorize("  &8- &7Reload all aliases from aliases.yml"));
        sender.sendMessage(CommandDispatcher.colorize(""));
        sender.sendMessage(CommandDispatcher.colorize("&6/" + label + " list"));
        sender.sendMessage(CommandDispatcher.colorize("  &8- &7Show all loaded aliases"));
    }

    private boolean handleReload(CommandSender sender) {
        try {
            plugin.reload();
            var count = plugin.getAliases().size();
            var msg = plugin.config().messages().reloadSuccess().replace("%count%", String.valueOf(count));
            sender.sendMessage(CommandDispatcher.colorize(msg));
            SoundUtil.playSuccess(sender);
            ConsoleLogger.info("Reloaded by " + sender.getName() + ". " + count + " aliases loaded.");
        } catch (Exception e) {
            var msg = plugin.config().messages().reloadFailed();
            sender.sendMessage(CommandDispatcher.colorize(msg));
            SoundUtil.playError(sender);
            ConsoleLogger.error("Reload failed: " + e.getMessage());
        }
        return true;
    }

    private boolean handleList(CommandSender sender) {
        var aliases = plugin.getAliases();
        if (aliases.isEmpty()) {
            sender.sendMessage(CommandDispatcher.colorize("&eNo aliases loaded."));
            return true;
        }

        sender.sendMessage(CommandDispatcher.colorize("&6&l=== Loaded Aliases (" + aliases.size() + ") ==="));
        sender.sendMessage(CommandDispatcher.colorize(""));

        var index = 1;
        for (var def : aliases.values()) {
            var line = "&6" + index + ". &f/" + def.commandName();
            if (!def.declaredArgs().isEmpty()) {
                line += " &7" + String.join(" ", def.declaredArgs().stream().map(a -> "<" + a + ">").toList());
            }
            line += " &8\u2192 &f" + def.execute();
            sender.sendMessage(CommandDispatcher.colorize(line));

            var details = "";
            if (def.permission() != null) {
                details += " &8Perm: &f" + def.permission();
            }
            if (!def.declaredArgs().isEmpty()) {
                var usage = "/" + def.commandName() + " " + String.join(" ", def.declaredArgs().stream().map(a -> "<" + a + ">").toList());
                if (!details.isEmpty()) details += " &8|";
                details += " &8Usage: &f" + usage;
            }
            if (!details.isEmpty()) {
                sender.sendMessage(CommandDispatcher.colorize("    " + details.trim()));
            }

            sender.sendMessage(CommandDispatcher.colorize(""));
            index++;
        }
        return true;
    }
}
