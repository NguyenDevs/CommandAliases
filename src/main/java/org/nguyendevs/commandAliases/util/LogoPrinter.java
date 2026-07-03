package org.nguyendevs.commandAliases.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class LogoPrinter {

    private LogoPrinter() {}

    public static void print(JavaPlugin plugin) {
        var s = Bukkit.getConsoleSender();
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b ██████╗ ██████╗ ███╗   ███╗███╗   ███╗ █████╗ ███╗   ██╗██████╗ "));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b██╔════╝██╔═══██╗████╗ ████║████╗ ████║██╔══██╗████╗  ██║██╔══██╗"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b██║     ██║   ██║██╔████╔██║██╔████╔██║███████║██╔██╗ ██║██║  ██║"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b██║     ██║   ██║██║╚██╔╝██║██║╚██╔╝██║██╔══██║██║╚██╗██║██║  ██║"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b╚██████╗╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║██║  ██║██║ ╚████║██████╔╝"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═════╝ "));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3 █████╗ ██╗     ██╗ █████╗ ███████╗███████╗███████╗"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3██╔══██╗██║     ██║██╔══██╗██╔════╝██╔════╝██╔════╝"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3███████║██║     ██║███████║███████╗█████╗  ███████╗"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3██╔══██║██║     ██║██╔══██║╚════██║██╔══╝  ╚════██║"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3██║  ██║███████╗██║██║  ██║███████║███████╗███████║"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3╚═╝  ╚═╝╚══════╝╚═╝╚═╝  ╚═╝╚══════╝╚══════╝╚══════╝"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b         Version " + plugin.getDescription().getVersion()));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3         Development by NguyenDevs"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
    }
}