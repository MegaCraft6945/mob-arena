package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Arena;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private final LegionMobArena plugin;

    public BlockPlaceListener(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        Arena arena = plugin.getArenaManager().getArenaAtLocation(event.getBlock().getLocation());

        if (arena != null) {
            // Check if player is placing a cake
            if (event.getItemInHand().getType() == Material.CAKE ||
                event.getBlockPlaced().getType() == Material.CAKE) {
                // Allow placing cakes ONLY if player is in an active game
                if (plugin.getGameManager().isPlayerInGame(event.getPlayer().getUniqueId())) {
                    // Explicitly allow the placement
                    event.setCancelled(false);
                    // Track the cake location so it can be cleared when game ends
                    plugin.getGameManager().trackPlacedCake(event.getPlayer(), event.getBlock().getLocation());
                    return;
                } else {
                    // Not in game, block the placement
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(plugin.getMessage("cannot-place"));
                    return;
                }
            }

            // For all other blocks, don't allow placing in arena unless player is admin
            if (!event.getPlayer().hasPermission("legion.admin")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.getMessage("cannot-place"));
            }
        }
    }
}
