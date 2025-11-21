package com.legion.mobarena;

import com.legion.mobarena.api.StatsAPIServer;
import com.legion.mobarena.commands.AdminCommand;
import com.legion.mobarena.commands.ArenaCommand;
import com.legion.mobarena.database.DatabaseManager;
import com.legion.mobarena.gui.AdminMenuGUI;
import com.legion.mobarena.handlers.GameManager;
import com.legion.mobarena.handlers.ArenaManager;
import com.legion.mobarena.handlers.KitManager;
import com.legion.mobarena.handlers.LeaderboardManager;
import com.legion.mobarena.handlers.PlayerDataManager;
import com.legion.mobarena.listeners.*;
import com.legion.mobarena.placeholders.LegionPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LegionMobArena extends JavaPlugin {

    private static LegionMobArena instance;
    private DatabaseManager databaseManager;
    private GameManager gameManager;
    private ArenaManager arenaManager;
    private KitManager kitManager;
    private LeaderboardManager leaderboardManager;
    private PlayerDataManager playerDataManager;
    private StatsAPIServer apiServer;
    private FileConfiguration messagesConfig;
    private PlayerInteractListener playerInteractListener;
    private AdminMenuGUI adminMenuGUI;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config
        saveDefaultConfig();

        // Load messages.yml
        loadMessagesConfig();

        // Initialize managers
        this.databaseManager = new DatabaseManager(this);
        this.arenaManager = new ArenaManager(this);
        this.kitManager = new KitManager(this);
        this.leaderboardManager = new LeaderboardManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.gameManager = new GameManager(this);

        // Connect to database
        databaseManager.connect();

        // Register commands
        getCommand("legion").setExecutor(new ArenaCommand(this));
        AdminCommand adminCommand = new AdminCommand(this);
        this.adminMenuGUI = adminCommand.getAdminMenu();
        getCommand("legionadmin").setExecutor(adminCommand);

        // Register listeners
        registerListeners();

        // Register PlaceholderAPI expansion
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LegionPlaceholders(this).register();
            getLogger().info("PlaceholderAPI expansion registered!");
        }

        // Start Stats API server
        this.apiServer = new StatsAPIServer(this);
        apiServer.start();

        getLogger().info("Legion Mob Arena has been enabled!");
    }

    @Override
    public void onDisable() {
        // Stop Stats API server
        if (apiServer != null) {
            apiServer.stop();
        }

        // End all active games
        if (gameManager != null) {
            gameManager.endAllGames();
        }

        // Save leaderboard before shutdown
        if (leaderboardManager != null) {
            leaderboardManager.saveLeaderboard();
        }

        // Disconnect from database
        if (databaseManager != null) {
            databaseManager.disconnect();
        }

        getLogger().info("Legion Mob Arena has been disabled!");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new EntitySpawnListener(this), this);

        // Store PlayerInteractListener so GameManager can access it for cooldown clearing
        this.playerInteractListener = new PlayerInteractListener(this);
        getServer().getPluginManager().registerEvents(playerInteractListener, this);

        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryDragListener(this), this);
        getServer().getPluginManager().registerEvents(new AdminBlockClickListener(this), this);
    }

    public PlayerInteractListener getPlayerInteractListener() {
        return playerInteractListener;
    }

    public static LegionMobArena getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    private void loadMessagesConfig() {
        File messagesFile = new File(getDataFolder(), "messages.yml");

        // Create messages.yml if it doesn't exist
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String path) {
        return messagesConfig.getString(path, "§cMessage not found: " + path);
    }

    public AdminMenuGUI getAdminMenu() {
        return adminMenuGUI;
    }
}
