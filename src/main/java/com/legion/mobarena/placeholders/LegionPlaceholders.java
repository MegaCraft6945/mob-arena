package com.legion.mobarena.placeholders;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LegionPlaceholders extends PlaceholderExpansion {

    private final LegionMobArena plugin;

    public LegionPlaceholders(LegionMobArena plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "legion";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) {
            return "0";
        }

        // Player stats
        switch (params.toLowerCase()) {
            case "gems":
                return String.valueOf(data.getGems());

            case "highest_round":
                return String.valueOf(data.getHighestRound());

            case "total_kills":
            case "kills":
                return String.valueOf(data.getTotalKills());

            case "total_deaths":
            case "deaths":
                return String.valueOf(data.getTotalDeaths());

            case "kdr":
                double kdr = data.getTotalDeaths() > 0
                    ? (double) data.getTotalKills() / data.getTotalDeaths()
                    : data.getTotalKills();
                return String.format("%.2f", kdr);

            case "games_played":
                return String.valueOf(data.getGamesPlayed());

            case "games_won":
            case "wins":
                return String.valueOf(data.getGamesWon());

            case "winrate":
                double winRate = data.getGamesPlayed() > 0
                    ? (double) data.getGamesWon() / data.getGamesPlayed() * 100
                    : 0;
                return String.format("%.1f", winRate);

            case "unlocked_kits":
                return String.valueOf(data.getUnlockedKits().size());
        }

        // Leaderboard placeholders
        if (params.startsWith("top_kills_")) {
            return getLeaderboardValue(params, "kills");
        } else if (params.startsWith("top_games_")) {
            return getLeaderboardValue(params, "games");
        } else if (params.startsWith("top_wins_")) {
            return getLeaderboardValue(params, "wins");
        }

        return null;
    }

    private String getLeaderboardValue(String params, String type) {
        String[] parts = params.split("_");
        if (parts.length < 3) return "";

        try {
            int position = Integer.parseInt(parts[2]);
            String valueType = parts.length > 3 ? parts[3] : "name";

            Map<String, Integer> leaderboard;
            switch (type) {
                case "kills":
                    leaderboard = plugin.getPlayerDataManager().getTopKills(10);
                    break;
                case "games":
                    leaderboard = plugin.getPlayerDataManager().getTopGamesPlayed(10);
                    break;
                case "wins":
                    leaderboard = plugin.getPlayerDataManager().getTopWins(10);
                    break;
                default:
                    return "";
            }

            List<Map.Entry<String, Integer>> entries = new ArrayList<>(leaderboard.entrySet());
            if (position < 1 || position > entries.size()) {
                return valueType.equals("name") ? "-" : "0";
            }

            Map.Entry<String, Integer> entry = entries.get(position - 1);
            return valueType.equals("name") ? entry.getKey() : String.valueOf(entry.getValue());

        } catch (NumberFormatException e) {
            return "";
        }
    }
}
