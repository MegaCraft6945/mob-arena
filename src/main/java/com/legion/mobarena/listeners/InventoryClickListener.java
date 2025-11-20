package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.commands.AdminCommand;
import com.legion.mobarena.commands.ArenaCommand;
import com.legion.mobarena.gui.KitSelectionGUI;
import com.legion.mobarena.gui.UpgradeShopGUI;
import com.legion.mobarena.models.Arena;
import com.legion.mobarena.models.Game;
import org.bukkit.Sound;
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
        // Upgrade Shop GUI - Main menu and submenus
        else if (title.contains("Upgrade Shop") || title.contains("Armor Shop") ||
                 title.contains("Weapons Shop") || title.contains("Consumables Shop")) {
            event.setCancelled(true);

            if (clicked == null || !clicked.hasItemMeta()) {
                return;
            }

            Game game = plugin.getGameManager().getPlayerGame(player.getUniqueId());
            if (game == null) {
                return;
            }

            String displayName = clicked.getItemMeta().getDisplayName();

            // Handle category buttons in main shop
            if (title.contains("Upgrade Shop") && !title.contains("Armor") &&
                !title.contains("Weapons") && !title.contains("Consumables")) {
                if (displayName.contains("Armor Upgrades")) {
                    upgradeShopGUI.openArmorShop(player, game);
                } else if (displayName.contains("Weapon Upgrades")) {
                    upgradeShopGUI.openWeaponsShop(player, game);
                } else if (displayName.contains("Consumables")) {
                    upgradeShopGUI.openConsumablesShop(player, game);
                }
            }
            // Handle back button in submenus
            else if (displayName.contains("← Back to Shop")) {
                upgradeShopGUI.openShop(player, game);
            }
            // Handle item purchases/upgrades in submenus
            else {
                upgradeShopGUI.handleClick(player, clicked, game);
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
        else if (title.contains("Admin Menu") || title.contains("Manage Arenas") || title.startsWith("§8§l✦ §e")) {
            event.setCancelled(true);

            // Check if clicked item is null or has no metadata
            if (clicked == null || !clicked.hasItemMeta()) {
                return;
            }

            AdminCommand cmd = (AdminCommand) plugin.getCommand("legionadmin").getExecutor();
            String displayName = clicked.getItemMeta().getDisplayName();

            // Main Admin Menu
            if (title.contains("Legion Admin Menu")) {
                if (displayName.contains("Create New Arena")) {
                    player.closeInventory();
                    player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    player.sendMessage("§a§lCreate Arena");
                    player.sendMessage("§7Type: §e/legionadmin create <arena-name>");
                    player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                } else if (displayName.contains("Manage Arenas")) {
                    cmd.getAdminMenu().openArenaManagement(player);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                }
            }
            // Manage Arenas Menu
            else if (title.contains("Manage Arenas")) {
                if (displayName.contains("← Back")) {
                    cmd.getAdminMenu().openAdminMenu(player);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.8f);
                } else if (!displayName.equals("§c§lNo Arenas Found") && !displayName.equals(" ")) {
                    // Extract arena name (remove "§6§l✦ §e" prefix)
                    String arenaName = displayName.substring(displayName.lastIndexOf("§e") + 2);
                    Arena arena = plugin.getArenaManager().getArena(arenaName);

                    if (arena != null) {
                        if (event.getClick().isLeftClick()) {
                            // Open settings
                            cmd.getAdminMenu().openArenaSettings(player, arena);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                        } else if (event.getClick().isRightClick()) {
                            // Delete arena
                            plugin.getArenaManager().deleteArena(arenaName);
                            player.closeInventory();
                            player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                            player.sendMessage("§c§l✗ Arena Deleted!");
                            player.sendMessage("§7Arena §e" + arenaName + " §7has been deleted!");
                            player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.0f);
                        }
                    }
                }
            }
            // Arena Settings Menu
            else if (title.startsWith("§8§l✦ §e") && !title.contains("Manage") && !title.contains("Admin Menu")) {
                // Extract arena name from title
                String arenaName = title.substring(title.indexOf("§e") + 2, title.lastIndexOf(" §8§l✦"));
                Arena arena = plugin.getArenaManager().getArena(arenaName);

                if (arena != null) {
                    if (displayName.contains("← Back")) {
                        cmd.getAdminMenu().openArenaManagement(player);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.8f);
                    } else if (displayName.contains("Arena Enabled") || displayName.contains("Arena Disabled")) {
                        // Toggle enabled status
                        arena.setEnabled(!arena.isEnabled());
                        plugin.getArenaManager().saveArena(arena);
                        cmd.getAdminMenu().openArenaSettings(player, arena);
                        player.sendMessage("§7Arena §e" + arenaName + " §7is now " +
                                (arena.isEnabled() ? "§a§l✓ enabled" : "§c§l✗ disabled"));
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    } else if (displayName.contains("Teleport to Lobby")) {
                        player.teleport(arena.getLobby());
                        player.closeInventory();
                        player.sendMessage("§d§lTeleported §7to §e" + arenaName + " §7lobby!");
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    } else if (displayName.contains("Delete Arena")) {
                        plugin.getArenaManager().deleteArena(arenaName);
                        player.closeInventory();
                        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        player.sendMessage("§c§l✗ Arena Deleted!");
                        player.sendMessage("§7Arena §e" + arenaName + " §7has been permanently deleted!");
                        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.0f);
                    }
                }
            }
        }
    }

    public UpgradeShopGUI getUpgradeShopGUI() {
        return upgradeShopGUI;
    }
}
