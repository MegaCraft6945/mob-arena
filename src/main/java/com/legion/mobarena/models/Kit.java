package com.legion.mobarena.models;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kit {
    private final String name;
    private final String displayName;
    private final List<String> description;
    private final int gemCost;
    private final Material icon;
    private final ItemStack[] armor;
    private final List<ItemStack> items;
    private final String ability;
    private final ItemStack abilityItem;

    public Kit(String name, String displayName, List<String> description, int gemCost,
               Material icon, ItemStack[] armor, List<ItemStack> items, String ability, ItemStack abilityItem) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.gemCost = gemCost;
        this.icon = icon;
        this.armor = armor;
        this.items = items;
        this.ability = ability;
        this.abilityItem = abilityItem;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getDescription() {
        return description;
    }

    public int getGemCost() {
        return gemCost;
    }

    public Material getIcon() {
        return icon;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public String getAbility() {
        return ability;
    }

    public ItemStack getAbilityItem() {
        return abilityItem;
    }

    // Helper methods to create item stacks
    public static ItemStack createItem(Material material, int amount) {
        return new ItemStack(material, amount);
    }

    public static ItemStack createItem(Material material, int amount, String name) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createItem(Material material, int amount, String name, String... lore) {
        ItemStack item = createItem(material, amount, name);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createEnchantedItem(Material material, int amount, Enchantment enchant, int level) {
        ItemStack item = new ItemStack(material, amount);
        item.addUnsafeEnchantment(enchant, level);
        return item;
    }

    // Create leather armor piece
    public static ItemStack createLeatherArmor(Material armorType) {
        return new ItemStack(armorType);
    }
}
