package com.legion.mobarena.models;

public enum GameState {
    WAITING,      // Waiting for players to select kits
    COUNTDOWN,    // Countdown before game starts
    ACTIVE,       // Game is in progress
    UPGRADE_SHOP, // Players are in the upgrade shop
    ENDING        // Game is ending
}
