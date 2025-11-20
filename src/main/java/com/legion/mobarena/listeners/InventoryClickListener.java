package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.commands.AdminCommand;
import com.legion.mobarena.commands.ArenaCommand;
import com.legion.mobarena.gui.KitSelectionGUI;
import com.legion.mobarena.gui.UpgradeShopGUI;
import com.legion.mobarena.models.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private final LegionMobArena plugin;
    private UpgradeShopGUI upgradeShopGUI;
    private KitSelectionGUI kitSelectionGUI;

    public InventoryClickListener(LegionMobArena plugin) {
        this.plugin = plugin;
        this.upgradeShopGUI = new UpgradeShopGUI(plugin);
        this.kitSelectionGUI = new KitSelectionGUI(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();

        // Kit Selection GUI
        if (title.contains("Select Your Kit")) {
            event.setCancelled(true);
            if (clicked != null && clicked.hasItemMeta()) {
                kitSelectionGUI.handleClick(player, clicked);
            }
            return;
        }
        // Upgrade Shop GUI - Fixed title check to match new format "Shop - X Gold"
        else if (title.contains("Shop -")) {
            event.setCancelled(true);
            if (clicked != null && clicked.hasItemMeta()) {
                Game game = plugin.getGameManager().getPlayerGame(player.getUniqueId());
                if (game != null) {
                    upgradeShopGUI.handleClick(player, clicked, game);
                }
            }
            return;
        }
        // Arena Selection GUI
        else if (title.contains("Select Arena")) {
            event.setCancelled(true);
            ArenaCommand cmd = (ArenaCommand) plugin.getCommand("legion").getExecutor();
            cmd.getArenaSelection().handleClick(player, clicked);
        }
        // Admin Menu GUIs
        else if (title.contains("Admin Menu") || title.contains("Manage Arenas")) {
            event.setCancelled(true);
            AdminCommand cmd = (AdminCommand) plugin.getCommand("legionadmin").getExecutor();

            if (title.contains("Admin Menu")) {
                String displayName = clicked.getItemMeta().getDisplayName();
                if (displayName.contains("Create Arena")) {
                    player.closeInventory();
                    player.sendMessage("§7Type: §e/legionadmin create <arena-name>");
                } else if (displayName.contains("Manage Arenas")) {
                    cmd.getAdminMenu().openArenaManagement(player);
                }
            } else if (title.contains("Manage Arenas")) {
                // Handle arena management clicks
                String arenaName = clicked.getItemMeta().getDisplayName().substring(2);

                if (event.getClick().isLeftClick()) {
                    // Toggle enable/disable
                    var arena = plugin.getArenaManager().getArena(arenaName);
                    if (arena != null) {
                        arena.setEnabled(!arena.isEnabled());
                        player.sendMessage("§7Arena §e" + arenaName + " §7is now " +
                                (arena.isEnabled() ? "§aenabled" : "§cdisabled"));
                        cmd.getAdminMenu().openArenaManagement(player);
                    }
                } else if (event.getClick().isRightClick()) {
                    // Delete arena
                    plugin.getArenaManager().deleteArena(arenaName);
                    player.sendMessage("§cArena §e" + arenaName + " §cdeleted!");
                    cmd.getAdminMenu().openArenaManagement(player);
                }
            }
        }
    }

    public UpgradeShopGUI getUpgradeShopGUI() {
        return upgradeShopGUI;
    }
}
