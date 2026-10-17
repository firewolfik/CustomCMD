package xd.firewoflik.cc.manager;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final Map<String, Map<UUID, Long>> cooldowns = new ConcurrentHashMap<>();

    public boolean hasCooldown(String commandId, Player player) {
        Map<UUID, Long> userCooldowns = cooldowns.get(commandId);
        if (userCooldowns == null) return false;

        Long expireAt = userCooldowns.get(player.getUniqueId());
        if (expireAt == null) return false;

        if (System.currentTimeMillis() >= expireAt) {
            userCooldowns.remove(player.getUniqueId());
            return false;
        }

        return true;
    }

    public long getRemainingSeconds(String commandId, Player player) {
        Map<UUID, Long> userCooldowns = cooldowns.get(commandId);
        if (userCooldowns == null) return 0;

        Long expireAt = userCooldowns.get(player.getUniqueId());
        if (expireAt == null) return 0;

        long diff = expireAt - System.currentTimeMillis();
        return Math.max(0, (diff + 999) / 1000);
    }

    public void setCooldown(String commandId, Player player, int seconds) {
        if (seconds <= 0) return;
        cooldowns.computeIfAbsent(commandId, k -> new ConcurrentHashMap<>())
                 .put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public void clear() {
        cooldowns.clear();
    }
}
