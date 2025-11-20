package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Arena;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final LegionMobArena plugin;

    public BlockBreakListener(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Arena arena = plugin.getArenaManager().getArenaAtLocation(event.getBlock().getLocation());

        if (arena != null) {
            // Allow eating/breaking cakes if player is in game
            if (event.getBlock().getType() == Material.CAKE) {
                if (plugin.getGameManager().isPlayerInGame(event.getPlayer().getUniqueId())) {
                    return; // Allow cake interaction
                }
            }

            // Don't allow breaking blocks in arena unless player is admin
            if (!event.getPlayer().hasPermission("legion.admin")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.getConfig().getString("messages.arena-protected"));
            }
        }
    }
}
