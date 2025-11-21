package com.legion.mobarena.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.PlayerData;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

        // Validate port is not the Minecraft server port
        if (port == 25565) {
            plugin.getLogger().severe("========================================");
            plugin.getLogger().severe("ERROR: API port is set to 25565!");
            plugin.getLogger().severe("Port 25565 is used by the Minecraft server.");
            plugin.getLogger().severe("Please change 'api.port' in config.yml to a different port (recommended: 25566)");
            plugin.getLogger().severe("========================================");
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
            app.get("/", this::serveStatsPage);
            app.get("/api/stats", this::getStats);
            app.get("/api/leaderboard", this::getLeaderboard);
            app.get("/api/player/{uuid}", this::getPlayerStats);
            app.options("/*", ctx -> ctx.status(200));

            plugin.getLogger().info("========================================");
            plugin.getLogger().info("Stats API started successfully!");
            plugin.getLogger().info("Port: " + port);
            plugin.getLogger().info("Local: http://localhost:" + port + "/");
            plugin.getLogger().info("Web: http://YOUR-SERVER-IP:" + port + "/");
            plugin.getLogger().info("========================================");

        } catch (Exception e) {
            plugin.getLogger().severe("========================================");
            plugin.getLogger().severe("Failed to start Stats API server!");
            plugin.getLogger().severe("Error: " + e.getMessage());

            if (e.getMessage().contains("Port already in use") || e.getMessage().contains("Address already in use")) {
                plugin.getLogger().severe("");
                plugin.getLogger().severe("Port " + port + " is already in use by another application.");
                plugin.getLogger().severe("Solutions:");
                plugin.getLogger().severe("1. Change 'api.port' in config.yml to a different port");
                plugin.getLogger().severe("2. Stop the other application using port " + port);
                plugin.getLogger().severe("3. If using port 25565, that's the Minecraft server port - use 25566 instead");
            }
            plugin.getLogger().severe("========================================");
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
     * GET / - Show API information and website setup instructions
     */
    private void serveStatsPage(Context ctx) {
        String serverName = plugin.getConfig().getString("api.server-name", "Legion Mob Arena");
        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s - API Server</title>
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            min-height: 100vh;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            padding: 20px;
                            margin: 0;
                        }
                        .container {
                            background: white;
                            border-radius: 15px;
                            padding: 40px;
                            max-width: 800px;
                            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
                        }
                        h1 { color: #667eea; margin-bottom: 10px; }
                        h2 { color: #764ba2; margin-top: 30px; margin-bottom: 15px; font-size: 1.3em; }
                        .status {
                            background: #d4edda;
                            color: #155724;
                            padding: 15px;
                            border-radius: 8px;
                            margin: 20px 0;
                            border: 1px solid #c3e6cb;
                        }
                        .info {
                            background: #d1ecf1;
                            color: #0c5460;
                            padding: 15px;
                            border-radius: 8px;
                            margin: 20px 0;
                            border: 1px solid #bee5eb;
                        }
                        .endpoint {
                            background: #f8f9fa;
                            padding: 10px 15px;
                            border-left: 4px solid #667eea;
                            margin: 10px 0;
                            font-family: monospace;
                        }
                        .endpoint a {
                            color: #667eea;
                            text-decoration: none;
                        }
                        .endpoint a:hover {
                            text-decoration: underline;
                        }
                        code {
                            background: #f4f4f4;
                            padding: 2px 6px;
                            border-radius: 3px;
                            font-family: monospace;
                        }
                        ul {
                            line-height: 1.8;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>⚔️ %s</h1>
                        <div class="status">
                            <strong>✅ API Server is Running</strong>
                        </div>

                        <h2>📊 Available API Endpoints</h2>
                        <div class="endpoint">
                            <a href="/api/stats">/api/stats</a> - Server summary statistics
                        </div>
                        <div class="endpoint">
                            <a href="/api/leaderboard">/api/leaderboard</a> - Full player leaderboard
                        </div>
                        <div class="endpoint">
                            /api/player/{uuid} - Individual player stats
                        </div>

                        <h2>🌐 Website Setup</h2>
                        <div class="info">
                            <p><strong>Want a beautiful stats website?</strong></p>
                            <p>The standalone website files are available in the plugin's GitHub repository:</p>
                            <ol>
                                <li>Download the <code>website/</code> folder from GitHub</li>
                                <li>Edit <code>config.js</code> with your server's IP address</li>
                                <li>Upload to any web hosting (InfinityFree, GitHub Pages, etc.)</li>
                            </ol>
                            <p>See <code>website/README.md</code> for full instructions.</p>
                        </div>

                        <h2>🔧 API Information</h2>
                        <ul>
                            <li><strong>CORS:</strong> Enabled (allows remote websites to access this API)</li>
                            <li><strong>Format:</strong> JSON</li>
                            <li><strong>Authentication:</strong> None (read-only public API)</li>
                        </ul>
                    </div>
                </body>
                </html>
                """.formatted(serverName, serverName);

        ctx.contentType("text/html").result(html);
    }

    /**
     * GET /api/stats - Server summary statistics
     */
    private void getStats(Context ctx) {
        List<PlayerData> allData = plugin.getPlayerDataManager().getAllPlayerDataFromDatabase();

        Map<String, Object> stats = new HashMap<>();

        // Basic stats
        stats.put("totalPlayers", allData.size());
        stats.put("totalGames", allData.stream().mapToInt(PlayerData::getGamesPlayed).sum());
        stats.put("totalGamesWon", allData.stream().mapToInt(PlayerData::getGamesWon).sum());
        stats.put("totalKills", allData.stream().mapToInt(PlayerData::getTotalKills).sum());
        stats.put("totalDeaths", allData.stream().mapToInt(PlayerData::getTotalDeaths).sum());
        stats.put("totalGems", allData.stream().mapToInt(PlayerData::getGems).sum());
        stats.put("highestRound", allData.stream().mapToInt(PlayerData::getHighestRound).max().orElse(0));

        // Server info
        stats.put("serverName", plugin.getConfig().getString("api.server-name", "Legion Mob Arena"));
        stats.put("timestamp", System.currentTimeMillis());

        ctx.json(stats);
    }

    /**
     * GET /api/leaderboard - Full leaderboard with all players
     */
    private void getLeaderboard(Context ctx) {
        List<PlayerData> allData = plugin.getPlayerDataManager().getAllPlayerDataFromDatabase();
        List<Map<String, Object>> leaderboard = new ArrayList<>();

        for (PlayerData data : allData) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("playerName", data.getName());
            entry.put("playerId", data.getUuid().toString());
            entry.put("highestRound", data.getHighestRound());
            entry.put("totalGamesPlayed", data.getGamesPlayed());
            entry.put("gamesWon", data.getGamesWon());
            entry.put("totalKills", data.getTotalKills());
            entry.put("totalDeaths", data.getTotalDeaths());
            entry.put("totalGemsEarned", data.getGems());
            entry.put("unlockedKits", data.getUnlockedKits().size());
            entry.put("lastPlayed", data.getLastSeen());

            // Calculated stats
            double kdr = data.getTotalDeaths() > 0 ?
                    (double) data.getTotalKills() / data.getTotalDeaths() : data.getTotalKills();
            double winRate = data.getGamesPlayed() > 0 ?
                    (double) data.getGamesWon() / data.getGamesPlayed() * 100 : 0;

            entry.put("kdr", Math.round(kdr * 100.0) / 100.0);
            entry.put("winRate", Math.round(winRate * 10.0) / 10.0);

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

            // First check cache for online player
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(uuid);

            // If not in cache, load from database
            if (data == null) {
                List<PlayerData> allPlayers = plugin.getPlayerDataManager().getAllPlayerDataFromDatabase();
                data = allPlayers.stream()
                        .filter(p -> p.getUuid().equals(uuid))
                        .findFirst()
                        .orElse(null);
            }

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
