package com.legion.mobarena.gui;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
        Inventory inv = Bukkit.createInventory(null, 27, "§8Legion Admin Menu");

        ItemStack createArena = new ItemStack(Material.EMERALD);
        ItemMeta createMeta = createArena.getItemMeta();
        createMeta.setDisplayName("§aCreate Arena");
        createMeta.setLore(Arrays.asList(
                "§7Click to start creating a new arena",
                "",
                "§eYou will need to:",
                "§7- Set the arena corners (min & max)",
                "§7- Set the lobby spawn point"
        ));
        createArena.setItemMeta(createMeta);
        inv.setItem(11, createArena);

        ItemStack manageArenas = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta manageMeta = manageArenas.getItemMeta();
        manageMeta.setDisplayName("§eManage Arenas");
        manageMeta.setLore(Arrays.asList(
                "§7View and manage existing arenas",
                "",
                "§eClick to view all arenas"
        ));
        manageArenas.setItemMeta(manageMeta);
        inv.setItem(13, manageArenas);

        ItemStack settings = new ItemStack(Material.COMPARATOR);
        ItemMeta settingsMeta = settings.getItemMeta();
        settingsMeta.setDisplayName("§bSettings");
        settingsMeta.setLore(Arrays.asList(
                "§7Configure plugin settings",
                "",
                "§7Edit config.yml for now"
        ));
        settings.setItemMeta(settingsMeta);
        inv.setItem(15, settings);

        player.openInventory(inv);
    }

    public void openArenaManagement(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8Manage Arenas");

        Collection<Arena> arenas = plugin.getArenaManager().getAllArenas();

        int slot = 0;
        for (Arena arena : arenas) {
            ItemStack item = new ItemStack(arena.isEnabled() ? Material.LIME_CONCRETE : Material.RED_CONCRETE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e" + arena.getName());
            meta.setLore(Arrays.asList(
                    "§7World: §f" + arena.getWorld().getName(),
                    "§7Status: " + (arena.isEnabled() ? "§aEnabled" : "§cDisabled"),
                    "",
                    "§eLeft Click: §7Toggle Enable/Disable",
                    "§cRight Click: §7Delete Arena"
            ));
            item.setItemMeta(meta);
            inv.setItem(slot++, item);

            if (slot >= 54) break;
        }

        player.openInventory(inv);
    }

    public void startArenaSetup(Player player, String arenaName) {
        ArenaSetupSession session = new ArenaSetupSession(arenaName);
        setupSessions.put(player, session);

        player.sendMessage("§a§lArena Setup Started!");
        player.sendMessage("§7Arena Name: §e" + arenaName);
        player.sendMessage("");
        player.sendMessage("§7Step 1: Set the first corner");
        player.sendMessage("§7Type §e/legionadmin setpos1 §7to set position 1");
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

        setupSessions.remove(player);
        player.sendMessage("§a§lArena created successfully!");
        player.sendMessage("§7Arena §e" + session.arenaName + " §7is now available!");
    }

    public ArenaSetupSession getSetupSession(Player player) {
        return setupSessions.get(player);
    }

    public void cancelSetup(Player player) {
        setupSessions.remove(player);
        player.sendMessage("§cArena setup cancelled!");
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
