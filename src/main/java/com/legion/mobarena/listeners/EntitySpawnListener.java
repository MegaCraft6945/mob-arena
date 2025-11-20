package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Arena;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntitySpawnListener implements Listener {

    private final LegionMobArena plugin;

    public EntitySpawnListener(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        Arena arena = plugin.getArenaManager().getArenaAtLocation(entity.getLocation());

        if (arena != null) {
            // Only allow spawning from plugin or spawners
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM &&
                event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER) {
                event.setCancelled(true);
            }
        }
    }
}
