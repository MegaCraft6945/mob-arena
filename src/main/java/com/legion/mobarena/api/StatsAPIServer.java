package com.legion.mobarena.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.PlayerData;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Embedded HTTP server that exposes leaderboard data via REST API
 */
public class StatsAPIServer {

    private final LegionMobArena plugin;
    private final Gson gson;
    private Javalin app;
    private final int port;

    public StatsAPIServer(LegionMobArena plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.port = plugin.getConfig().getInt("api.port", 25566);
    }

    /**
     * Start the HTTP server
     */
    public void start() {
        if (!plugin.getConfig().getBoolean("api.enabled", false)) {
            plugin.getLogger().info("Stats API is disabled in config");
            return;
        }

        try {
            app = Javalin.create(config -> {
                config.showJavalinBanner = false;
            }).start("0.0.0.0", port); // Bind to all interfaces (0.0.0.0) for Docker/Pterodactyl

            // CORS middleware
            app.before(ctx -> {
                ctx.header("Access-Control-Allow-Origin", "*");
                ctx.header("Access-Control-Allow-Methods", "GET, OPTIONS");
                ctx.header("Access-Control-Allow-Headers", "Content-Type");
            });

            // Register routes
            app.get("/api/stats", this::getStats);
            app.get("/api/leaderboard", this::getLeaderboard);
            app.get("/api/player/{uuid}", this::getPlayerStats);
            app.options("/*", ctx -> ctx.status(200));

            plugin.getLogger().info("Stats API server started on 0.0.0.0:" + port);
            plugin.getLogger().info("API Endpoints:");
            plugin.getLogger().info("  - http://0.0.0.0:" + port + "/api/stats");
            plugin.getLogger().info("  - http://0.0.0.0:" + port + "/api/leaderboard");
            plugin.getLogger().info("  - http://0.0.0.0:" + port + "/api/player/{uuid}");
            plugin.getLogger().info("For Pterodactyl/Docker, use your server IP to access the API");

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to start Stats API server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stop the HTTP server
     */
    public void stop() {
        if (app != null) {
            app.stop();
            plugin.getLogger().info("Stats API server stopped");
        }
    }

    /**
     * GET /api/stats - Server summary statistics
     */
    private void getStats(Context ctx) {
        Map<String, Object> stats = new HashMap<>();
        Map<UUID, PlayerData> allData = plugin.getPlayerDataManager().getAllPlayerData();

        stats.put("totalPlayers", allData.size());
        stats.put("totalGames", allData.values().stream().mapToInt(PlayerData::getGamesPlayed).sum());
        stats.put("totalKills", allData.values().stream().mapToInt(PlayerData::getTotalKills).sum());
        stats.put("highestRound", allData.values().stream().mapToInt(PlayerData::getHighestRound).max().orElse(0));
        stats.put("serverName", plugin.getConfig().getString("api.server-name", "Legion Mob Arena"));
        stats.put("timestamp", System.currentTimeMillis());

        ctx.json(stats);
    }

    /**
     * GET /api/leaderboard - Full leaderboard with all players
     */
    private void getLeaderboard(Context ctx) {
        List<Map<String, Object>> leaderboard = new ArrayList<>();
        Map<UUID, PlayerData> allData = plugin.getPlayerDataManager().getAllPlayerData();

        for (PlayerData data : allData.values()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("playerName", data.getName());
            entry.put("playerId", data.getUuid().toString());
            entry.put("highestRound", data.getHighestRound());
            entry.put("totalGamesPlayed", data.getGamesPlayed());
            entry.put("totalKills", data.getTotalKills());
            entry.put("totalGemsEarned", data.getGems());
            entry.put("lastPlayed", data.getLastSeen());
            leaderboard.add(entry);
        }

        // Sort by highest round, then kills
        leaderboard.sort((a, b) -> {
            int roundCompare = Integer.compare((int) b.get("highestRound"), (int) a.get("highestRound"));
            if (roundCompare != 0) return roundCompare;
            return Integer.compare((int) b.get("totalKills"), (int) a.get("totalKills"));
        });

        ctx.json(leaderboard);
    }

    /**
     * GET /api/player/{uuid} - Individual player stats
     */
    private void getPlayerStats(Context ctx) {
        String uuidStr = ctx.pathParam("uuid");

        try {
            UUID uuid = UUID.fromString(uuidStr);
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(uuid);

            if (data == null) {
                ctx.status(404).json(Map.of("error", "Player not found"));
                return;
            }

            Map<String, Object> playerStats = new HashMap<>();
            playerStats.put("playerName", data.getName());
            playerStats.put("playerId", data.getUuid().toString());
            playerStats.put("gems", data.getGems());
            playerStats.put("highestRound", data.getHighestRound());
            playerStats.put("gamesPlayed", data.getGamesPlayed());
            playerStats.put("gamesWon", data.getGamesWon());
            playerStats.put("totalKills", data.getTotalKills());
            playerStats.put("totalDeaths", data.getTotalDeaths());
            playerStats.put("kdr", data.getTotalDeaths() > 0 ?
                    (double) data.getTotalKills() / data.getTotalDeaths() : data.getTotalKills());
            playerStats.put("winRate", data.getGamesPlayed() > 0 ?
                    (double) data.getGamesWon() / data.getGamesPlayed() * 100 : 0);
            playerStats.put("unlockedKits", data.getUnlockedKits().size());
            playerStats.put("lastSeen", data.getLastSeen());

            ctx.json(playerStats);

        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid UUID format"));
        }
    }
}
