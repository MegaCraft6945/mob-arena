package com.legion.mobarena.gui;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AdminMenuGUI {

    private final LegionMobArena plugin;
    private final Map<Player, ArenaSetupSession> setupSessions;

    public AdminMenuGUI(LegionMobArena plugin) {
        this.plugin = plugin;
        this.setupSessions = new HashMap<>();
    }

    public void openAdminMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, "§8§l✦ §6§lLegion Admin Menu §8§l✦");

        // Add decorative border
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);

        // Set border
        for (int i = 0; i < 45; i++) {
            if (i < 9 || i >= 36 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border);
            }
        }

        // Create Arena
        ItemStack createArena = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta createMeta = createArena.getItemMeta();
        createMeta.setDisplayName("§a§l✦ Create New Arena");
        createMeta.setLore(Arrays.asList(
                "",
                "§7Start the arena creation wizard",
                "",
                "§e§lFeatures:",
                "§7• §eClick-based §7position selection",
                "§7• §eInteractive §7setup process",
                "§7• §eVisual §7feedback and particles",
                "",
                "§a§l» Click to start creating!"
        ));
        createArena.setItemMeta(createMeta);
        inv.setItem(20, createArena);

        // Manage Arenas
        ItemStack manageArenas = new ItemStack(Material.BOOKSHELF);
        ItemMeta manageMeta = manageArenas.getItemMeta();
        manageMeta.setDisplayName("§e§l✦ Manage Arenas");
        manageMeta.setLore(Arrays.asList(
                "",
                "§7View and manage existing arenas",
                "",
                "§e§lActions Available:",
                "§7• §eView §7all arenas",
                "§7• §eToggle §7enabled/disabled",
                "§7• §eEdit §7arena settings",
                "§7• §eDelete §7arenas",
                "",
                "§e§l» Click to manage!"
        ));
        manageArenas.setItemMeta(manageMeta);
        inv.setItem(22, manageArenas);

        // Plugin Info
        ItemStack info = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§b§l✦ Plugin Information");
        int arenaCount = plugin.getArenaManager().getAllArenas().size();
        infoMeta.setLore(Arrays.asList(
                "",
                "§7Total Arenas: §e" + arenaCount,
                "§7Version: §e1.0.0",
                "",
                "§7Use §e/legionadmin help §7for commands"
        ));
        info.setItemMeta(infoMeta);
        inv.setItem(24, info);

        player.openInventory(inv);
    }

    public void openArenaManagement(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8§l✦ §e§lManage Arenas §8§l✦");

        Collection<Arena> arenas = plugin.getArenaManager().getAllArenas();

        if (arenas.isEmpty()) {
            ItemStack noArenas = new ItemStack(Material.BARRIER);
            ItemMeta noMeta = noArenas.getItemMeta();
            noMeta.setDisplayName("§c§lNo Arenas Found");
            noMeta.setLore(Arrays.asList(
                    "",
                    "§7You haven't created any arenas yet!",
                    "",
                    "§7Use §e/legionadmin create <name>",
                    "§7or the §aCreate Arena §7button to get started"
            ));
            noArenas.setItemMeta(noMeta);
            inv.setItem(22, noArenas);
        } else {
            int slot = 10;
            for (Arena arena : arenas) {
                ItemStack item = new ItemStack(arena.isEnabled() ? Material.LIME_CONCRETE : Material.RED_CONCRETE);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§6§l✦ §e" + arena.getName());
                meta.setLore(Arrays.asList(
                        "",
                        "§7World: §f" + arena.getWorld().getName(),
                        "§7Status: " + (arena.isEnabled() ? "§a§l✓ Enabled" : "§c§l✗ Disabled"),
                        "",
                        "§e§lLeft Click: §7Open settings",
                        "§c§lRight Click: §7Delete arena",
                        ""
                ));
                item.setItemMeta(meta);
                inv.setItem(slot++, item);

                // Skip border positions
                if (slot % 9 == 8) slot += 2;
                if (slot >= 44) break;
            }
        }

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§l← Back");
        backMeta.setLore(Arrays.asList("§7Return to admin menu"));
        back.setItemMeta(backMeta);
        inv.setItem(49, back);

        player.openInventory(inv);
    }

    public void openArenaSettings(Player player, Arena arena) {
        Inventory inv = Bukkit.createInventory(null, 45, "§8§l✦ §e" + arena.getName() + " §8§l✦");

        // Add decorative border
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);

        for (int i = 0; i < 45; i++) {
            if (i < 9 || i >= 36 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border);
            }
        }

        // Arena Info
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§b§l✦ Arena Information");
        infoMeta.setLore(Arrays.asList(
                "",
                "§7Name: §e" + arena.getName(),
                "§7World: §e" + arena.getWorld().getName(),
                "§7Status: " + (arena.isEnabled() ? "§a§l✓ Enabled" : "§c§l✗ Disabled"),
                "",
                "§7Position 1: §e" + arena.getMin().getBlockX() + "§7, §e" + arena.getMin().getBlockY() + "§7, §e" + arena.getMin().getBlockZ(),
                "§7Position 2: §e" + arena.getMax().getBlockX() + "§7, §e" + arena.getMax().getBlockY() + "§7, §e" + arena.getMax().getBlockZ(),
                "§7Lobby: §e" + arena.getLobby().getBlockX() + "§7, §e" + arena.getLobby().getBlockY() + "§7, §e" + arena.getLobby().getBlockZ(),
                ""
        ));
        info.setItemMeta(infoMeta);
        inv.setItem(13, info);

        // Toggle Enabled
        ItemStack toggle = new ItemStack(arena.isEnabled() ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta toggleMeta = toggle.getItemMeta();
        toggleMeta.setDisplayName(arena.isEnabled() ? "§a§l✓ Arena Enabled" : "§7§l✗ Arena Disabled");
        toggleMeta.setLore(Arrays.asList(
                "",
                "§7Currently: " + (arena.isEnabled() ? "§aEnabled" : "§cDisabled"),
                "",
                arena.isEnabled()
                    ? "§7Click to §cdisable §7this arena"
                    : "§7Click to §aenable §7this arena",
                ""
        ));
        toggle.setItemMeta(toggleMeta);
        inv.setItem(20, toggle);

        // Teleport to Arena
        ItemStack teleport = new ItemStack(Material.ENDER_PEARL);
        ItemMeta tpMeta = teleport.getItemMeta();
        tpMeta.setDisplayName("§d§l✦ Teleport to Lobby");
        tpMeta.setLore(Arrays.asList(
                "",
                "§7Teleport to this arena's lobby",
                "",
                "§e§l» Click to teleport!",
                ""
        ));
        teleport.setItemMeta(tpMeta);
        inv.setItem(22, teleport);

        // Delete Arena
        ItemStack delete = new ItemStack(Material.TNT);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName("§c§l✗ Delete Arena");
        deleteMeta.setLore(Arrays.asList(
                "",
                "§7§lWARNING: §7This cannot be undone!",
                "",
                "§7This will permanently delete",
                "§7the arena §e" + arena.getName(),
                "",
                "§c§l» Click to delete!",
                ""
        ));
        delete.setItemMeta(deleteMeta);
        inv.setItem(24, delete);

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§l← Back");
        backMeta.setLore(Arrays.asList("§7Return to arena list"));
        back.setItemMeta(backMeta);
        inv.setItem(40, back);

        player.openInventory(inv);
    }

    public void startArenaSetup(Player player, String arenaName) {
        ArenaSetupSession session = new ArenaSetupSession(arenaName);
        setupSessions.put(player, session);

        // Give player the selection tool (enchanted stick)
        ItemStack selectionTool = new ItemStack(Material.STICK);
        ItemMeta meta = selectionTool.getItemMeta();
        meta.setDisplayName("§6§l✦ §e§lArena Selection Tool §6§l✦");
        meta.setLore(Arrays.asList(
                "",
                "§7Use this tool to select arena boundaries",
                "",
                "§eLeft Click: §7Set Position 1",
                "§eRight Click: §7Set Position 2",
                "",
                "§7Drop this item or use §e/legionadmin cancel §7to exit"
        ));
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        selectionTool.setItemMeta(meta);

        player.getInventory().addItem(selectionTool);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);

        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§a§l✓ Arena Setup Started!");
        player.sendMessage("§7Arena Name: §e" + arenaName);
        player.sendMessage("");
        player.sendMessage("§6§lStep 1: §7Set Arena Boundaries");
        player.sendMessage("§7You have been given a §eSelection Tool");
        player.sendMessage("");
        player.sendMessage("§e§lHow to use:");
        player.sendMessage("§7• §eLeft-click §7a block to set §bPosition 1");
        player.sendMessage("§7• §eRight-click §7a block to set §bPosition 2");
        player.sendMessage("§7• These will define your arena boundaries");
        player.sendMessage("");
        player.sendMessage("§7You can also use:");
        player.sendMessage("§7• §e/legionadmin setpos1 §7(at your location)");
        player.sendMessage("§7• §e/legionadmin setpos2 §7(at your location)");
        player.sendMessage("§7• §e/legionadmin cancel §7to cancel setup");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    public void setPosition1(Player player) {
        ArenaSetupSession session = setupSessions.get(player);
        if (session == null) {
            player.sendMessage("§cYou are not in an arena setup session!");
            return;
        }

        session.pos1 = player.getLocation();
        player.sendMessage("§aPosition 1 set!");
        player.sendMessage("§7Now type §e/legionadmin setpos2 §7to set position 2");
    }

    public void setPosition2(Player player) {
        ArenaSetupSession session = setupSessions.get(player);
        if (session == null) {
            player.sendMessage("§cYou are not in an arena setup session!");
            return;
        }

        if (session.pos1 == null) {
            player.sendMessage("§cYou must set position 1 first!");
            return;
        }

        session.pos2 = player.getLocation();
        player.sendMessage("§aPosition 2 set!");
        player.sendMessage("§7Now type §e/legionadmin setlobby §7to set the lobby spawn");
    }

    public void setLobby(Player player) {
        ArenaSetupSession session = setupSessions.get(player);
        if (session == null) {
            player.sendMessage("§cYou are not in an arena setup session!");
            return;
        }

        if (session.pos1 == null || session.pos2 == null) {
            player.sendMessage("§cYou must set both positions first!");
            return;
        }

        session.lobby = player.getLocation();
        player.sendMessage("§aLobby spawn set!");
        player.sendMessage("§7Type §e/legionadmin finishsetup §7to complete the setup");
    }

    public void finishSetup(Player player) {
        ArenaSetupSession session = setupSessions.get(player);
        if (session == null) {
            player.sendMessage("§cYou are not in an arena setup session!");
            return;
        }

        if (!session.isComplete()) {
            player.sendMessage("§cYou must set all positions first!");
            return;
        }

        // Create the arena
        plugin.getArenaManager().createArena(
                session.arenaName,
                session.pos1,
                session.pos2,
                session.lobby
        );

        // Remove selection tool from inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.STICK && item.hasItemMeta()) {
                String displayName = item.getItemMeta().getDisplayName();
                if (displayName.contains("Arena Selection Tool")) {
                    player.getInventory().remove(item);
                    break;
                }
            }
        }

        setupSessions.remove(player);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§a§l✓ Arena Created Successfully!");
        player.sendMessage("§7Arena §e" + session.arenaName + " §7is now available!");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    public ArenaSetupSession getSetupSession(Player player) {
        return setupSessions.get(player);
    }

    public void cancelSetup(Player player) {
        setupSessions.remove(player);

        // Remove selection tool from inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.STICK && item.hasItemMeta()) {
                String displayName = item.getItemMeta().getDisplayName();
                if (displayName.contains("Arena Selection Tool")) {
                    player.getInventory().remove(item);
                    break;
                }
            }
        }

        player.sendMessage("§c§lArena setup cancelled!");
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 0.5f);
    }

    public static class ArenaSetupSession {
        public String arenaName;
        public org.bukkit.Location pos1;
        public org.bukkit.Location pos2;
        public org.bukkit.Location lobby;

        public ArenaSetupSession(String arenaName) {
            this.arenaName = arenaName;
        }

        public boolean isComplete() {
            return pos1 != null && pos2 != null && lobby != null;
        }
    }
}
