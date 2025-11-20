package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.gui.AdminMenuGUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AdminBlockClickListener implements Listener {

    private final LegionMobArena plugin;

    public AdminBlockClickListener(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Check if player has admin permission
        if (!player.hasPermission("legion.admin")) {
            return;
        }

        // Check if player is in an arena setup session
        AdminMenuGUI.ArenaSetupSession session = plugin.getAdminMenu().getSetupSession(player);
        if (session == null) {
            return;
        }

        // Only handle left and right clicks on blocks
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Get the clicked block location
        if (event.getClickedBlock() == null) {
            return;
        }

        Location blockLoc = event.getClickedBlock().getLocation();

        // Check if player is holding the selection tool (stick)
        if (player.getInventory().getItemInMainHand().getType() != Material.STICK) {
            return;
        }

        // Cancel the event to prevent block breaking/placing
        event.setCancelled(true);

        // Left click = Position 1
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            session.pos1 = blockLoc;

            // Visual feedback
            player.playSound(blockLoc, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
            player.spawnParticle(Particle.VILLAGER_HAPPY, blockLoc.add(0.5, 0.5, 0.5), 20, 0.3, 0.3, 0.3);

            player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage("§a✓ Position 1 Set!");
            player.sendMessage("§7Location: §e" + blockLoc.getBlockX() + "§7, §e" + blockLoc.getBlockY() + "§7, §e" + blockLoc.getBlockZ());

            if (session.pos2 == null) {
                player.sendMessage("§7Now §eright-click §7a block to set position 2");
            } else {
                player.sendMessage("§7Position 2: §aAlready set");
                player.sendMessage("§7Next: §e/legionadmin setlobby");
            }
            player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
        // Right click = Position 2
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (session.pos1 == null) {
                player.sendMessage("§c§l⚠ §cYou must set position 1 first!");
                player.sendMessage("§7Use §eleft-click §7to set position 1");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }

            session.pos2 = blockLoc;

            // Visual feedback
            player.playSound(blockLoc, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
            player.spawnParticle(Particle.VILLAGER_HAPPY, blockLoc.add(0.5, 0.5, 0.5), 20, 0.3, 0.3, 0.3);

            player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage("§a✓ Position 2 Set!");
            player.sendMessage("§7Location: §e" + blockLoc.getBlockX() + "§7, §e" + blockLoc.getBlockY() + "§7, §e" + blockLoc.getBlockZ());
            player.sendMessage("");
            player.sendMessage("§7Arena boundaries configured!");
            player.sendMessage("§7Next: §e/legionadmin setlobby");
            player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }
}
