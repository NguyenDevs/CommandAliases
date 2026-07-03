package org.nguyendevs.commandAliases.cooldown;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public boolean hasCooldown(Player player, String aliasName) {
        var playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return false;

        var expiry = playerCooldowns.get(aliasName.toLowerCase());
        if (expiry == null) return false;

        if (System.currentTimeMillis() >= expiry) {
            playerCooldowns.remove(aliasName.toLowerCase());
            return false;
        }

        return true;
    }

    public int getRemainingSeconds(Player player, String aliasName) {
        var playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return 0;

        var expiry = playerCooldowns.get(aliasName.toLowerCase());
        if (expiry == null) return 0;

        var remaining = (expiry - System.currentTimeMillis()) / 1000;
        return Math.max(0, (int) remaining);
    }

    public void setCooldown(Player player, String aliasName, int seconds) {
        if (seconds <= 0) return;

        var expiry = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
            .put(aliasName.toLowerCase(), expiry);
    }

    public void clearAll() {
        cooldowns.clear();
    }
}
