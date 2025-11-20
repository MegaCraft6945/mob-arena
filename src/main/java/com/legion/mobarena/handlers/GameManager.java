package com.legion.mobarena.handlers;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.gui.KitSelectionGUI;
import com.legion.mobarena.gui.UpgradeShopGUI;
import com.legion.mobarena.models.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameManager {

    private final LegionMobArena plugin;
    private final Map<Arena, Game> activeGames;
    private final Map<UUID, Game> playerGames;
    private final KitSelectionGUI kitSelectionGUI;
    private final UpgradeShopGUI upgradeShopGUI;

    public GameManager(LegionMobArena plugin) {
        this.plugin = plugin;
        this.activeGames = new HashMap<>();
        this.playerGames = new HashMap<>();
        this.kitSelectionGUI = new KitSelectionGUI(plugin);
        this.upgradeShopGUI = new UpgradeShopGUI(plugin);
    }

    public Game getGame(Arena arena) {
        return activeGames.get(arena);
    }

    public Game getPlayerGame(UUID playerUuid) {
        return playerGames.get(playerUuid);
    }

    public boolean isPlayerInGame(UUID playerUuid) {
        return playerGames.containsKey(playerUuid);
    }

    public void joinGame(Player player, Arena arena) {
        if (isPlayerInGame(player.getUniqueId())) {
            player.sendMessage(plugin.getConfig().getString("messages.prefix") + "§cYou are already in a game!");
            return;
        }

        Game game = activeGames.get(arena);
        if (game == null) {
            game = new Game(arena);
            activeGames.put(arena, game);
        }

        if (game.getPlayers().size() >= plugin.getConfig().getInt("game.max-players", 8)) {
            player.sendMessage(plugin.getConfig().getString("messages.prefix") + "§cThis arena is full!");
            return;
        }

        game.addPlayer(player.getUniqueId());
        playerGames.put(player.getUniqueId(), game);

        player.teleport(arena.getLobby());
        // Keep player in current gamemode (usually survival) - protection handled by listeners

        // Welcome messages
        player.sendMessage("");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§6§l      Welcome to Legion Mob Arena!");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§7Arena: §e" + arena.getName());
        player.sendMessage("§7Players: §e" + game.getPlayers().size() + "§7/§e" + plugin.getConfig().getInt("game.max-players", 8));
        player.sendMessage("");
        player.sendMessage("§7§oSelect your kit to get ready!");
        player.sendMessage("§7§oUse §e/legion leave §7to exit anytime.");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("");

        // Open kit selection
        openKitSelection(player);
    }

    public void openKitSelection(Player player) {
        kitSelectionGUI.openKitSelection(player);
    }

    public void selectKit(Player player, Kit kit) {
        Game game = getPlayerGame(player.getUniqueId());
        if (game == null) return;

        game.setPlayerKit(player.getUniqueId(), kit);
        player.sendMessage(plugin.getConfig().getString("messages.kit-selected")
                .replace("{kit}", kit.getDisplayName()));

        // Check if all players are ready
        if (game.areAllPlayersReady() && game.getPlayers().size() >= plugin.getConfig().getInt("game.min-players", 1)) {
            startCountdown(game);
        }
    }

    private void startCountdown(Game game) {
        if (game.getState() != GameState.WAITING) return;

        game.setState(GameState.COUNTDOWN);
        game.setCountdown(plugin.getConfig().getInt("game.countdown-seconds", 10));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getState() != GameState.COUNTDOWN) {
                    cancel();
                    return;
                }

                int countdown = game.getCountdown();
                if (countdown <= 0) {
                    startGame(game);
                    cancel();
                    return;
                }

                // Broadcast countdown
                for (UUID playerId : game.getPlayers()) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null) {
                        player.sendMessage("§eGame starting in §a" + countdown + " §eseconds...");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                    }
                }

                game.decrementCountdown();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void startGame(Game game) {
        game.setState(GameState.ACTIVE);
        game.setCurrentRound(1);

        // Give players their kits and emerald
        for (UUID playerId : game.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                giveKit(player, game.getPlayerKit(playerId));

                // Give emerald for upgrade shop access
                ItemStack emerald = new ItemStack(Material.EMERALD);
                ItemMeta meta = emerald.getItemMeta();
                meta.setDisplayName("§aUpgrade Shop");
                meta.setLore(Arrays.asList("§7Right-click to open the", "§7upgrade shop after each round!"));
                emerald.setItemMeta(meta);
                player.getInventory().setItem(8, emerald);

                player.sendMessage("§a§lGAME STARTED!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
            }
        }

        startRound(game);
    }

    private void giveKit(Player player, Kit kit) {
        if (kit == null) return;

        player.getInventory().clear();

        // Make armor unbreakable and give to player
        ItemStack[] armor = kit.getArmor();
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] != null) {
                makeUnbreakable(armor[i]);
            }
        }
        player.getInventory().setArmorContents(armor);

        // Make items unbreakable and give to player
        for (ItemStack item : kit.getItems()) {
            makeUnbreakable(item);
            player.getInventory().addItem(item);
        }

        // Ability item is already unbreakable from Kit.createItem()
        if (kit.getAbilityItem() != null) {
            player.getInventory().addItem(kit.getAbilityItem());
        }

        // Give emerald for upgrade shop access
        ItemStack emerald = new ItemStack(Material.EMERALD, 1);
        makeUnbreakable(emerald);
        player.getInventory().setItem(8, emerald);
    }

    private void makeUnbreakable(ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(true);
                item.setItemMeta(meta);
            }
        }
    }

    private void startRound(Game game) {
        int round = game.getCurrentRound();
        int mobCount = game.calculateMobsForRound(round);
        game.setMobsRemaining(mobCount);

        // Check if this is a boss round
        boolean isBossRound = round % 10 == 0;

        // Broadcast round start
        for (UUID playerId : game.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                if (isBossRound) {
                    player.sendMessage("§c§l⚠ BOSS ROUND " + round + " ⚠");
                    player.sendTitle("§c§lBOSS ROUND " + round, "§eDefeat the bosses!", 10, 70, 20);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.8f);
                } else {
                    player.sendMessage("§6§lRound " + round + " Started!");
                    player.sendTitle("§6Round " + round, "§eKill " + mobCount + " mobs!", 10, 70, 20);
                    player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5f, 1f);
                }
            }
        }

        // Spawn mobs or bosses
        if (isBossRound) {
            spawnBossRound(game);
        } else {
            spawnRoundMobs(game);
        }

        // Start action bar updater
        startActionBarUpdater(game);

        // Start mob boundary checker
        startMobBoundaryChecker(game);
    }

    private void spawnRoundMobs(Game game) {
        Arena arena = game.getArena();
        int mobCount = game.getMobsRemaining();
        int round = game.getCurrentRound();

        new BukkitRunnable() {
            int spawned = 0;

            @Override
            public void run() {
                if (spawned >= mobCount || game.getState() != GameState.ACTIVE) {
                    cancel();
                    return;
                }

                // Spawn 3 mobs at a time
                for (int i = 0; i < 3 && spawned < mobCount; i++) {
                    Location spawnLoc = getRandomSpawnLocation(arena);
                    Entity mob = spawnMobForRound(spawnLoc, round);
                    if (mob != null) {
                        game.addArenaMob(mob);
                        spawned++;
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Spawn every second
    }

    private void spawnBossRound(Game game) {
        Arena arena = game.getArena();
        int mobCount = game.getMobsRemaining();
        int round = game.getCurrentRound();

        new BukkitRunnable() {
            int spawned = 0;

            @Override
            public void run() {
                if (spawned >= mobCount || game.getState() != GameState.ACTIVE) {
                    cancel();
                    return;
                }

                // Spawn 2 bosses at a time (slower spawn rate for bosses)
                for (int i = 0; i < 2 && spawned < mobCount; i++) {
                    Location spawnLoc = getRandomSpawnLocation(arena);
                    Entity boss = spawnBossMob(spawnLoc, round, spawned);
                    if (boss != null) {
                        game.addArenaMob(boss);
                        spawned++;
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 30L); // Spawn every 1.5 seconds
    }

    private Location getRandomSpawnLocation(Arena arena) {
        Location min = arena.getMin();
        Location max = arena.getMax();
        Random random = new Random();

        double x = min.getX() + random.nextDouble() * (max.getX() - min.getX());
        double z = min.getZ() + random.nextDouble() * (max.getZ() - min.getZ());
        double y = min.getWorld().getHighestBlockYAt((int) x, (int) z) + 1;

        return new Location(min.getWorld(), x, y, z);
    }

    private Entity spawnBossMob(Location location, int round, int bossIndex) {
        Random random = new Random();
        EntityType type;

        // Boss type selection based on round
        if (round <= 20) {
            // Early boss rounds: Wolves and basic bosses
            EntityType[] bossTypes = {EntityType.WOLF, EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER};
            type = bossTypes[random.nextInt(bossTypes.length)];
        } else if (round <= 30) {
            // Mid boss rounds: Tougher bosses
            EntityType[] bossTypes = {EntityType.WOLF, EntityType.IRON_GOLEM, EntityType.WITHER_SKELETON,
                                     EntityType.BLAZE, EntityType.RAVAGER};
            type = bossTypes[random.nextInt(bossTypes.length)];
        } else {
            // Late boss rounds: Most dangerous bosses
            EntityType[] bossTypes = {EntityType.WOLF, EntityType.IRON_GOLEM, EntityType.RAVAGER,
                                     EntityType.WITHER_SKELETON, EntityType.VINDICATOR, EntityType.EVOKER};
            type = bossTypes[random.nextInt(bossTypes.length)];
        }

        Entity entity = location.getWorld().spawnEntity(location, type);

        if (entity instanceof LivingEntity) {
            LivingEntity boss = (LivingEntity) entity;
            boss.setRemoveWhenFarAway(false);
            boss.setPersistent(true);

            // Boss name prefixes
            String[] namePrefixes = {"§c§lBOSS", "§4§lELITE", "§c§lCHAMPION", "§4§lTERROR"};
            String namePrefix = namePrefixes[random.nextInt(namePrefixes.length)];

            // Set custom name based on type
            String mobName = type.name().replace("_", " ");
            boss.setCustomName(namePrefix + " §r§c" + mobName + " #" + (bossIndex + 1));
            boss.setCustomNameVisible(true);

            // Enhanced boss stats
            double healthMultiplier = 2.0 + (round / 10.0);
            boss.setMaxHealth(boss.getMaxHealth() * healthMultiplier);
            boss.setHealth(boss.getMaxHealth());

            // Special wolf enhancements
            if (type == EntityType.WOLF) {
                Wolf wolf = (Wolf) boss;
                wolf.setAngry(true);
                wolf.setAdult();
                // Wolves are very aggressive bosses
                boss.setMaxHealth(boss.getMaxHealth() * 1.5);
                boss.setHealth(boss.getMaxHealth());
            }

            // Give equipment to humanoid bosses
            if (type == EntityType.ZOMBIE || type == EntityType.SKELETON || type == EntityType.WITHER_SKELETON) {
                boss.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
                boss.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                boss.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                boss.getEquipment().setItemInMainHandDropChance(0f);
                boss.getEquipment().setHelmetDropChance(0f);
                boss.getEquipment().setChestplateDropChance(0f);
            }

            // Spawn particles around boss
            location.getWorld().spawnParticle(Particle.FLAME, location, 30, 0.5, 0.5, 0.5, 0.05);
            location.getWorld().playSound(location, Sound.ENTITY_WITHER_SPAWN, 0.5f, 1.2f);
        }

        return entity;
    }

    private Entity spawnMobForRound(Location location, int round) {
        Random random = new Random();
        EntityType type;

        // Determine round theme for variety
        int roundMod = round % 10;

        if (roundMod == 1 || roundMod == 2) {
            // Early rounds: Zombies and Skeletons
            type = random.nextBoolean() ? EntityType.ZOMBIE : EntityType.SKELETON;
        } else if (roundMod == 3 || roundMod == 4) {
            // Spider rounds
            type = random.nextInt(3) == 0 ? EntityType.CAVE_SPIDER : EntityType.SPIDER;
        } else if (roundMod == 5 || roundMod == 6) {
            // Mixed dangerous mobs
            EntityType[] options = {EntityType.CREEPER, EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER};
            type = options[random.nextInt(options.length)];
        } else if (roundMod == 7 || roundMod == 8) {
            // Advanced mobs
            EntityType[] options = {EntityType.WITCH, EntityType.ENDERMAN, EntityType.BLAZE, EntityType.ZOMBIE_VILLAGER};
            type = options[random.nextInt(options.length)];
        } else {
            // Mixed rounds with all types
            EntityType[] allTypes = {
                EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER,
                EntityType.CREEPER, EntityType.ENDERMAN, EntityType.WITCH,
                EntityType.CAVE_SPIDER, EntityType.BLAZE, EntityType.HUSK,
                EntityType.STRAY, EntityType.ZOMBIE_VILLAGER
            };
            type = allTypes[random.nextInt(allTypes.length)];
        }

        Entity entity = location.getWorld().spawnEntity(location, type);

        // Prevent mobs from despawning and apply enhancements
        if (entity instanceof LivingEntity) {
            LivingEntity mob = (LivingEntity) entity;
            mob.setRemoveWhenFarAway(false);
            mob.setPersistent(true);

            // Add difficulty modifiers for higher rounds
            if (round >= 10) {
                double healthMultiplier = 1.0 + (round / 10.0) * 0.5;
                mob.setMaxHealth(mob.getMaxHealth() * healthMultiplier);
                mob.setHealth(mob.getMaxHealth());
            }

            // Give equipment to zombies and skeletons in higher rounds
            if (round >= 5 && (type == EntityType.ZOMBIE || type == EntityType.SKELETON)) {
                if (random.nextInt(3) == 0) { // 33% chance
                    if (type == EntityType.ZOMBIE) {
                        mob.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                    }
                    if (random.nextInt(2) == 0) {
                        mob.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                    }
                }
            }
        }

        return entity;
    }

    private void startActionBarUpdater(Game game) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getState() != GameState.ACTIVE || game.getMobsRemaining() <= 0) {
                    cancel();
                    return;
                }

                int round = game.getCurrentRound();
                int gems = game.calculateGemsForRound(round);
                int mobsLeft = game.getMobsRemaining();

                String actionBar = "§6Round #" + round + " §7+§a" + gems + " Gems §7| §c" + mobsLeft + " mobs remaining";

                for (UUID playerId : game.getPlayers()) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBar));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void startMobBoundaryChecker(Game game) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getState() != GameState.ACTIVE) {
                    cancel();
                    return;
                }

                Arena arena = game.getArena();
                int round = game.getCurrentRound();
                boolean isBossRound = round % 10 == 0;

                // Check each mob in the game
                for (Entity mob : new ArrayList<>(game.getArenaMobs())) {
                    if (mob == null || mob.isDead()) {
                        game.removeArenaMob(mob);
                        continue;
                    }

                    Location mobLoc = mob.getLocation();
                    Location blockBelow = mobLoc.clone().subtract(0, 1, 0);

                    // Only act if mob is standing on grass block AND outside arena
                    if (blockBelow.getBlock().getType() == Material.GRASS_BLOCK && !arena.contains(mobLoc)) {
                        // Remove the escaped mob from tracking
                        game.removeArenaMob(mob);

                        // Kill the mob quietly
                        mob.remove();

                        // Spawn replacement mob in arena
                        Location spawnLoc = getRandomSpawnLocation(arena);
                        Entity replacement;

                        if (isBossRound) {
                            replacement = spawnBossMob(spawnLoc, round, game.getArenaMobs().size());
                        } else {
                            replacement = spawnMobForRound(spawnLoc, round);
                        }

                        if (replacement != null) {
                            game.addArenaMob(replacement);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 60L, 60L); // Check every 3 seconds
    }

    public void onMobKilled(Entity mob, Player killer) {
        Game game = getPlayerGame(killer.getUniqueId());
        if (game == null || !game.getArenaMobs().contains(mob)) return;

        game.removeArenaMob(mob);
        game.decrementMobsRemaining();

        // Give gold nugget directly to player inventory
        killer.getInventory().addItem(new ItemStack(Material.GOLD_NUGGET, 1));

        // Check if round is complete
        if (game.getMobsRemaining() <= 0) {
            completeRound(game);
        }
    }

    private void completeRound(Game game) {
        int round = game.getCurrentRound();
        int gems = game.calculateGemsForRound(round);

        // Give gems to all players
        for (UUID playerId : game.getPlayers()) {
            game.addPlayerGems(playerId, gems);

            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendMessage("");
                player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("§a§l         ROUND " + round + " COMPLETE!");
                player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("§7Reward: §a+" + gems + " Gems");
                player.sendMessage("§7Next Round: §e" + (round + 1));
                player.sendMessage("");
                player.sendMessage("§e§lUpgrade shop is now open!");
                player.sendMessage("§7Use your §6Gold Nuggets §7to upgrade gear");
                player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

                // Update persistent gems
                var playerData = plugin.getPlayerDataManager().getPlayerData(playerId);
                if (playerData != null) {
                    playerData.addGems(gems);
                    playerData.setHighestRound(round);
                }
            }
        }

        // Check if game is won
        if (round >= plugin.getConfig().getInt("game.max-rounds", 50)) {
            winGame(game);
            return;
        }

        // Open upgrade shop
        game.setState(GameState.UPGRADE_SHOP);
        openUpgradeShop(game);
    }

    private void openUpgradeShop(Game game) {
        for (UUID playerId : game.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                upgradeShopGUI.openShop(player, game);
            }
        }

        // Auto-close after 30 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getState() == GameState.UPGRADE_SHOP) {
                    game.setCurrentRound(game.getCurrentRound() + 1);
                    game.setState(GameState.ACTIVE);
                    startRound(game);
                }
            }
        }.runTaskLater(plugin, 30 * 20L);
    }

    public void openUpgradeShop(Player player, Game game) {
        // Allow opening shop anytime during the game
        upgradeShopGUI.openShop(player, game);
    }

    private void winGame(Game game) {
        game.setState(GameState.ENDING);

        for (UUID playerId : game.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendTitle("§6§lVICTORY!", "§eYou completed all 50 rounds!", 20, 100, 20);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

                player.sendMessage("");
                player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("§6§l            VICTORY!");
                player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("§e§lCongratulations!");
                player.sendMessage("§7You have completed all §e50 rounds§7!");
                player.sendMessage("");
                player.sendMessage("§7Arena: §e" + game.getArena().getName());
                player.sendMessage("§7Players: §e" + game.getPlayers().size());
                player.sendMessage("");
                player.sendMessage("§a§lYou are a true champion!");
                player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("");

                var playerData = plugin.getPlayerDataManager().getPlayerData(playerId);
                if (playerData != null) {
                    playerData.addGameWon();
                    playerData.addGamePlayed();
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                endGame(game);
            }
        }.runTaskLater(plugin, 100L);
    }

    public void onPlayerDeath(Player player) {
        Game game = getPlayerGame(player.getUniqueId());
        if (game == null) return;

        int round = game.getCurrentRound();
        game.removePlayer(player.getUniqueId());
        playerGames.remove(player.getUniqueId());

        // Clear inventory, upgrades, and ability cooldowns when player leaves game
        player.getInventory().clear();
        upgradeShopGUI.clearPlayerUpgrades(player);
        plugin.getPlayerInteractListener().clearCooldowns(player.getUniqueId());

        player.sendMessage("");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§c§l           GAME OVER");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§7You were eliminated on §eRound " + round);
        player.sendMessage("§7Better luck next time!");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("");

        var playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (playerData != null) {
            playerData.addDeath();
            playerData.addGamePlayed();
        }

        // Check if all players are dead
        if (game.getPlayers().isEmpty()) {
            loseGame(game);
        }
    }

    private void loseGame(Game game) {
        game.setState(GameState.ENDING);
        endGame(game);
    }

    public void endGame(Game game) {
        // Clean up
        game.clearArenaMobs();
        game.clearPlacedCakes();

        // Clear items in arena
        Arena arena = game.getArena();
        for (Entity entity : arena.getWorld().getEntities()) {
            if (entity instanceof Item && arena.contains(entity.getLocation())) {
                entity.remove();
            }
        }

        // Teleport players to lobby
        Location lobby = new Location(
                Bukkit.getWorld(plugin.getConfig().getString("spawn.lobby-world", "world")),
                plugin.getConfig().getDouble("spawn.lobby-x", 0),
                plugin.getConfig().getDouble("spawn.lobby-y", 64),
                plugin.getConfig().getDouble("spawn.lobby-z", 0),
                (float) plugin.getConfig().getDouble("spawn.lobby-yaw", 0),
                (float) plugin.getConfig().getDouble("spawn.lobby-pitch", 0)
        );

        for (UUID playerId : new HashSet<>(game.getPlayers())) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.teleport(lobby);
                // Don't change gamemode - keep them in survival
                player.getInventory().clear();
                player.setHealth(20);
                player.setFoodLevel(20);
            }
            playerGames.remove(playerId);
        }

        activeGames.remove(arena);
    }

    public void endAllGames() {
        for (Game game : new ArrayList<>(activeGames.values())) {
            endGame(game);
        }
    }

    public void trackPlacedCake(Player player, Location cakeLocation) {
        Game game = getPlayerGame(player.getUniqueId());
        if (game != null) {
            game.addPlacedCake(cakeLocation);
        }
    }

    public boolean isArenaMob(Entity entity) {
        for (Game game : activeGames.values()) {
            if (game.getArenaMobs().contains(entity)) {
                return true;
            }
        }
        return false;
    }

    public void onMobKilledByOther(Entity mob) {
        // Find which game this mob belongs to
        for (Game game : activeGames.values()) {
            if (game.getArenaMobs().contains(mob)) {
                game.removeArenaMob(mob);
                game.decrementMobsRemaining();

                // Spawn replacement mob to maintain count
                if (game.getState() == GameState.ACTIVE) {
                    Location spawnLoc = getRandomSpawnLocation(game.getArena());
                    Entity replacement = spawnMobForRound(spawnLoc, game.getCurrentRound());
                    if (replacement != null) {
                        game.addArenaMob(replacement);
                        game.setMobsRemaining(game.getMobsRemaining() + 1); // Compensate for decrement
                    }
                }
                break;
            }
        }
    }
}
