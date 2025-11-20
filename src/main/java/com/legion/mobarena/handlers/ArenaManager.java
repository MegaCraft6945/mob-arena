package com.legion.mobarena.handlers;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.models.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager {

    private final LegionMobArena plugin;
    private final Map<String, Arena> arenas;
    private final File arenasFolder;

    public ArenaManager(LegionMobArena plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.arenasFolder = new File(plugin.getDataFolder(), "arenas");

        // Create arenas folder if it doesn't exist
        if (!arenasFolder.exists()) {
            arenasFolder.mkdirs();
        }

        loadArenas();
    }

    private void loadArenas() {
        File[] arenaFiles = arenasFolder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (arenaFiles == null || arenaFiles.length == 0) {
            plugin.getLogger().info("No arenas found.");
            return;
        }

        for (File file : arenaFiles) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                String name = config.getString("name");
                String worldName = config.getString("world");
                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    plugin.getLogger().warning("World " + worldName + " not found for arena " + name);
                    continue;
                }

                // Load positions
                Location min = new Location(world,
                        config.getInt("min.x"),
                        config.getInt("min.y"),
                        config.getInt("min.z"));

                Location max = new Location(world,
                        config.getInt("max.x"),
                        config.getInt("max.y"),
                        config.getInt("max.z"));

                Location lobby = new Location(world,
                        config.getDouble("lobby.x"),
                        config.getDouble("lobby.y"),
                        config.getDouble("lobby.z"),
                        (float) config.getDouble("lobby.yaw"),
                        (float) config.getDouble("lobby.pitch"));

                Arena arena = new Arena(name, world, min, max, lobby);
                arena.setEnabled(config.getBoolean("enabled", true));

                arenas.put(name.toLowerCase(), arena);

            } catch (Exception e) {
                plugin.getLogger().severe("Error loading arena from file: " + file.getName());
                e.printStackTrace();
            }
        }

        plugin.getLogger().info("Loaded " + arenas.size() + " arenas from files!");
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

        // Save to file
        saveArena(arena);
        plugin.getLogger().info("Arena " + name + " created and saved to file!");
    }

    public void deleteArena(String name) {
        arenas.remove(name.toLowerCase());

        // Delete file
        File arenaFile = new File(arenasFolder, name.toLowerCase() + ".yml");
        if (arenaFile.exists()) {
            arenaFile.delete();
            plugin.getLogger().info("Arena " + name + " deleted!");
        }
    }

    public void saveArena(Arena arena) {
        File arenaFile = new File(arenasFolder, arena.getName().toLowerCase() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        config.set("name", arena.getName());
        config.set("world", arena.getWorld().getName());

        // Save positions
        Location min = arena.getMin();
        config.set("min.x", min.getBlockX());
        config.set("min.y", min.getBlockY());
        config.set("min.z", min.getBlockZ());

        Location max = arena.getMax();
        config.set("max.x", max.getBlockX());
        config.set("max.y", max.getBlockY());
        config.set("max.z", max.getBlockZ());

        Location lobby = arena.getLobby();
        config.set("lobby.x", lobby.getX());
        config.set("lobby.y", lobby.getY());
        config.set("lobby.z", lobby.getZ());
        config.set("lobby.yaw", lobby.getYaw());
        config.set("lobby.pitch", lobby.getPitch());

        config.set("enabled", arena.isEnabled());

        try {
            config.save(arenaFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Error saving arena " + arena.getName());
            e.printStackTrace();
        }
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
