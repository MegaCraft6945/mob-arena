package com.legion.mobarena.handlers;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final LegionMobArena plugin;
    private final Map<UUID, PlayerData> playerDataCache;

    public PlayerDataManager(LegionMobArena plugin) {
        this.plugin = plugin;
        this.playerDataCache = new HashMap<>();
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataCache.get(uuid);
    }

    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection conn = plugin.getDatabaseManager().getConnection();

                PlayerData data;

                if (conn == null) {
                    // Database disabled - create in-memory player data
                    data = new PlayerData(uuid, player.getName());
                    data.unlockKit("warrior"); // Free kit

                    // Cache immediately on main thread
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        playerDataCache.put(uuid, data);
                    });
                    return;
                }

                // Load player data from database
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM player_data WHERE uuid = ?"
                );
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    data = new PlayerData(uuid, player.getName());
                    data.setGems(rs.getInt("gems"));
                    data.setTotalKills(rs.getInt("total_kills"));
                    data.setTotalDeaths(rs.getInt("total_deaths"));
                    data.setHighestRound(rs.getInt("highest_round"));
                    data.setGamesPlayed(rs.getInt("games_played"));
                    data.setGamesWon(rs.getInt("games_won"));
                    data.setLastSeen(rs.getLong("last_seen"));
                } else {
                    data = new PlayerData(uuid, player.getName());
                    // Insert new player
                    ps = conn.prepareStatement(
                            "INSERT INTO player_data (uuid, name, gems, total_kills, total_deaths, " +
                                    "highest_round, games_played, games_won, last_seen) " +
                                    "VALUES (?, ?, 0, 0, 0, 0, 0, 0, ?)"
                    );
                    ps.setString(1, uuid.toString());
                    ps.setString(2, player.getName());
                    ps.setLong(3, System.currentTimeMillis());
                    ps.executeUpdate();
                }
                rs.close();
                ps.close();

                // Load unlocked kits
                ps = conn.prepareStatement(
                        "SELECT kit_name FROM player_kits WHERE uuid = ?"
                );
                ps.setString(1, uuid.toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    data.unlockKit(rs.getString("kit_name"));
                }
                rs.close();
                ps.close();

                // Always ensure the free warrior kit is unlocked
                data.unlockKit("warrior");

                // Cache the data
                Bukkit.getScheduler().runTask(plugin, () -> {
                    playerDataCache.put(uuid, data);
                });

            } catch (SQLException e) {
                plugin.getLogger().severe("Error loading player data for " + player.getName());
                e.printStackTrace();
            }
        });
    }

    public void savePlayerData(UUID uuid) {
        PlayerData data = playerDataCache.get(uuid);
        if (data == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection conn = plugin.getDatabaseManager().getConnection();
                if (conn == null) return;

                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE player_data SET name = ?, gems = ?, total_kills = ?, " +
                                "total_deaths = ?, highest_round = ?, games_played = ?, " +
                                "games_won = ?, last_seen = ? WHERE uuid = ?"
                );
                ps.setString(1, data.getName());
                ps.setInt(2, data.getGems());
                ps.setInt(3, data.getTotalKills());
                ps.setInt(4, data.getTotalDeaths());
                ps.setInt(5, data.getHighestRound());
                ps.setInt(6, data.getGamesPlayed());
                ps.setInt(7, data.getGamesWon());
                ps.setLong(8, System.currentTimeMillis());
                ps.setString(9, uuid.toString());
                ps.executeUpdate();
                ps.close();

            } catch (SQLException e) {
                plugin.getLogger().severe("Error saving player data for " + uuid);
                e.printStackTrace();
            }
        });
    }

    public void unlockKit(UUID uuid, String kitName) {
        PlayerData data = playerDataCache.get(uuid);
        if (data == null) return;

        data.unlockKit(kitName);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection conn = plugin.getDatabaseManager().getConnection();
                if (conn == null) return;

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO player_kits (uuid, kit_name, unlocked_at) VALUES (?, ?, ?)"
                );
                ps.setString(1, uuid.toString());
                ps.setString(2, kitName);
                ps.setLong(3, System.currentTimeMillis());
                ps.executeUpdate();
                ps.close();

            } catch (SQLException e) {
                plugin.getLogger().severe("Error unlocking kit for " + uuid);
                e.printStackTrace();
            }
        });
    }

    public void unloadPlayerData(UUID uuid) {
        savePlayerData(uuid);
        playerDataCache.remove(uuid);
    }

    public void saveAll() {
        for (UUID uuid : playerDataCache.keySet()) {
            savePlayerData(uuid);
        }
    }

    // Leaderboard methods
    public Map<String, Integer> getTopKills(int limit) {
        Map<String, Integer> topKills = new HashMap<>();
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            if (conn == null) return topKills;

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT name, total_kills FROM player_data ORDER BY total_kills DESC LIMIT ?"
            );
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                topKills.put(rs.getString("name"), rs.getInt("total_kills"));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error fetching top kills");
            e.printStackTrace();
        }
        return topKills;
    }

    public Map<String, Integer> getTopGamesPlayed(int limit) {
        Map<String, Integer> topGamesPlayed = new HashMap<>();
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            if (conn == null) return topGamesPlayed;

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT name, games_played FROM player_data ORDER BY games_played DESC LIMIT ?"
            );
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                topGamesPlayed.put(rs.getString("name"), rs.getInt("games_played"));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error fetching top games played");
            e.printStackTrace();
        }
        return topGamesPlayed;
    }

    public Map<String, Integer> getTopWins(int limit) {
        Map<String, Integer> topWins = new HashMap<>();
        try {
            Connection conn = plugin.getDatabaseManager().getConnection();
            if (conn == null) return topWins;

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT name, games_won FROM player_data ORDER BY games_won DESC LIMIT ?"
            );
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                topWins.put(rs.getString("name"), rs.getInt("games_won"));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error fetching top wins");
            e.printStackTrace();
        }
        return topWins;
    }

    public Map<UUID, PlayerData> getAllPlayerData() {
        return new HashMap<>(playerDataCache);
    }

    public void resetAllStats() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection conn = plugin.getDatabaseManager().getConnection();
                if (conn == null) return;

                // Delete all player kits
                PreparedStatement ps = conn.prepareStatement("DELETE FROM player_kits");
                ps.executeUpdate();
                ps.close();

                // Delete all player data
                ps = conn.prepareStatement("DELETE FROM player_data");
                ps.executeUpdate();
                ps.close();

                // Clear cache on main thread
                Bukkit.getScheduler().runTask(plugin, () -> {
                    playerDataCache.clear();
                    plugin.getLogger().info("All player statistics have been reset!");
                });

            } catch (SQLException e) {
                plugin.getLogger().severe("Error resetting all player stats");
                e.printStackTrace();
            }
        });
    }

    public void resetPlayerStats(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection conn = plugin.getDatabaseManager().getConnection();
                if (conn == null) return;

                // Delete player kits
                PreparedStatement ps = conn.prepareStatement("DELETE FROM player_kits WHERE uuid = ?");
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
                ps.close();

                // Delete player data
                ps = conn.prepareStatement("DELETE FROM player_data WHERE uuid = ?");
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
                ps.close();

                // Clear from cache on main thread
                Bukkit.getScheduler().runTask(plugin, () -> {
                    playerDataCache.remove(uuid);
                    plugin.getLogger().info("Reset stats for player UUID: " + uuid);
                });

            } catch (SQLException e) {
                plugin.getLogger().severe("Error resetting player stats for " + uuid);
                e.printStackTrace();
            }
        });
    }
}
