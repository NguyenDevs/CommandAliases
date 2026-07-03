package org.nguyendevs.commandAliases.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SoundUtil {

    private SoundUtil() {}

    public static void playError(CommandSender sender) {
        if (sender instanceof Player player) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.MASTER, 1.0f, 0.5f);
        }
    }

    public static void playSuccess(CommandSender sender) {
        if (sender instanceof Player player) {
            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.MASTER, 1.0f, 1.5f);
        }
    }

    public static void play(CommandSender sender, String soundName, double pitch, double volume) {
        if (!(sender instanceof Player player)) return;
        try {
            var sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, SoundCategory.MASTER, (float) volume, (float) pitch);
        } catch (IllegalArgumentException ignored) {}
    }
}
