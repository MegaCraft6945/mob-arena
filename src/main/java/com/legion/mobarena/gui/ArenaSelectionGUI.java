package com.legion.mobarena.gui;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Arena;
import com.legion.mobarena.models.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArenaSelectionGUI {

    private final LegionMobArena plugin;

    public ArenaSelectionGUI(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    public void openArenaSelection(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8Legion Mob Arena - Select Arena");

        Collection<Arena> arenas = plugin.getArenaManager().getAllArenas();

        if (arenas.isEmpty()) {
            ItemStack noArenas = new ItemStack(Material.BARRIER);
            ItemMeta meta = noArenas.getItemMeta();
            meta.setDisplayName("§cNo Arenas Available");
            meta.setLore(List.of("§7Contact an administrator to create arenas!"));
            noArenas.setItemMeta(meta);
            inv.setItem(22, noArenas);
        } else {
            int slot = 10;
            for (Arena arena : arenas) {
                if (!arena.isEnabled()) continue;

                ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§e" + arena.getName());

                List<String> lore = new ArrayList<>();
                lore.add("§7World: §f" + arena.getWorld().getName());

                // Check if game is active
                Game game = plugin.getGameManager().getGame(arena);
                if (game != null) {
                    int playerCount = game.getPlayers().size();
                    int maxPlayers = plugin.getConfig().getInt("game.max-players", 8);
                    lore.add("§7Players: §a" + playerCount + "§7/§a" + maxPlayers);
                    lore.add("§7Status: §6In Progress");

                    if (playerCount >= maxPlayers) {
                        lore.add("");
                        lore.add("§c§lFULL");
                        item.setType(Material.BARRIER);
                    } else {
                        lore.add("");
                        lore.add("§eClick to join!");
                    }
                } else {
                    lore.add("§7Players: §a0§7/§a" + plugin.getConfig().getInt("game.max-players", 8));
                    lore.add("§7Status: §aWaiting");
                    lore.add("");
                    lore.add("§eClick to join!");
                }

                meta.setLore(lore);
                item.setItemMeta(meta);

                inv.setItem(slot++, item);

                if (slot == 17) slot = 19;
                if (slot == 26) slot = 28;
                if (slot >= 35) break;
            }
        }

        player.openInventory(inv);
    }

    public void handleClick(Player player, ItemStack clicked) {
        if (clicked == null || !clicked.hasItemMeta()) return;
        if (clicked.getType() == Material.BARRIER) return;

        String displayName = clicked.getItemMeta().getDisplayName();
        String arenaName = displayName.substring(2); // Remove color code

        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena != null) {
            player.closeInventory();
            plugin.getGameManager().joinGame(player, arena);
        }
    }
}
