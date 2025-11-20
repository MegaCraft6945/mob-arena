package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    private final LegionMobArena plugin;

    public EntityDeathListener(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            Player killer = event.getEntity().getKiller();

            if (plugin.getGameManager().isPlayerInGame(killer.getUniqueId())) {
                // Prevent normal drops
                event.getDrops().clear();
                event.setDroppedExp(0);

                // Handle mob kill in game
                plugin.getGameManager().onMobKilled(event.getEntity(), killer);

                // Update player stats
                PlayerData data = plugin.getPlayerDataManager().getPlayerData(killer.getUniqueId());
                if (data != null) {
                    data.addKill();
                }
            }
        }
    }
}
