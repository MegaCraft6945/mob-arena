package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final LegionMobArena plugin;

    public PlayerQuitListener(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove player from game if they're in one
        if (plugin.getGameManager().isPlayerInGame(event.getPlayer().getUniqueId())) {
            plugin.getGameManager().onPlayerDeath(event.getPlayer());
        }

        // Cancel arena setup if player is in one
        if (plugin.getAdminMenu() != null && plugin.getAdminMenu().getSetupSession(event.getPlayer()) != null) {
            plugin.getAdminMenu().cancelSetup(event.getPlayer());
        }

        // Save and unload player data
        plugin.getPlayerDataManager().unloadPlayerData(event.getPlayer().getUniqueId());
    }
}
