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
        // Count gold nuggets in player inventory
        int goldNuggets = countGoldNuggets(player);

        // Format gold amount for title (abbreviate if large)
        String goldDisplay;
        if (goldNuggets >= 1000) {
            goldDisplay = String.format("%.1fK", goldNuggets / 1000.0);
        } else {
            goldDisplay = String.valueOf(goldNuggets);
        }

        Inventory inv = Bukkit.createInventory(null, 27, "§8§l✦ §6§lUpgrade Shop §8§l✦ §e" + goldDisplay + "G");

        // Initialize player upgrade levels if not exists
        if (!playerUpgradeLevels.containsKey(player)) {
            playerUpgradeLevels.put(player, new HashMap<>());
        }

        // === CATEGORY BUTTONS ===

        // Armor Category
        ItemStack armorButton = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta armorMeta = armorButton.getItemMeta();
        armorMeta.setDisplayName("§6§l✦ Armor Upgrades");
        armorMeta.setLore(Arrays.asList(
                "",
                "§7Upgrade your armor pieces",
                "",
                "§e§lAvailable:",
                "§7• Helmet",
                "§7• Chestplate",
                "§7• Leggings",
                "§7• Boots",
                "",
                "§e§l» Click to browse!"
        ));
        armorButton.setItemMeta(armorMeta);
        inv.setItem(11, armorButton);

        // Weapons Category
        ItemStack weaponsButton = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta weaponsMeta = weaponsButton.getItemMeta();
        weaponsMeta.setDisplayName("§c§l✦ Weapon Upgrades");
        weaponsMeta.setLore(Arrays.asList(
                "",
                "§7Upgrade your weapons",
                "",
                "§e§lAvailable:",
                "§7• Sword",
                "§7• Bow",
                "",
                "§e§l» Click to browse!"
        ));
        weaponsButton.setItemMeta(weaponsMeta);
        inv.setItem(13, weaponsButton);

        // Consumables Category
        ItemStack consumablesButton = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta consumablesMeta = consumablesButton.getItemMeta();
        consumablesMeta.setDisplayName("§a§l✦ Consumables");
        consumablesMeta.setLore(Arrays.asList(
                "",
                "§7Purchase consumable items",
                "",
                "§e§lAvailable:",
                "§7• Arrows (16x)",
                "§7• Golden Apple",
                "§7• Cake",
                "",
                "§e§l» Click to browse!"
        ));
        consumablesButton.setItemMeta(consumablesMeta);
        inv.setItem(15, consumablesButton);

        player.openInventory(inv);
    }

    public void openArmorShop(Player player, Game game) {
        int goldNuggets = countGoldNuggets(player);
        String goldDisplay = goldNuggets >= 1000 ? String.format("%.1fK", goldNuggets / 1000.0) : String.valueOf(goldNuggets);

        Inventory inv = Bukkit.createInventory(null, 45, "§8§l✦ §6§lArmor Shop §8§l✦ §e" + goldDisplay + "G");

        Map<String, Integer> upgrades = playerUpgradeLevels.get(player);

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

        // Helmet - Slot 20
        inv.setItem(20, createUpgradeItem(
                "helmet",
                new Material[]{Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET},
                upgrades.getOrDefault("helmet", 0),
                6
        ));

        // Chestplate - Slot 21
        inv.setItem(21, createUpgradeItem(
                "chestplate",
                new Material[]{Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE},
                upgrades.getOrDefault("chestplate", 0),
                10
        ));

        // Leggings - Slot 22
        inv.setItem(22, createUpgradeItem(
                "leggings",
                new Material[]{Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS},
                upgrades.getOrDefault("leggings", 0),
                8
        ));

        // Boots - Slot 23
        inv.setItem(23, createUpgradeItem(
                "boots",
                new Material[]{Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS},
                upgrades.getOrDefault("boots", 0),
                6
        ));

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§l← Back to Shop");
        backMeta.setLore(Arrays.asList("§7Return to main shop"));
        back.setItemMeta(backMeta);
        inv.setItem(40, back);

        player.openInventory(inv);
    }

    public void openWeaponsShop(Player player, Game game) {
        int goldNuggets = countGoldNuggets(player);
        String goldDisplay = goldNuggets >= 1000 ? String.format("%.1fK", goldNuggets / 1000.0) : String.valueOf(goldNuggets);

        Inventory inv = Bukkit.createInventory(null, 45, "§8§l✦ §c§lWeapons Shop §8§l✦ §e" + goldDisplay + "G");

        Map<String, Integer> upgrades = playerUpgradeLevels.get(player);

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

        // Sword - Slot 21
        inv.setItem(21, createUpgradeItem(
                "sword",
                new Material[]{Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD},
                upgrades.getOrDefault("sword", 0),
                15
        ));

        // Bow - Slot 23
        inv.setItem(23, createBowUpgradeItem(upgrades.getOrDefault("bow", 0)));

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§l← Back to Shop");
        backMeta.setLore(Arrays.asList("§7Return to main shop"));
        back.setItemMeta(backMeta);
        inv.setItem(40, back);

        player.openInventory(inv);
    }

    public void openConsumablesShop(Player player, Game game) {
        int goldNuggets = countGoldNuggets(player);
        String goldDisplay = goldNuggets >= 1000 ? String.format("%.1fK", goldNuggets / 1000.0) : String.valueOf(goldNuggets);

        Inventory inv = Bukkit.createInventory(null, 45, "§8§l✦ §a§lConsumables Shop §8§l✦ §e" + goldDisplay + "G");

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

        // Arrows - Slot 20
        inv.setItem(20, createConsumableItem(Material.ARROW, 16, 5, "Arrows"));

        // Golden Apple - Slot 22
        inv.setItem(22, createConsumableItem(Material.GOLDEN_APPLE, 1, 10, "Golden Apple"));

        // Cake - Slot 24
        inv.setItem(24, createConsumableItem(Material.CAKE, 1, 40, "Cake"));

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§l← Back to Shop");
        backMeta.setLore(Arrays.asList("§7Return to main shop"));
        back.setItemMeta(backMeta);
        inv.setItem(40, back);

        player.openInventory(inv);
    }

    private ItemStack createSectionHeader(String title) {
        ItemStack header = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = header.getItemMeta();
        meta.setDisplayName(title);
        header.setItemMeta(meta);
        return header;
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
        // Level 0 = No bow, Level 1 = Normal bow, Level 2-6 = Power I-V
        if (currentLevel >= 6) {
            ItemStack item = new ItemStack(Material.BOW);
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§aBow §7(§aMAX§7)");
            meta.setLore(Arrays.asList("§7Current: §ePower V", "", "§a§lMAX LEVEL"));
            item.setItemMeta(meta);
            return item;
        }

        int nextLevel = currentLevel + 1;
        int price;

        if (currentLevel == 0) {
            // First purchase - normal bow
            price = 5;
        } else {
            // Upgrades cost more
            price = 10 * currentLevel;
        }

        ItemStack item = new ItemStack(Material.BOW);
        // Show current enchant if level 2+
        if (currentLevel >= 2) {
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, currentLevel - 1);
        }

        ItemMeta meta = item.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();

        if (currentLevel == 0) {
            // No bow yet
            meta.setDisplayName("§eBuy Bow");
            lore.add("§7Buy: §eNormal Bow");
            lore.add("");
            lore.add("§6Cost: " + price + " Gold");
        } else if (currentLevel == 1) {
            // Have normal bow, upgrade to Power I
            meta.setDisplayName("§eUpgrade Bow");
            lore.add("§7Current: §eNormal Bow");
            lore.add("§7Next: §ePower I");
            lore.add("");
            lore.add("§6Cost: " + price + " Gold");
        } else {
            // Have Power enchant, upgrade to next level
            meta.setDisplayName("§eUpgrade Bow");
            lore.add("§7Current: §ePower " + toRoman(currentLevel - 1));
            lore.add("§7Next: §ePower " + toRoman(currentLevel));
            lore.add("");
            lore.add("§6Cost: " + price + " Gold");
        }

        lore.add("");
        lore.add("§eClick to " + (currentLevel == 0 ? "purchase" : "upgrade") + "!");

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

        // Initialize player upgrades if not exists
        if (!playerUpgradeLevels.containsKey(player)) {
            playerUpgradeLevels.put(player, new HashMap<>());
        }
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
        } else if (displayName.contains("Bow")) {
            // Handles both "Buy Bow" and "Upgrade Bow"
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

        if (!removeGoldNuggets(player, price)) {
            player.sendMessage(plugin.getMessage("not-enough-gold"));
            return;
        }

        upgrades.put(type, nextLevel);

        // Give the armor piece (unbreakable)
        ItemStack armorPiece = new ItemStack(materials[nextLevel]);
        ItemMeta armorMeta = armorPiece.getItemMeta();
        if (armorMeta != null) {
            armorMeta.setUnbreakable(true);
            armorPiece.setItemMeta(armorMeta);
        }

        if (type.equals("helmet")) {
            player.getInventory().setHelmet(armorPiece);
        } else if (type.equals("chestplate")) {
            player.getInventory().setChestplate(armorPiece);
        } else if (type.equals("leggings")) {
            player.getInventory().setLeggings(armorPiece);
        } else if (type.equals("boots")) {
            player.getInventory().setBoots(armorPiece);
        }

        player.sendMessage(plugin.getMessage("purchased").replace("{item}", capitalize(type)));
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

        if (!removeGoldNuggets(player, price)) {
            player.sendMessage(plugin.getMessage("not-enough-gold"));
            return;
        }

        upgrades.put("sword", nextLevel);

        // Remove old sword and give new one (unbreakable)
        player.getInventory().remove(swords[currentLevel]);
        ItemStack newSword = new ItemStack(swords[nextLevel]);
        ItemMeta swordMeta = newSword.getItemMeta();
        if (swordMeta != null) {
            swordMeta.setUnbreakable(true);
            newSword.setItemMeta(swordMeta);
        }
        player.getInventory().addItem(newSword);

        player.sendMessage(plugin.getMessage("purchased").replace("{item}", "Sword"));
        openShop(player, game);
    }

    private void handleBowUpgrade(Player player, Game game) {
        Map<String, Integer> upgrades = playerUpgradeLevels.get(player);
        int currentLevel = upgrades.getOrDefault("bow", 0);

        // Max level is now 6 (Power V)
        if (currentLevel >= 6) {
            player.sendMessage("§cYou already have the maximum level for this item!");
            return;
        }

        int nextLevel = currentLevel + 1;
        int price;

        if (currentLevel == 0) {
            // First purchase - normal bow
            price = 5;
        } else {
            // Upgrades
            price = 10 * currentLevel;
        }

        if (!removeGoldNuggets(player, price)) {
            player.sendMessage(plugin.getMessage("not-enough-gold"));
            return;
        }

        upgrades.put("bow", nextLevel);

        // Remove all existing bows from inventory
        player.getInventory().remove(Material.BOW);

        // Give new bow (unbreakable)
        ItemStack bow = new ItemStack(Material.BOW);

        // Add enchantment if level 2+ (Power I-V)
        if (nextLevel >= 2) {
            bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, nextLevel - 1);
        }

        ItemMeta bowMeta = bow.getItemMeta();
        if (bowMeta != null) {
            bowMeta.setUnbreakable(true);
            bow.setItemMeta(bowMeta);
        }
        player.getInventory().addItem(bow);

        String itemName = (nextLevel == 1) ? "Bow" : "Bow Power " + toRoman(nextLevel - 1);
        player.sendMessage(plugin.getMessage("purchased").replace("{item}", itemName));
        openShop(player, game);
    }

    private void handleConsumablePurchase(Player player, Game game, ItemStack item, int price) {
        if (!removeGoldNuggets(player, price)) {
            player.sendMessage(plugin.getMessage("not-enough-gold"));
            return;
        }

        player.getInventory().addItem(item);

        player.sendMessage(plugin.getMessage("purchased").replace("{item}", item.getType().name()));
        openShop(player, game);
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String toRoman(int num) {
        String[] romans = {"", "I", "II", "III", "IV", "V"};
        return romans[num];
    }

    private int countGoldNuggets(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.GOLD_NUGGET) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private boolean removeGoldNuggets(Player player, int amount) {
        if (countGoldNuggets(player) < amount) {
            return false;
        }

        int remaining = amount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.GOLD_NUGGET) {
                int itemAmount = item.getAmount();
                if (itemAmount <= remaining) {
                    remaining -= itemAmount;
                    item.setAmount(0);
                } else {
                    item.setAmount(itemAmount - remaining);
                    remaining = 0;
                }
                if (remaining == 0) break;
            }
        }
        return true;
    }

    public void clearPlayerUpgrades(Player player) {
        playerUpgradeLevels.remove(player);
    }
}
