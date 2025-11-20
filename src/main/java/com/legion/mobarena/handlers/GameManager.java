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
        player.setGameMode(GameMode.ADVENTURE);

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
        player.getInventory().setArmorContents(kit.getArmor());

        for (ItemStack item : kit.getItems()) {
            player.getInventory().addItem(item);
        }

        if (kit.getAbilityItem() != null) {
            player.getInventory().addItem(kit.getAbilityItem());
        }
    }

    private void startRound(Game game) {
        int round = game.getCurrentRound();
        int mobCount = game.calculateMobsForRound(round);
        game.setMobsRemaining(mobCount);

        // Broadcast round start
        for (UUID playerId : game.getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendMessage("§6§lRound " + round + " Started!");
                player.sendTitle("§6Round " + round, "§eKill " + mobCount + " mobs!", 10, 70, 20);
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5f, 1f);
            }
        }

        // Spawn mobs
        spawnRoundMobs(game);

        // Start action bar updater
        startActionBarUpdater(game);
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

    private Location getRandomSpawnLocation(Arena arena) {
        Location min = arena.getMin();
        Location max = arena.getMax();
        Random random = new Random();

        double x = min.getX() + random.nextDouble() * (max.getX() - min.getX());
        double z = min.getZ() + random.nextDouble() * (max.getZ() - min.getZ());
        double y = min.getWorld().getHighestBlockYAt((int) x, (int) z) + 1;

        return new Location(min.getWorld(), x, y, z);
    }

    private Entity spawnMobForRound(Location location, int round) {
        EntityType[] mobTypes = {
                EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER,
                EntityType.CREEPER, EntityType.ENDERMAN, EntityType.WITCH
        };

        // Higher rounds have tougher mobs
        int maxIndex = Math.min(round / 5 + 2, mobTypes.length);
        EntityType type = mobTypes[new Random().nextInt(maxIndex)];

        Entity entity = location.getWorld().spawnEntity(location, type);

        // Prevent mobs from despawning
        if (entity instanceof LivingEntity) {
            LivingEntity mob = (LivingEntity) entity;
            mob.setRemoveWhenFarAway(false);
            mob.setPersistent(true);
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
                player.sendMessage(plugin.getConfig().getString("messages.round-complete")
                        .replace("{round}", String.valueOf(round))
                        .replace("{gems}", String.valueOf(gems)));
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
                player.sendMessage(plugin.getConfig().getString("messages.game-won"));

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

        game.removePlayer(player.getUniqueId());
        playerGames.remove(player.getUniqueId());

        var playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (playerData != null) {
            playerData.addDeath();
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
                player.setGameMode(GameMode.SURVIVAL);
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
}
