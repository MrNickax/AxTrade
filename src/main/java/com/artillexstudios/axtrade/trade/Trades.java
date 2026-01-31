package com.artillexstudios.axtrade.trade;

import com.artillexstudios.axtrade.utils.SoundUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.artillexstudios.axtrade.AxTrade.MESSAGEUTILS;

public class Trades {

    /**
     * Folia-safe: constant-time lookup instead of iterating a shared list on region threads.
     * Each trade is stored twice (for both participants).
     */
    private static final Map<UUID, Trade> tradesByPlayer = new ConcurrentHashMap<>();

    public static void addTrade(Player p1, Player p2) {
        Trade trade = new Trade(p1, p2);

        tradesByPlayer.put(p1.getUniqueId(), trade);
        tradesByPlayer.put(p2.getUniqueId(), trade);

        MESSAGEUTILS.sendLang(p1, "trade.started", Map.of("%player%", p2.getName()));
        MESSAGEUTILS.sendLang(p2, "trade.started", Map.of("%player%", p1.getName()));
        SoundUtils.playSound(p1, "started");
        SoundUtils.playSound(p2, "started");
    }

    public static void removeTrade(Trade trade) {
        // Remove both sides safely; protected fields are accessible within the same package.
        UUID p1 = trade.player1.getPlayer().getUniqueId();
        UUID p2 = trade.player2.getPlayer().getUniqueId();

        tradesByPlayer.remove(p1, trade);
        tradesByPlayer.remove(p2, trade);
    }

    /**
     * Returns a snapshot list of active trades (unique).
     * Do not mutate the returned list.
     */
    public static List<Trade> getTrades() {
        return new ArrayList<>(new HashSet<>(tradesByPlayer.values()));
    }

    public static boolean isTrading(Player player) {
        return tradesByPlayer.containsKey(player.getUniqueId());
    }

    @Nullable
    public static Trade getTrade(Player player) {
        return tradesByPlayer.get(player.getUniqueId());
    }
}
