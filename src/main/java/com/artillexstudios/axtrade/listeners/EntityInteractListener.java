package com.artillexstudios.axtrade.listeners;

import com.artillexstudios.axtrade.request.Requests;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.artillexstudios.axtrade.AxTrade.CONFIG;

public class EntityInteractListener implements Listener {

    private static final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    @EventHandler (ignoreCancelled = true)
    public void onInteract(@NotNull PlayerInteractEntityEvent event) {
        if (!CONFIG.getBoolean("shift-click-send-request", true)) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("axtrade.trade")) return;
        if (hasCooldown(player)) return;
        if (!player.isSneaking()) return;
        if (!(event.getRightClicked() instanceof Player sendTo)) return;
        if (!sendTo.isOnline()) return;

        addCooldown(player);
        Requests.addRequest(player, sendTo);
        event.setCancelled(true);
    }

    private boolean hasCooldown(Player player) {
        Long expireTime = cooldowns.get(player.getUniqueId());
        if (expireTime == null) return false;

        if (System.currentTimeMillis() > expireTime) {
            cooldowns.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    private void addCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 100L);
    }
}
