package com.legion.mobarena;

import com.legion.mobarena.commands.AdminCommand;
import com.legion.mobarena.commands.ArenaCommand;
import com.legion.mobarena.database.DatabaseManager;
import com.legion.mobarena.handlers.GameManager;
import com.legion.mobarena.handlers.ArenaManager;
import com.legion.mobarena.handlers.KitManager;
import com.legion.mobarena.handlers.PlayerDataManager;
import com.legion.mobarena.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;

public class LegionMobArena extends JavaPlugin {

    private static LegionMobArena instance;
    private DatabaseManager databaseManager;
    private GameManager gameManager;
    private ArenaManager arenaManager;
    private KitManager kitManager;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config
        saveDefaultConfig();

        // Initialize managers
        this.databaseManager = new DatabaseManager(this);
        this.arenaManager = new ArenaManager(this);
        this.kitManager = new KitManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.gameManager = new GameManager(this);

        // Connect to database
        databaseManager.connect();

        // Register commands
        getCommand("legion").setExecutor(new ArenaCommand(this));
        getCommand("legionadmin").setExecutor(new AdminCommand(this));

        // Register listeners
        registerListeners();

        getLogger().info("Legion Mob Arena has been enabled!");
    }

    @Override
    public void onDisable() {
        // End all active games
        if (gameManager != null) {
            gameManager.endAllGames();
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
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
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
}
