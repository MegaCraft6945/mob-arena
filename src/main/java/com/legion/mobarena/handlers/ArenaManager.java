package com.legion.mobarena.handlers;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager {

    private final LegionMobArena plugin;
    private final Map<String, Arena> arenas;

    public ArenaManager(LegionMobArena plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        loadArenas();
    }

    private void loadArenas() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection conn = plugin.getDatabaseManager().getConnection();
                if (conn == null) return;

                PreparedStatement ps = conn.prepareStatement("SELECT * FROM arenas");
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String name = rs.getString("name");
                    String worldName = rs.getString("world");
                    World world = Bukkit.getWorld(worldName);

                    if (world == null) {
                        plugin.getLogger().warning("World " + worldName + " not found for arena " + name);
                        continue;
                    }

                    Location min = new Location(world,
                            rs.getInt("min_x"),
                            rs.getInt("min_y"),
                            rs.getInt("min_z"));
                    Location max = new Location(world,
                            rs.getInt("max_x"),
                            rs.getInt("max_y"),
                            rs.getInt("max_z"));
                    Location lobby = new Location(world,
                            rs.getDouble("lobby_x"),
                            rs.getDouble("lobby_y"),
                            rs.getDouble("lobby_z"),
                            rs.getFloat("lobby_yaw"),
                            rs.getFloat("lobby_pitch"));

                    Arena arena = new Arena(name, world, min, max, lobby);
                    arena.setEnabled(rs.getBoolean("enabled"));

                    arenas.put(name.toLowerCase(), arena);
                }

                rs.close();
                ps.close();

                plugin.getLogger().info("Loaded " + arenas.size() + " arenas!");

            } catch (SQLException e) {
                plugin.getLogger().severe("Error loading arenas!");
                e.printStackTrace();
            }
        });
    }

    public Arena getArena(String name) {
        return arenas.get(name.toLowerCase());
    }

    public Collection<Arena> getAllArenas() {
        return arenas.values();
    }

    public void createArena(String name, Location min, Location max, Location lobby) {
        World world = lobby.getWorld();
        if (world == null) return;

        Arena arena = new Arena(name, world, min, max, lobby);
        arenas.put(name.toLowerCase(), arena);

        // Save to database
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection conn = plugin.getDatabaseManager().getConnection();
                if (conn == null) return;

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO arenas (name, world, min_x, min_y, min_z, max_x, max_y, max_z, " +
                                "lobby_x, lobby_y, lobby_z, lobby_yaw, lobby_pitch, enabled) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );
                ps.setString(1, name);
                ps.setString(2, world.getName());
                ps.setInt(3, min.getBlockX());
                ps.setInt(4, min.getBlockY());
                ps.setInt(5, min.getBlockZ());
                ps.setInt(6, max.getBlockX());
                ps.setInt(7, max.getBlockY());
                ps.setInt(8, max.getBlockZ());
                ps.setDouble(9, lobby.getX());
                ps.setDouble(10, lobby.getY());
                ps.setDouble(11, lobby.getZ());
                ps.setFloat(12, lobby.getYaw());
                ps.setFloat(13, lobby.getPitch());
                ps.setBoolean(14, true);
                ps.executeUpdate();
                ps.close();

                plugin.getLogger().info("Arena " + name + " created!");

            } catch (SQLException e) {
                plugin.getLogger().severe("Error creating arena " + name);
                e.printStackTrace();
            }
        });
    }

    public void deleteArena(String name) {
        arenas.remove(name.toLowerCase());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection conn = plugin.getDatabaseManager().getConnection();
                if (conn == null) return;

                PreparedStatement ps = conn.prepareStatement("DELETE FROM arenas WHERE name = ?");
                ps.setString(1, name);
                ps.executeUpdate();
                ps.close();

                plugin.getLogger().info("Arena " + name + " deleted!");

            } catch (SQLException e) {
                plugin.getLogger().severe("Error deleting arena " + name);
                e.printStackTrace();
            }
        });
    }

    public Arena getArenaAtLocation(Location location) {
        for (Arena arena : arenas.values()) {
            if (arena.contains(location)) {
                return arena;
            }
        }
        return null;
    }
}
