package org.nguyendevs.commandAliases.dispatch;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

class SimpleDispatchStrategy implements DispatchStrategy {

    static final SimpleDispatchStrategy INSTANCE = new SimpleDispatchStrategy();

    @Override
    public boolean dispatch(CommandSender sender, String commandString) {
        try {
            return Bukkit.dispatchCommand(sender, commandString);
        } catch (Exception e) {
            return false;
        }
    }
}
