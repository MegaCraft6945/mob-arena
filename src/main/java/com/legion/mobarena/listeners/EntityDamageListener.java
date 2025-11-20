package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Arena;
import com.legion.mobarena.models.Game;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    private final LegionMobArena plugin;

    public EntityDamageListener(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        // Check if player is in arena but not in a game
        Arena arena = plugin.getArenaManager().getArenaAtLocation(player.getLocation());
        if (arena != null && !plugin.getGameManager().isPlayerInGame(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Prevent players from damaging each other in the arena
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            if (plugin.getGameManager().isPlayerInGame(damaged.getUniqueId()) &&
                plugin.getGameManager().isPlayerInGame(damager.getUniqueId())) {
                event.setCancelled(true);
            }
        }

        // Prevent mobs outside arena from damaging players in arena
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Entity damager = event.getDamager();

            Game game = plugin.getGameManager().getPlayerGame(player.getUniqueId());
            if (game != null && !game.getArenaMobs().contains(damager)) {
                event.setCancelled(true);
            }
        }
    }
}
