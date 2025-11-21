package com.legion.mobarena.gui;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Kit;
import com.legion.mobarena.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KitSelectionGUI {

    private final LegionMobArena plugin;

    public KitSelectionGUI(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    public void openKitSelection(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8Select Your Kit");

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (playerData == null) return;

        Collection<Kit> allKits = plugin.getKitManager().getAllKits();
        int slot = 0;

        for (Kit kit : allKits) {
            ItemStack item = new ItemStack(kit.getIcon());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(kit.getDisplayName());

            List<String> lore = new ArrayList<>(kit.getDescription());
            lore.add("");

            boolean canUse = kit.getGemCost() == 0 || playerData.hasKit(kit.getName());
            boolean canAfford = playerData.getGems() >= kit.getGemCost();

            if (canUse) {
                lore.add("§a✓ Unlocked");
                lore.add("");
                lore.add("§eClick to select!");
            } else if (canAfford) {
                lore.add("§6Cost: " + kit.getGemCost() + " Gems");
                lore.add("");
                lore.add("§eClick to unlock and select!");
            } else {
                lore.add("§cLocked");
                lore.add("§6Cost: " + kit.getGemCost() + " Gems");
                lore.add("§cYou have: " + playerData.getGems() + " Gems");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot++, item);

            if (slot >= 54) break;
        }

        player.openInventory(inv);
    }

    public void handleClick(Player player, ItemStack clicked) {
        if (clicked == null || !clicked.hasItemMeta()) return;

        String displayName = clicked.getItemMeta().getDisplayName();

        // Find the kit by display name
        for (Kit kit : plugin.getKitManager().getAllKits()) {
            if (kit.getDisplayName().equals(displayName)) {
                PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
                if (playerData == null) return;

                // Check if player can use this kit
                boolean canUse = kit.getGemCost() == 0 || playerData.hasKit(kit.getName());

                if (!canUse) {
                    // Try to unlock
                    if (playerData.getGems() >= kit.getGemCost()) {
                        playerData.removeGems(kit.getGemCost());
                        plugin.getPlayerDataManager().unlockKit(player.getUniqueId(), kit.getName());
                        player.sendMessage(plugin.getMessage("kit-purchased")
                                .replace("{kit}", kit.getDisplayName()));
                    } else {
                        player.sendMessage(plugin.getMessage("not-enough-gems"));
                        return;
                    }
                }

                // Select the kit
                plugin.getGameManager().selectKit(player, kit);
                player.closeInventory();
                return;
            }
        }
    }
}
