package com.legion.mobarena.models;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class Game {
    private final Arena arena;
    private final Set<UUID> players;
    private final Map<UUID, Integer> playerGold;
    private final Map<UUID, Integer> playerGems;
    private final Map<UUID, Integer> playerKills;
    private final Map<UUID, Kit> playerKits;
    private final Set<Entity> arenaMobs;
    private final Set<Location> placedCakes;

    private GameState state;
    private int currentRound;
    private int mobsRemaining;
    private int countdown;

    public Game(Arena arena) {
        this.arena = arena;
        this.players = new HashSet<>();
        this.playerGold = new HashMap<>();
        this.playerGems = new HashMap<>();
        this.playerKills = new HashMap<>();
        this.playerKits = new HashMap<>();
        this.arenaMobs = new HashSet<>();
        this.placedCakes = new HashSet<>();
        this.state = GameState.WAITING;
        this.currentRound = 0;
        this.mobsRemaining = 0;
        this.countdown = 10;
    }

    public Arena getArena() {
        return arena;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public void addPlayer(UUID player) {
        players.add(player);
        playerGold.put(player, 0);
        playerGems.put(player, 0);
        playerKills.put(player, 0);
    }

    public void removePlayer(UUID player) {
        players.remove(player);
        playerGold.remove(player);
        playerGems.remove(player);
        playerKills.remove(player);
        playerKits.remove(player);
    }

    public int getPlayerGold(UUID player) {
        return playerGold.getOrDefault(player, 0);
    }

    public void addPlayerGold(UUID player, int amount) {
        playerGold.put(player, getPlayerGold(player) + amount);
    }

    public void removePlayerGold(UUID player, int amount) {
        playerGold.put(player, getPlayerGold(player) - amount);
    }

    public int getPlayerGems(UUID player) {
        return playerGems.getOrDefault(player, 0);
    }

    public void addPlayerGems(UUID player, int amount) {
        playerGems.put(player, getPlayerGems(player) + amount);
    }

    public int getPlayerKills(UUID player) {
        return playerKills.getOrDefault(player, 0);
    }

    public void incrementPlayerKills(UUID player) {
        playerKills.put(player, getPlayerKills(player) + 1);
    }

    public Kit getPlayerKit(UUID player) {
        return playerKits.get(player);
    }

    public void setPlayerKit(UUID player, Kit kit) {
        playerKits.put(player, kit);
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int round) {
        this.currentRound = round;
    }

    public int getMobsRemaining() {
        return mobsRemaining;
    }

    public void setMobsRemaining(int mobsRemaining) {
        this.mobsRemaining = mobsRemaining;
    }

    public void decrementMobsRemaining() {
        this.mobsRemaining--;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public void decrementCountdown() {
        this.countdown--;
    }

    public Set<Entity> getArenaMobs() {
        return arenaMobs;
    }

    public void addArenaMob(Entity mob) {
        arenaMobs.add(mob);
    }

    public void removeArenaMob(Entity mob) {
        arenaMobs.remove(mob);
    }

    public void clearArenaMobs() {
        for (Entity mob : new HashSet<>(arenaMobs)) {
            mob.remove();
        }
        arenaMobs.clear();
    }

    public Set<Location> getPlacedCakes() {
        return placedCakes;
    }

    public void addPlacedCake(Location location) {
        placedCakes.add(location);
    }

    public void clearPlacedCakes() {
        for (Location cakeLocation : new HashSet<>(placedCakes)) {
            if (cakeLocation.getBlock().getType() == org.bukkit.Material.CAKE) {
                cakeLocation.getBlock().setType(org.bukkit.Material.AIR);
            }
        }
        placedCakes.clear();
    }

    public int calculateMobsForRound(int round) {
        // Round 1: 10 mobs
        // Each round increases by 2 mobs
        int baseMobs = 10;
        int increasePerRound = 2;
        return baseMobs + ((round - 1) * increasePerRound);
    }

    public int calculateGemsForRound(int round) {
        // Round 1: 5 gems
        // Gems increase by 1 every 5 rounds
        int baseGems = 5;
        int gemIncreaseEveryRounds = 5;
        return baseGems + ((round - 1) / gemIncreaseEveryRounds);
    }

    public boolean areAllPlayersReady() {
        for (UUID playerId : players) {
            if (!playerKits.containsKey(playerId)) {
                return false;
            }
        }
        return !players.isEmpty();
    }
}
