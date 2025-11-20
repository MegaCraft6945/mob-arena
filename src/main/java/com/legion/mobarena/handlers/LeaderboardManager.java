package com.legion.mobarena.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.PlayerData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardManager {

    private final LegionMobArena plugin;
    private final File leaderboardFile;
    private final Gson gson;

    public LeaderboardManager(LegionMobArena plugin) {
        this.plugin = plugin;
        this.leaderboardFile = new File(plugin.getDataFolder(), "leaderboard.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Export current PlayerData to leaderboard.json for GitHub Pages
     */
    public void saveLeaderboard() {
        try (FileWriter writer = new FileWriter(leaderboardFile)) {
            List<LeaderboardData> entries = new ArrayList<>();

            // Get all player data and convert to leaderboard format
            for (PlayerData data : plugin.getPlayerDataManager().getAllPlayerData().values()) {
                LeaderboardData entry = new LeaderboardData();
                entry.playerName = data.getName();
                entry.playerId = data.getUuid().toString();
                entry.highestRound = data.getHighestRound();
                entry.totalGamesPlayed = data.getGamesPlayed();
                entry.totalKills = data.getTotalKills();
                entry.totalGemsEarned = data.getGems();
                entry.lastPlayed = data.getLastSeen();
                entries.add(entry);
            }

            // Sort by highest round, then by kills
            entries.sort((a, b) -> {
                if (b.highestRound != a.highestRound) {
                    return Integer.compare(b.highestRound, a.highestRound);
                }
                return Integer.compare(b.totalKills, a.totalKills);
            });

            gson.toJson(entries, writer);
            plugin.getLogger().info("Exported " + entries.size() + " players to leaderboard.json");
        } catch (IOException e) {
            plugin.getLogger().severe("Error saving leaderboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Data class for JSON serialization
     */
    private static class LeaderboardData {
        String playerName;
        String playerId;
        int highestRound;
        int totalGamesPlayed;
        int totalKills;
        int totalGemsEarned;
        long lastPlayed;
    }
}
