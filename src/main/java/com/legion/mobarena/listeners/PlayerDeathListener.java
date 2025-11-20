package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final LegionMobArena plugin;

    public PlayerDeathListener(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (plugin.getGameManager().isPlayerInGame(event.getEntity().getUniqueId())) {
            event.setKeepInventory(false);
            event.getDrops().clear();
            event.setDroppedExp(0);

            plugin.getGameManager().onPlayerDeath(event.getEntity());
            event.getEntity().sendMessage("§c§lYou died!");
        }
    }
}
