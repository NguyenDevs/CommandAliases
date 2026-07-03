package org.nguyendevs.commandAliases.executor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.nguyendevs.commandAliases.CommandAliases;
import org.nguyendevs.commandAliases.dispatch.CommandDispatcher;
import org.nguyendevs.commandAliases.model.AliasDefinition;
import org.nguyendevs.commandAliases.placeholder.PlaceholderResolver;
import org.nguyendevs.commandAliases.util.SoundUtil;

import java.util.HashMap;

public class AliasCommandExecutor implements CommandExecutor {

    private static final String PLACEHOLDER_EXECUTER = "%executer%";

    private final CommandAliases plugin;
    private final PlaceholderResolver resolver;

    public AliasCommandExecutor(CommandAliases plugin, PlaceholderResolver resolver) {
        this.plugin = plugin;
        this.resolver = resolver;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        var def = plugin.getAliases().get(label.toLowerCase());
        if (def == null) return false;

        if (!checkPermission(sender, def)) return true;

        if (args.length < def.declaredArgs().size()) {
            var usage = buildUsage(def);
            var msg = plugin.config().messages().invalidArguments().replace("%command%", usage);
            SoundUtil.playError(sender);
            sender.sendMessage(CommandDispatcher.colorize(msg));
            return true;
        }

        var placeholders = new HashMap<String, String>();
        placeholders.put(PLACEHOLDER_EXECUTER, sender.getName());

        for (int i = 0; i < def.declaredArgs().size(); i++) {
            placeholders.put("%" + def.declaredArgs().get(i) + "%", args[i]);
        }

        var resolved = resolver.resolve(def.execute(), placeholders);

        if (plugin.config().debug()) {
            plugin.getLogger().info("[Debug] Alias '/" + label + "' resolved to: " + resolved);
        }

        plugin.getDispatcher().dispatch(sender, resolved);

        if (def.sound() != null) {
            SoundUtil.play(sender, def.sound(), def.soundPitch(), def.soundVolume());
        }

        return true;
    }

    private boolean checkPermission(CommandSender sender, AliasDefinition def) {
        if (def.permission() == null) return true;
        if (sender.hasPermission(def.permission())) return true;

        var msg = def.permissionMessage();
        if (msg == null || msg.isBlank()) {
            msg = plugin.config().messages().noPermission();
        }
        SoundUtil.playError(sender);
        sender.sendMessage(CommandDispatcher.colorize(msg));
        return false;
    }

    private static String buildUsage(AliasDefinition def) {
        var usage = "/" + def.commandName();
        if (!def.declaredArgs().isEmpty()) {
            usage += " " + String.join(" ", def.declaredArgs().stream().map(a -> "<" + a + ">").toList());
        }
        return usage;
    }
}
