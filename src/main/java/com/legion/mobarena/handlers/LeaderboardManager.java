package com.legion.mobarena.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.LeaderboardEntry;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardManager {

    private final LegionMobArena plugin;
    private final File leaderboardFile;
    private final Map<UUID, LeaderboardEntry> leaderboard;
    private final Gson gson;

    public LeaderboardManager(LegionMobArena plugin) {
        this.plugin = plugin;
        this.leaderboardFile = new File(plugin.getDataFolder(), "leaderboard.json");
        this.leaderboard = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        if (!leaderboardFile.exists()) {
            plugin.getLogger().info("No leaderboard file found, creating new one");
            return;
        }

        try (FileReader reader = new FileReader(leaderboardFile)) {
            Type listType = new TypeToken<List<LeaderboardEntry>>(){}.getType();
            List<LeaderboardEntry> entries = gson.fromJson(reader, listType);

            if (entries != null) {
                for (LeaderboardEntry entry : entries) {
                    leaderboard.put(entry.getPlayerId(), entry);
                }
                plugin.getLogger().info("Loaded " + leaderboard.size() + " leaderboard entries");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Error loading leaderboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveLeaderboard() {
        try (FileWriter writer = new FileWriter(leaderboardFile)) {
            List<LeaderboardEntry> entries = new ArrayList<>(leaderboard.values());
            Collections.sort(entries);
            gson.toJson(entries, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Error saving leaderboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updatePlayerStats(Player player, int round, int kills, int gems) {
        LeaderboardEntry entry = leaderboard.computeIfAbsent(
            player.getUniqueId(),
            uuid -> new LeaderboardEntry(player.getName(), uuid)
        );

        entry.updateStats(round, kills, gems);
        saveLeaderboard();
    }

    public List<LeaderboardEntry> getTopPlayers(int limit) {
        return leaderboard.values().stream()
                .sorted()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public LeaderboardEntry getPlayerEntry(UUID playerId) {
        return leaderboard.get(playerId);
    }

    public int getPlayerRank(UUID playerId) {
        List<LeaderboardEntry> sortedEntries = new ArrayList<>(leaderboard.values());
        Collections.sort(sortedEntries);

        for (int i = 0; i < sortedEntries.size(); i++) {
            if (sortedEntries.get(i).getPlayerId().equals(playerId)) {
                return i + 1;
            }
        }
        return -1;
    }

    public Map<String, Object> getStatsSummary() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPlayers", leaderboard.size());
        stats.put("totalGamesPlayed", leaderboard.values().stream()
                .mapToInt(LeaderboardEntry::getTotalGamesPlayed).sum());
        stats.put("totalKills", leaderboard.values().stream()
                .mapToInt(LeaderboardEntry::getTotalKills).sum());
        stats.put("highestRoundEver", leaderboard.values().stream()
                .mapToInt(LeaderboardEntry::getHighestRound).max().orElse(0));
        stats.put("lastUpdated", System.currentTimeMillis());
        return stats;
    }
}
