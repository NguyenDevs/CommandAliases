package org.nguyendevs.commandAliases.executor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import org.nguyendevs.commandAliases.CommandAliases;
import org.nguyendevs.commandAliases.model.AliasDefinition;

import java.util.List;

public class AliasTabCompleter implements TabCompleter {

    private static final String ARG_PLAYER = "player";
    private static final String ARG_AMOUNT = "amount";
    private static final List<String> SUGGESTED_AMOUNTS = List.of("1", "2", "4", "8", "16", "32", "64", "128");

    private final CommandAliases plugin;

    public AliasTabCompleter(CommandAliases plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        var def = plugin.getAliases().get(label.toLowerCase());
        if (def == null) return List.of();

        if (!checkPermission(sender, def)) return List.of();

        var argIndex = args.length - 1;

        if (argIndex < def.declaredArgs().size()) {
            var argName = def.declaredArgs().get(argIndex);
            return suggestFor(argName, args[argIndex]);
        }

        return List.of();
    }

    private List<String> suggestFor(String argName, String partial) {
        if (argName.equalsIgnoreCase(ARG_PLAYER)) {
            return suggestPlayers(partial);
        }
        if (argName.equalsIgnoreCase(ARG_AMOUNT)) {
            return suggestAmounts(partial);
        }
        return List.of(argName);
    }

    private List<String> suggestAmounts(String partial) {
        return SUGGESTED_AMOUNTS.stream()
            .filter(a -> a.startsWith(partial))
            .toList();
    }

    private List<String> suggestPlayers(String partial) {
        var lower = partial.toLowerCase();
        return Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .filter(name -> name.toLowerCase().startsWith(lower))
            .sorted()
            .toList();
    }

    private boolean checkPermission(CommandSender sender, AliasDefinition def) {
        if (def.permission() == null) return true;
        return sender.hasPermission(def.permission());
    }
}
