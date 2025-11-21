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

        // Player rank placeholders
        if (params.equals("rank_kills")) {
            return String.valueOf(getPlayerRank(player.getUniqueId(), "kills"));
        } else if (params.equals("rank_rounds")) {
            return String.valueOf(getPlayerRank(player.getUniqueId(), "rounds"));
        } else if (params.equals("rank_games")) {
            return String.valueOf(getPlayerRank(player.getUniqueId(), "games"));
        } else if (params.equals("rank_wins")) {
            return String.valueOf(getPlayerRank(player.getUniqueId(), "wins"));
        }

        // Leaderboard placeholders
        if (params.startsWith("top_")) {
            if (params.startsWith("top_kills_")) {
                return getLeaderboardValue(params, "kills");
            } else if (params.startsWith("top_rounds_")) {
                return getLeaderboardValue(params, "rounds");
            } else if (params.startsWith("top_games_")) {
                return getLeaderboardValue(params, "games");
            } else if (params.startsWith("top_wins_")) {
                return getLeaderboardValue(params, "wins");
            } else if (params.startsWith("top_deaths_")) {
                return getLeaderboardValue(params, "deaths");
            } else if (params.startsWith("top_kdr_")) {
                return getLeaderboardValue(params, "kdr");
            } else if (params.startsWith("top_winrate_")) {
                return getLeaderboardValue(params, "winrate");
            } else if (params.startsWith("top_gems_")) {
                return getLeaderboardValue(params, "gems");
            }
        }

        return null;
    }

    private String getLeaderboardValue(String params, String type) {
        String[] parts = params.split("_");
        if (parts.length < 3) return "";

        try {
            int position = Integer.parseInt(parts[2]);
            String valueType = parts.length > 3 ? parts[3] : "name";

            // Get appropriate leaderboard
            List<PlayerData> sortedPlayers = getSortedPlayers(type);

            if (position < 1 || position > sortedPlayers.size()) {
                return valueType.equals("name") ? "-" : "0";
            }

            PlayerData playerData = sortedPlayers.get(position - 1);

            if (valueType.equals("name")) {
                return playerData.getName();
            } else {
                return getStatValue(playerData, type);
            }

        } catch (NumberFormatException e) {
            return "";
        }
    }

    private List<PlayerData> getSortedPlayers(String type) {
        List<PlayerData> players = new ArrayList<>(plugin.getPlayerDataManager().getAllPlayerData().values());

        switch (type) {
            case "kills":
                players.sort((a, b) -> Integer.compare(b.getTotalKills(), a.getTotalKills()));
                break;
            case "rounds":
                players.sort((a, b) -> Integer.compare(b.getHighestRound(), a.getHighestRound()));
                break;
            case "games":
                players.sort((a, b) -> Integer.compare(b.getGamesPlayed(), a.getGamesPlayed()));
                break;
            case "wins":
                players.sort((a, b) -> Integer.compare(b.getGamesWon(), a.getGamesWon()));
                break;
            case "deaths":
                players.sort((a, b) -> Integer.compare(b.getTotalDeaths(), a.getTotalDeaths()));
                break;
            case "gems":
                players.sort((a, b) -> Integer.compare(b.getGems(), a.getGems()));
                break;
            case "kdr":
                players.sort((a, b) -> {
                    double kdrA = a.getTotalDeaths() > 0 ? (double) a.getTotalKills() / a.getTotalDeaths() : a.getTotalKills();
                    double kdrB = b.getTotalDeaths() > 0 ? (double) b.getTotalKills() / b.getTotalDeaths() : b.getTotalKills();
                    return Double.compare(kdrB, kdrA);
                });
                break;
            case "winrate":
                players.sort((a, b) -> {
                    double wrA = a.getGamesPlayed() > 0 ? (double) a.getGamesWon() / a.getGamesPlayed() * 100 : 0;
                    double wrB = b.getGamesPlayed() > 0 ? (double) b.getGamesWon() / b.getGamesPlayed() * 100 : 0;
                    return Double.compare(wrB, wrA);
                });
                break;
        }

        return players;
    }

    private String getStatValue(PlayerData data, String type) {
        switch (type) {
            case "kills":
                return String.valueOf(data.getTotalKills());
            case "rounds":
                return String.valueOf(data.getHighestRound());
            case "games":
                return String.valueOf(data.getGamesPlayed());
            case "wins":
                return String.valueOf(data.getGamesWon());
            case "deaths":
                return String.valueOf(data.getTotalDeaths());
            case "gems":
                return String.valueOf(data.getGems());
            case "kdr":
                double kdr = data.getTotalDeaths() > 0 ? (double) data.getTotalKills() / data.getTotalDeaths() : data.getTotalKills();
                return String.format("%.2f", kdr);
            case "winrate":
                double winrate = data.getGamesPlayed() > 0 ? (double) data.getGamesWon() / data.getGamesPlayed() * 100 : 0;
                return String.format("%.1f", winrate);
            default:
                return "0";
        }
    }

    private int getPlayerRank(java.util.UUID uuid, String type) {
        List<PlayerData> sortedPlayers = getSortedPlayers(type);

        for (int i = 0; i < sortedPlayers.size(); i++) {
            if (sortedPlayers.get(i).getUuid().equals(uuid)) {
                return i + 1;
            }
        }

        return sortedPlayers.size() + 1;
    }
}
