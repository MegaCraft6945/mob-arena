package com.legion.mobarena.listeners;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Game;
import com.legion.mobarena.models.Kit;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerInteractListener implements Listener {

    private final LegionMobArena plugin;

    public PlayerInteractListener(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) return;

        Game game = plugin.getGameManager().getPlayerGame(player.getUniqueId());
        if (game == null) return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Kit kit = game.getPlayerKit(player.getUniqueId());
        if (kit == null) return;

        String displayName = item.getItemMeta().getDisplayName();

        // Handle abilities based on the item
        if (displayName.contains("Brute Rush")) {
            handleBruteRush(player, item);
        } else if (displayName.contains("Arrow Storm")) {
            handleArrowStorm(player, item);
        } else if (displayName.contains("Iron Skin")) {
            handleIronSkin(player, item);
        } else if (displayName.contains("Shadow Step")) {
            handleShadowStep(player, item);
        } else if (displayName.contains("Ground Slam")) {
            handleGroundSlam(player, item, game);
        } else if (displayName.contains("Lightning Strike")) {
            handleLightningStrike(player, item);
        } else if (displayName.contains("Shield Wall")) {
            handleShieldWall(player, item);
        } else if (displayName.contains("Rage")) {
            handleRage(player, item);
        } else if (displayName.contains("Turret")) {
            handleTurret(player, item);
        }
    }

    private void handleBruteRush(Player player, ItemStack item) {
        if (item.getAmount() <= 0) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 1)); // 5 seconds
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.5f);
        player.sendMessage("§6§lBrute Rush activated!");

        item.setAmount(item.getAmount() - 1);
    }

    private void handleArrowStorm(Player player, ItemStack item) {
        if (item.getAmount() <= 0) return;

        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection();

        for (int i = 0; i < 5; i++) {
            Vector spread = direction.clone();
            spread.add(new Vector(
                    (Math.random() - 0.5) * 0.3,
                    (Math.random() - 0.5) * 0.3,
                    (Math.random() - 0.5) * 0.3
            ));
            player.getWorld().spawnArrow(eyeLoc, spread, 2.0f, 0);
        }

        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 0.8f);
        player.sendMessage("§6§lArrow Storm activated!");

        item.setAmount(item.getAmount() - 1);
    }

    private void handleIronSkin(Player player, ItemStack item) {
        if (item.getAmount() <= 0) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 1)); // 8 seconds
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 2f);
        player.sendMessage("§6§lIron Skin activated!");

        item.setAmount(item.getAmount() - 1);
    }

    private void handleShadowStep(Player player, ItemStack item) {
        if (item.getAmount() <= 0) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1)); // 5 seconds
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0)); // 5 seconds
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        player.sendMessage("§6§lShadow Step activated!");

        item.setAmount(item.getAmount() - 1);
    }

    private void handleGroundSlam(Player player, ItemStack item, Game game) {
        if (item.getAmount() <= 0) return;

        // Knock back nearby mobs
        for (Entity entity : game.getArenaMobs()) {
            if (entity instanceof LivingEntity && entity.getLocation().distance(player.getLocation()) <= 5) {
                Vector direction = entity.getLocation().subtract(player.getLocation()).toVector().normalize();
                direction.setY(0.5);
                entity.setVelocity(direction.multiply(2));
            }
        }

        player.getWorld().playEffect(player.getLocation(), Effect.EXPLOSION_LARGE, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.8f);
        player.sendMessage("§6§lGround Slam activated!");

        item.setAmount(item.getAmount() - 1);
    }

    private void handleLightningStrike(Player player, ItemStack item) {
        if (item.getAmount() <= 0) return;

        Location target = player.getTargetBlock(null, 50).getLocation();
        player.getWorld().strikeLightningEffect(target);

        // Damage nearby entities
        for (Entity entity : target.getWorld().getNearbyEntities(target, 3, 3, 3)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                ((LivingEntity) entity).damage(8, player);
            }
        }

        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
        player.sendMessage("§6§lLightning Strike activated!");

        item.setAmount(item.getAmount() - 1);
    }

    private void handleShieldWall(Player player, ItemStack item) {
        if (item.getAmount() <= 0) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, 1)); // 30 seconds, 4 absorption hearts
        player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 1f);
        player.sendMessage("§6§lShield Wall activated!");

        item.setAmount(item.getAmount() - 1);
    }

    private void handleRage(Player player, ItemStack item) {
        if (item.getAmount() <= 0) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 2)); // 10 seconds, Strength III
        player.playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 1f, 1f);
        player.sendMessage("§6§lRage activated!");

        item.setAmount(item.getAmount() - 1);
    }

    private void handleTurret(Player player, ItemStack item) {
        // Simplified turret - just place a message for now
        // Full implementation would require creating a turret system
        player.sendMessage("§cTurret ability coming soon!");
    }
}
