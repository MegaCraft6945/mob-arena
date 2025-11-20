package com.legion.mobarena.models;

import org.bukkit.Location;
import org.bukkit.World;

public class Arena {
    private String name;
    private World world;
    private Location min;
    private Location max;
    private Location lobby;
    private boolean enabled;

    public Arena(String name, World world, Location min, Location max, Location lobby) {
        this.name = name;
        this.world = world;
        this.min = min;
        this.max = max;
        this.lobby = lobby;
        this.enabled = true;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public Location getMin() {
        return min;
    }

    public Location getMax() {
        return max;
    }

    public Location getLobby() {
        return lobby;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean contains(Location location) {
        if (!location.getWorld().equals(world)) {
            return false;
        }

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= min.getX() && x <= max.getX() &&
               y >= min.getY() && y <= max.getY() &&
               z >= min.getZ() && z <= max.getZ();
    }

    public Location getCenter() {
        double x = (min.getX() + max.getX()) / 2;
        double y = (min.getY() + max.getY()) / 2;
        double z = (min.getZ() + max.getZ()) / 2;
        return new Location(world, x, y, z);
    }
}
