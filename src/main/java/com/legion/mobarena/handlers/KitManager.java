package com.legion.mobarena.handlers;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitManager {

    private final LegionMobArena plugin;
    private final Map<String, Kit> kits;

    public KitManager(LegionMobArena plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();
        loadKits();
    }

    private void loadKits() {
        // WARRIOR - Free kit
        kits.put("warrior", new Kit(
                "warrior",
                "§cWarrior",
                Arrays.asList(
                        "§7Ability: §eBrute Rush",
                        "§7Gain super strength for a few seconds",
                        "",
                        "§7Items: Stone Sword, 3 Golden Apples",
                        "§7Armor: Leather"
                ),
                0, // Free
                Material.STONE_SWORD,
                new ItemStack[]{
                        new ItemStack(Material.LEATHER_BOOTS),
                        new ItemStack(Material.LEATHER_LEGGINGS),
                        new ItemStack(Material.LEATHER_CHESTPLATE),
                        new ItemStack(Material.LEATHER_HELMET)
                },
                Arrays.asList(
                        new ItemStack(Material.STONE_SWORD),
                        new ItemStack(Material.GOLDEN_APPLE, 3)
                ),
                "Brute Rush",
                Kit.createItem(Material.BLAZE_POWDER, 1, "§6Brute Rush §7(Right Click)",
                        "§7Gain Strength II for 5 seconds")
        ));

        // ARCHER
        kits.put("archer", new Kit(
                "archer",
                "§aArcher",
                Arrays.asList(
                        "§7Ability: §eArrow Storm",
                        "§7Shoot multiple arrows at once",
                        "",
                        "§7Items: Bow (Power I), 32 Arrows",
                        "§7Armor: Leather"
                ),
                100, // 100 gems
                Material.BOW,
                new ItemStack[]{
                        new ItemStack(Material.LEATHER_BOOTS),
                        new ItemStack(Material.LEATHER_LEGGINGS),
                        new ItemStack(Material.LEATHER_CHESTPLATE),
                        new ItemStack(Material.LEATHER_HELMET)
                },
                Arrays.asList(
                        new ItemStack(Material.WOODEN_SWORD),
                        Kit.createEnchantedItem(Material.BOW, 1, Enchantment.ARROW_DAMAGE, 1),
                        new ItemStack(Material.ARROW, 32)
                ),
                "Arrow Storm",
                Kit.createItem(Material.SPECTRAL_ARROW, 1, "§6Arrow Storm §7(Right Click)",
                        "§7Shoot 5 arrows at once")
        ));

        // TANK
        kits.put("tank", new Kit(
                "tank",
                "§9Tank",
                Arrays.asList(
                        "§7Ability: §eIron Skin",
                        "§7Gain resistance for a short time",
                        "",
                        "§7Items: Stone Sword",
                        "§7Armor: Chainmail"
                ),
                200, // 200 gems
                Material.CHAINMAIL_CHESTPLATE,
                new ItemStack[]{
                        new ItemStack(Material.CHAINMAIL_BOOTS),
                        new ItemStack(Material.CHAINMAIL_LEGGINGS),
                        new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                        new ItemStack(Material.CHAINMAIL_HELMET)
                },
                Arrays.asList(
                        new ItemStack(Material.STONE_SWORD),
                        new ItemStack(Material.GOLDEN_APPLE, 1)
                ),
                "Iron Skin",
                Kit.createItem(Material.IRON_INGOT, 1, "§6Iron Skin §7(Right Click)",
                        "§7Gain Resistance II for 8 seconds")
        ));

        // NINJA
        kits.put("ninja", new Kit(
                "ninja",
                "§8Ninja",
                Arrays.asList(
                        "§7Ability: §eShadow Step",
                        "§7Gain speed and invisibility",
                        "",
                        "§7Items: Stone Sword",
                        "§7Armor: Leather"
                ),
                300, // 300 gems
                Material.LEATHER_BOOTS,
                new ItemStack[]{
                        new ItemStack(Material.LEATHER_BOOTS),
                        new ItemStack(Material.LEATHER_LEGGINGS),
                        new ItemStack(Material.LEATHER_CHESTPLATE),
                        new ItemStack(Material.LEATHER_HELMET)
                },
                Arrays.asList(
                        new ItemStack(Material.STONE_SWORD)
                ),
                "Shadow Step",
                Kit.createItem(Material.SUGAR, 1, "§6Shadow Step §7(Right Click)",
                        "§7Gain Speed II and Invisibility for 5 seconds")
        ));

        // BRUTE
        kits.put("brute", new Kit(
                "brute",
                "§4Brute",
                Arrays.asList(
                        "§7Ability: §eGround Slam",
                        "§7Knock back nearby enemies",
                        "",
                        "§7Items: Iron Sword",
                        "§7Armor: Leather"
                ),
                400, // 400 gems
                Material.IRON_SWORD,
                new ItemStack[]{
                        new ItemStack(Material.LEATHER_BOOTS),
                        new ItemStack(Material.LEATHER_LEGGINGS),
                        new ItemStack(Material.LEATHER_CHESTPLATE),
                        new ItemStack(Material.LEATHER_HELMET)
                },
                Arrays.asList(
                        new ItemStack(Material.IRON_SWORD),
                        new ItemStack(Material.GOLDEN_APPLE, 2)
                ),
                "Ground Slam",
                Kit.createItem(Material.MAGMA_CREAM, 1, "§6Ground Slam §7(Right Click)",
                        "§7Knock back nearby mobs")
        ));

        // MAGE
        kits.put("mage", new Kit(
                "mage",
                "§5Mage",
                Arrays.asList(
                        "§7Ability: §eLightning Strike",
                        "§7Strike enemies with lightning",
                        "",
                        "§7Items: Stone Sword, 2 Potions",
                        "§7Armor: Leather"
                ),
                500, // 500 gems
                Material.ENCHANTED_BOOK,
                new ItemStack[]{
                        new ItemStack(Material.LEATHER_BOOTS),
                        new ItemStack(Material.LEATHER_LEGGINGS),
                        new ItemStack(Material.LEATHER_CHESTPLATE),
                        new ItemStack(Material.LEATHER_HELMET)
                },
                Arrays.asList(
                        new ItemStack(Material.STONE_SWORD)
                ),
                "Lightning Strike",
                Kit.createItem(Material.GLOWSTONE_DUST, 1, "§6Lightning Strike §7(Right Click)",
                        "§7Strike lightning where you're looking")
        ));

        // KNIGHT
        kits.put("knight", new Kit(
                "knight",
                "§bKnight",
                Arrays.asList(
                        "§7Ability: §eShield Wall",
                        "§7Gain absorption hearts",
                        "",
                        "§7Items: Iron Sword, Shield",
                        "§7Armor: Iron"
                ),
                600, // 600 gems
                Material.SHIELD,
                new ItemStack[]{
                        new ItemStack(Material.IRON_BOOTS),
                        new ItemStack(Material.IRON_LEGGINGS),
                        new ItemStack(Material.IRON_CHESTPLATE),
                        new ItemStack(Material.IRON_HELMET)
                },
                Arrays.asList(
                        new ItemStack(Material.IRON_SWORD),
                        new ItemStack(Material.SHIELD),
                        new ItemStack(Material.GOLDEN_APPLE, 3)
                ),
                "Shield Wall",
                Kit.createItem(Material.SLIME_BALL, 1, "§6Shield Wall §7(Right Click)",
                        "§7Gain 4 absorption hearts")
        ));

        // BERSERKER
        kits.put("berserker", new Kit(
                "berserker",
                "§cBerserker",
                Arrays.asList(
                        "§7Ability: §eRage",
                        "§7Gain massive strength when low on health",
                        "",
                        "§7Items: Diamond Sword",
                        "§7Armor: Leather"
                ),
                800, // 800 gems
                Material.DIAMOND_SWORD,
                new ItemStack[]{
                        new ItemStack(Material.LEATHER_BOOTS),
                        new ItemStack(Material.LEATHER_LEGGINGS),
                        new ItemStack(Material.LEATHER_CHESTPLATE),
                        new ItemStack(Material.LEATHER_HELMET)
                },
                Arrays.asList(
                        new ItemStack(Material.DIAMOND_SWORD)
                ),
                "Rage",
                Kit.createItem(Material.REDSTONE, 1, "§6Rage §7(Right Click)",
                        "§7Gain Strength III for 10 seconds")
        ));

        // ENGINEER
        kits.put("engineer", new Kit(
                "engineer",
                "§eEngineer",
                Arrays.asList(
                        "§7Ability: §eTurret",
                        "§7Place a turret that shoots arrows",
                        "",
                        "§7Items: Stone Sword, Bow",
                        "§7Armor: Leather"
                ),
                700, // 700 gems
                Material.DISPENSER,
                new ItemStack[]{
                        new ItemStack(Material.LEATHER_BOOTS),
                        new ItemStack(Material.LEATHER_LEGGINGS),
                        new ItemStack(Material.LEATHER_CHESTPLATE),
                        new ItemStack(Material.LEATHER_HELMET)
                },
                Arrays.asList(
                        new ItemStack(Material.STONE_SWORD),
                        new ItemStack(Material.BOW),
                        new ItemStack(Material.ARROW, 16)
                ),
                "Turret",
                Kit.createItem(Material.DISPENSER, 1, "§6Turret §7(Right Click)",
                        "§7Place a turret (max 2)")
        ));

        plugin.getLogger().info("Loaded " + kits.size() + " kits!");
    }

    public Kit getKit(String name) {
        return kits.get(name.toLowerCase());
    }

    public Collection<Kit> getAllKits() {
        return kits.values();
    }

    public List<Kit> getKitsForPlayer(UUID playerUuid) {
        List<Kit> availableKits = new ArrayList<>();
        var playerData = plugin.getPlayerDataManager().getPlayerData(playerUuid);

        for (Kit kit : kits.values()) {
            // Free kits or unlocked kits
            if (kit.getGemCost() == 0 || (playerData != null && playerData.hasKit(kit.getName()))) {
                availableKits.add(kit);
            }
        }

        return availableKits;
    }

    public boolean canPlayerUseKit(UUID playerUuid, String kitName) {
        Kit kit = getKit(kitName);
        if (kit == null) return false;

        // Free kits
        if (kit.getGemCost() == 0) return true;

        // Check if player has unlocked it
        var playerData = plugin.getPlayerDataManager().getPlayerData(playerUuid);
        return playerData != null && playerData.hasKit(kitName);
    }
}
