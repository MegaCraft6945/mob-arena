package com.legion.mobarena.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private String name;
    private int gems;
    private int totalKills;
    private int totalDeaths;
    private int highestRound;
    private int gamesPlayed;
    private int gamesWon;
    private long lastSeen;
    private final Set<String> unlockedKits;

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.gems = 0;
        this.totalKills = 0;
        this.totalDeaths = 0;
        this.highestRound = 0;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.lastSeen = System.currentTimeMillis();
        this.unlockedKits = new HashSet<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGems() {
        return gems;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public void addGems(int amount) {
        this.gems += amount;
    }

    public void removeGems(int amount) {
        this.gems -= amount;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public void setTotalKills(int totalKills) {
        this.totalKills = totalKills;
    }

    public void addKill() {
        this.totalKills++;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public void addDeath() {
        this.totalDeaths++;
    }

    public int getHighestRound() {
        return highestRound;
    }

    public void setHighestRound(int highestRound) {
        if (highestRound > this.highestRound) {
            this.highestRound = highestRound;
        }
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public void addGamePlayed() {
        this.gamesPlayed++;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public void addGameWon() {
        this.gamesWon++;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Set<String> getUnlockedKits() {
        return unlockedKits;
    }

    public void unlockKit(String kitName) {
        unlockedKits.add(kitName);
    }

    public boolean hasKit(String kitName) {
        return unlockedKits.contains(kitName);
    }
}
