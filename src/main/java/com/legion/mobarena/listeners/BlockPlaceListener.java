package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Arena;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private final LegionMobArena plugin;

    public BlockPlaceListener(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Arena arena = plugin.getArenaManager().getArenaAtLocation(event.getBlock().getLocation());

        if (arena != null) {
            // Allow placing cakes if player is in game
            if (event.getBlockPlaced().getType() == Material.CAKE ||
                event.getItemInHand().getType() == Material.CAKE) {
                if (plugin.getGameManager().isPlayerInGame(event.getPlayer().getUniqueId())) {
                    // Track the cake location so it can be cleared when game ends
                    plugin.getGameManager().trackPlacedCake(event.getPlayer(), event.getBlock().getLocation());
                    return; // Allow cake placement
                }
            }

            // Don't allow placing blocks in arena unless player is admin
            if (!event.getPlayer().hasPermission("legion.admin")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.getConfig().getString("messages.arena-protected"));
            }
        }
    }
}
