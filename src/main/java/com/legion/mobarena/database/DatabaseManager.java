package com.legion.mobarena.database;

import com.legion.mobarena.LegionMobArena;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {

    private final LegionMobArena plugin;
    private Connection connection;
    private String host, database, username, password;
    private int port;

    public DatabaseManager(LegionMobArena plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        this.host = config.getString("database.host", "localhost");
        this.port = config.getInt("database.port", 3306);
        this.database = config.getString("database.database", "legion_mobarena");
        this.username = config.getString("database.username", "root");
        this.password = config.getString("database.password", "password");
    }

    public void connect() {
        if (!plugin.getConfig().getBoolean("database.enabled", true)) {
            plugin.getLogger().warning("Database is disabled in config!");
            return;
        }

        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return;
                }

                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true",
                        username, password
                );

                plugin.getLogger().info("Successfully connected to MySQL database!");
                createTables();
            }
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to MySQL database!");
            e.printStackTrace();
        }
    }

    private void createTables() {
        try {
            // Player data table
            PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS player_data (" +
                            "uuid VARCHAR(36) PRIMARY KEY," +
                            "name VARCHAR(16) NOT NULL," +
                            "gems INT DEFAULT 0," +
                            "total_kills INT DEFAULT 0," +
                            "total_deaths INT DEFAULT 0," +
                            "highest_round INT DEFAULT 0," +
                            "games_played INT DEFAULT 0," +
                            "games_won INT DEFAULT 0," +
                            "last_seen BIGINT" +
                            ")"
            );
            ps.executeUpdate();
            ps.close();

            // Player kits table
            ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS player_kits (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "uuid VARCHAR(36) NOT NULL," +
                            "kit_name VARCHAR(50) NOT NULL," +
                            "unlocked_at BIGINT," +
                            "UNIQUE KEY unique_player_kit (uuid, kit_name)" +
                            ")"
            );
            ps.executeUpdate();
            ps.close();

            // Arenas table
            ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS arenas (" +
                            "name VARCHAR(50) PRIMARY KEY," +
                            "world VARCHAR(50) NOT NULL," +
                            "min_x INT NOT NULL," +
                            "min_y INT NOT NULL," +
                            "min_z INT NOT NULL," +
                            "max_x INT NOT NULL," +
                            "max_y INT NOT NULL," +
                            "max_z INT NOT NULL," +
                            "lobby_x DOUBLE NOT NULL," +
                            "lobby_y DOUBLE NOT NULL," +
                            "lobby_z DOUBLE NOT NULL," +
                            "lobby_yaw FLOAT NOT NULL," +
                            "lobby_pitch FLOAT NOT NULL," +
                            "enabled BOOLEAN DEFAULT TRUE" +
                            ")"
            );
            ps.executeUpdate();
            ps.close();

            plugin.getLogger().info("Database tables created successfully!");
        } catch (SQLException e) {
            plugin.getLogger().severe("Error creating database tables!");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                plugin.getLogger().info("Disconnected from MySQL database!");
            } catch (SQLException e) {
                plugin.getLogger().severe("Error disconnecting from database!");
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
