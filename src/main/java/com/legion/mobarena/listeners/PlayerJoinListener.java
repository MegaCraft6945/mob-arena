package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final LegionMobArena plugin;

    public PlayerJoinListener(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Load player data
        plugin.getPlayerDataManager().loadPlayerData(event.getPlayer());
    }
}
