package com.legion.mobarena.gui;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UpgradeShopGUI {

    private final LegionMobArena plugin;
    private final Map<Player, Map<String, Integer>> playerUpgradeLevels;

    public UpgradeShopGUI(LegionMobArena plugin) {
        this.plugin = plugin;
        this.playerUpgradeLevels = new HashMap<>();
    }

    public void openShop(Player player, Game game) {
        int gold = game.getPlayerGold(player.getUniqueId());

        Inventory inv = Bukkit.createInventory(null, 27, "§8Upgrade Shop - §6" + gold + " Gold");

        // Initialize player upgrade levels if not exists
        if (!playerUpgradeLevels.containsKey(player)) {
            playerUpgradeLevels.put(player, new HashMap<>());
        }

        Map<String, Integer> upgrades = playerUpgradeLevels.get(player);

        // Helmet - Slot 10
        inv.setItem(10, createUpgradeItem(
                "helmet",
                new Material[]{Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET},
                upgrades.getOrDefault("helmet", 0),
                6
        ));

        // Chestplate - Slot 11
        inv.setItem(11, createUpgradeItem(
                "chestplate",
                new Material[]{Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE},
                upgrades.getOrDefault("chestplate", 0),
                10
        ));

        // Leggings - Slot 12
        inv.setItem(12, createUpgradeItem(
                "leggings",
                new Material[]{Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS},
                upgrades.getOrDefault("leggings", 0),
                8
        ));

        // Boots - Slot 13
        inv.setItem(13, createUpgradeItem(
                "boots",
                new Material[]{Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS},
                upgrades.getOrDefault("boots", 0),
                6
        ));

        // Sword - Slot 19
        inv.setItem(19, createUpgradeItem(
                "sword",
                new Material[]{Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD},
                upgrades.getOrDefault("sword", 0),
                15
        ));

        // Bow - Slot 20
        inv.setItem(20, createBowUpgradeItem(upgrades.getOrDefault("bow", 0)));

        // Arrows - Slot 21
        inv.setItem(21, createConsumableItem(Material.ARROW, 16, 5, "Arrows"));

        // Cake - Slot 22
        inv.setItem(22, createConsumableItem(Material.CAKE, 1, 40, "Cake"));

        // Golden Apple - Slot 23
        inv.setItem(23, createConsumableItem(Material.GOLDEN_APPLE, 1, 10, "Golden Apple"));

        player.openInventory(inv);
    }

    private ItemStack createUpgradeItem(String type, Material[] materials, int currentLevel, int basePrice) {
        String[] names = {"Leather", "Chainmail", "Iron", "Diamond"};

        if (currentLevel >= materials.length - 1) {
            // Max level
            ItemStack item = new ItemStack(materials[materials.length - 1]);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a" + capitalize(type) + " §7(§aMAX§7)");
            meta.setLore(Arrays.asList("§7Current: §e" + names[materials.length - 1], "", "§a§lMAX LEVEL"));
            item.setItemMeta(meta);
            return item;
        }

        int nextLevel = currentLevel + 1;
        int price = basePrice * (nextLevel + 1);

        ItemStack item = new ItemStack(materials[nextLevel]);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eUpgrade " + capitalize(type));

        ArrayList<String> lore = new ArrayList<>();
        if (currentLevel > 0) {
            lore.add("§7Current: §e" + names[currentLevel]);
        } else {
            lore.add("§7Current: §e" + names[0] + " §7(Equipped)");
        }
        lore.add("§7Next: §e" + names[nextLevel]);
        lore.add("");
        lore.add("§6Cost: " + price + " Gold");
        lore.add("");
        lore.add("§eClick to upgrade!");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBowUpgradeItem(int currentLevel) {
        if (currentLevel >= 5) {
            ItemStack item = new ItemStack(Material.BOW);
            item.addUnsafeEnchantment(Enchantment.POWER, 5);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§aBow §7(§aMAX§7)");
            meta.setLore(Arrays.asList("§7Current: §ePower V", "", "§a§lMAX LEVEL"));
            item.setItemMeta(meta);
            return item;
        }

        int nextLevel = currentLevel + 1;
        int price = 10 * (nextLevel);

        ItemStack item = new ItemStack(Material.BOW);
        if (currentLevel > 0) {
            item.addUnsafeEnchantment(Enchantment.POWER, currentLevel);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eUpgrade Bow");

        ArrayList<String> lore = new ArrayList<>();
        if (currentLevel > 0) {
            lore.add("§7Current: §ePower " + toRoman(currentLevel));
        } else {
            lore.add("§7Buy: §eBow with Power I");
        }
        lore.add("§7Next: §ePower " + toRoman(nextLevel));
        lore.add("");
        lore.add("§6Cost: " + price + " Gold");
        lore.add("");
        lore.add("§eClick to upgrade!");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createConsumableItem(Material material, int amount, int price, String name) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e" + name);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§7Amount: §e" + amount);
        lore.add("");
        lore.add("§6Cost: " + price + " Gold");
        lore.add("");
        lore.add("§eClick to purchase!");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void handleClick(Player player, ItemStack clicked, Game game) {
        if (clicked == null || !clicked.hasItemMeta()) return;

        String displayName = clicked.getItemMeta().getDisplayName();
        int gold = game.getPlayerGold(player.getUniqueId());
        Map<String, Integer> upgrades = playerUpgradeLevels.get(player);

        // Check what was clicked
        if (displayName.contains("Upgrade Helmet")) {
            handleArmorUpgrade(player, game, "helmet", 6, Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET);
        } else if (displayName.contains("Upgrade Chestplate")) {
            handleArmorUpgrade(player, game, "chestplate", 10, Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE);
        } else if (displayName.contains("Upgrade Leggings")) {
            handleArmorUpgrade(player, game, "leggings", 8, Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS);
        } else if (displayName.contains("Upgrade Boots")) {
            handleArmorUpgrade(player, game, "boots", 6, Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS);
        } else if (displayName.contains("Upgrade Sword")) {
            handleSwordUpgrade(player, game);
        } else if (displayName.contains("Upgrade Bow")) {
            handleBowUpgrade(player, game);
        } else if (displayName.contains("Arrows")) {
            handleConsumablePurchase(player, game, new ItemStack(Material.ARROW, 16), 5);
        } else if (displayName.contains("Cake")) {
            handleConsumablePurchase(player, game, new ItemStack(Material.CAKE, 1), 40);
        } else if (displayName.contains("Golden Apple")) {
            handleConsumablePurchase(player, game, new ItemStack(Material.GOLDEN_APPLE, 1), 10);
        }
    }

    private void handleArmorUpgrade(Player player, Game game, String type, int basePrice, Material... materials) {
        Map<String, Integer> upgrades = playerUpgradeLevels.get(player);
        int currentLevel = upgrades.getOrDefault(type, 0);

        if (currentLevel >= materials.length - 1) {
            player.sendMessage("§cYou already have the maximum level for this item!");
            return;
        }

        int nextLevel = currentLevel + 1;
        int price = basePrice * (nextLevel + 1);

        if (game.getPlayerGold(player.getUniqueId()) < price) {
            player.sendMessage(plugin.getConfig().getString("messages.not-enough-gold"));
            return;
        }

        game.removePlayerGold(player.getUniqueId(), price);
        upgrades.put(type, nextLevel);

        // Give the armor piece
        ItemStack armorPiece = new ItemStack(materials[nextLevel]);
        if (type.equals("helmet")) {
            player.getInventory().setHelmet(armorPiece);
        } else if (type.equals("chestplate")) {
            player.getInventory().setChestplate(armorPiece);
        } else if (type.equals("leggings")) {
            player.getInventory().setLeggings(armorPiece);
        } else if (type.equals("boots")) {
            player.getInventory().setBoots(armorPiece);
        }

        player.sendMessage(plugin.getConfig().getString("messages.purchased").replace("{item}", capitalize(type)));
        openShop(player, game);
    }

    private void handleSwordUpgrade(Player player, Game game) {
        Map<String, Integer> upgrades = playerUpgradeLevels.get(player);
        int currentLevel = upgrades.getOrDefault("sword", 0);
        Material[] swords = {Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD};

        if (currentLevel >= swords.length - 1) {
            player.sendMessage("§cYou already have the maximum level for this item!");
            return;
        }

        int nextLevel = currentLevel + 1;
        int price = 15 * (nextLevel + 1);

        if (game.getPlayerGold(player.getUniqueId()) < price) {
            player.sendMessage(plugin.getConfig().getString("messages.not-enough-gold"));
            return;
        }

        game.removePlayerGold(player.getUniqueId(), price);
        upgrades.put("sword", nextLevel);

        // Remove old sword and give new one
        player.getInventory().remove(swords[currentLevel]);
        player.getInventory().addItem(new ItemStack(swords[nextLevel]));

        player.sendMessage(plugin.getConfig().getString("messages.purchased").replace("{item}", "Sword"));
        openShop(player, game);
    }

    private void handleBowUpgrade(Player player, Game game) {
        Map<String, Integer> upgrades = playerUpgradeLevels.get(player);
        int currentLevel = upgrades.getOrDefault("bow", 0);

        if (currentLevel >= 5) {
            player.sendMessage("§cYou already have the maximum level for this item!");
            return;
        }

        int nextLevel = currentLevel + 1;
        int price = 10 * nextLevel;

        if (game.getPlayerGold(player.getUniqueId()) < price) {
            player.sendMessage(plugin.getConfig().getString("messages.not-enough-gold"));
            return;
        }

        game.removePlayerGold(player.getUniqueId(), price);
        upgrades.put("bow", nextLevel);

        // Give upgraded bow
        ItemStack bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.POWER, nextLevel);
        player.getInventory().addItem(bow);

        player.sendMessage(plugin.getConfig().getString("messages.purchased").replace("{item}", "Bow Upgrade"));
        openShop(player, game);
    }

    private void handleConsumablePurchase(Player player, Game game, ItemStack item, int price) {
        if (game.getPlayerGold(player.getUniqueId()) < price) {
            player.sendMessage(plugin.getConfig().getString("messages.not-enough-gold"));
            return;
        }

        game.removePlayerGold(player.getUniqueId(), price);
        player.getInventory().addItem(item);

        player.sendMessage(plugin.getConfig().getString("messages.purchased").replace("{item}", item.getType().name()));
        openShop(player, game);
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String toRoman(int num) {
        String[] romans = {"", "I", "II", "III", "IV", "V"};
        return romans[num];
    }

    public void clearPlayerUpgrades(Player player) {
        playerUpgradeLevels.remove(player);
    }
}
