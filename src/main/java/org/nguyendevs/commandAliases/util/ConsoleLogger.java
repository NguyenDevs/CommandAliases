package org.nguyendevs.commandAliases.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class ConsoleLogger {

    private static final String PREFIX = "&7[&bCommandAliases&7] ";

    private ConsoleLogger() {}

    public static void info(String message) {
        send(PREFIX + "&f" + message);
    }

    public static void warn(String message) {
        send(PREFIX + "&e" + message);
    }

    public static void error(String message) {
        send(PREFIX + "&c" + message);
    }

    public static void debug(String message) {
        send(PREFIX + "&7" + message);
    }

    public static void send(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
