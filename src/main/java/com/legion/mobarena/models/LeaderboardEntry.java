package com.legion.mobarena.models;

import java.util.UUID;

public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    private final String playerName;
    private final UUID playerId;
    private int highestRound;
    private int totalGamesPlayed;
    private int totalKills;
    private int totalGemsEarned;
    private long lastPlayed;

    public LeaderboardEntry(String playerName, UUID playerId) {
        this.playerName = playerName;
        this.playerId = playerId;
        this.highestRound = 0;
        this.totalGamesPlayed = 0;
        this.totalKills = 0;
        this.totalGemsEarned = 0;
        this.lastPlayed = System.currentTimeMillis();
    }

    public void updateStats(int round, int kills, int gems) {
        if (round > this.highestRound) {
            this.highestRound = round;
        }
        this.totalGamesPlayed++;
        this.totalKills += kills;
        this.totalGemsEarned += gems;
        this.lastPlayed = System.currentTimeMillis();
    }

    // Getters
    public String getPlayerName() {
        return playerName;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getHighestRound() {
        return highestRound;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public int getTotalGemsEarned() {
        return totalGemsEarned;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    @Override
    public int compareTo(LeaderboardEntry other) {
        // Sort by highest round first, then by total kills
        if (this.highestRound != other.highestRound) {
            return Integer.compare(other.highestRound, this.highestRound);
        }
        return Integer.compare(other.totalKills, this.totalKills);
    }
}
