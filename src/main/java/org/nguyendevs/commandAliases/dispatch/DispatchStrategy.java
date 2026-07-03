package org.nguyendevs.commandAliases.dispatch;

import org.bukkit.command.CommandSender;

@FunctionalInterface
interface DispatchStrategy {
    boolean dispatch(CommandSender sender, String commandString);
}
