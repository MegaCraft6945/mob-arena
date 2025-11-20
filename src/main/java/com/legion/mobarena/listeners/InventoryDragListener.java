package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryDragListener implements Listener {

    private final LegionMobArena plugin;

    public InventoryDragListener(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();

        // Prevent dragging in all plugin GUIs
        if (title.contains("Select Your Kit") ||
            title.contains("Shop -") ||
            title.contains("Select Arena") ||
            title.contains("Admin Menu") ||
            title.contains("Manage Arenas")) {
            event.setCancelled(true);
        }
    }
}
