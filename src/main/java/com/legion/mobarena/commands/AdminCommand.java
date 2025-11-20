package com.legion.mobarena.commands;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.gui.AdminMenuGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final LegionMobArena plugin;
    private final AdminMenuGUI adminMenu;

    public AdminCommand(LegionMobArena plugin) {
        this.plugin = plugin;
        this.adminMenu = new AdminMenuGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("legion.admin")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            adminMenu.openAdminMenu(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /legionadmin create <arena-name>");
                    return true;
                }
                String arenaName = args[1];
                adminMenu.startArenaSetup(player, arenaName);
                break;

            case "setpos1":
            case "pos1":
                adminMenu.setPosition1(player);
                break;

            case "setpos2":
            case "pos2":
                adminMenu.setPosition2(player);
                break;

            case "setlobby":
            case "lobby":
                adminMenu.setLobby(player);
                break;

            case "finish":
            case "finishsetup":
                adminMenu.finishSetup(player);
                break;

            case "cancel":
                adminMenu.cancelSetup(player);
                break;

            case "manage":
            case "list":
                adminMenu.openArenaManagement(player);
                break;

            case "delete":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /legionadmin delete <arena-name>");
                    return true;
                }
                String arenaToDelete = args[1];
                plugin.getArenaManager().deleteArena(arenaToDelete);
                player.sendMessage("§aArena §e" + arenaToDelete + " §adeleted!");
                break;

            case "reload":
                plugin.reloadConfig();
                player.sendMessage("§aConfiguration reloaded!");
                break;

            case "help":
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§c§l    Legion Mob Arena Admin Commands");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("");
        player.sendMessage("§6Arena Management:");
        player.sendMessage("  §e/legionadmin §8» §7Open admin GUI menu");
        player.sendMessage("  §e/legionadmin manage §8» §7List and manage all arenas");
        player.sendMessage("  §e/legionadmin delete <name> §8» §7Delete an arena");
        player.sendMessage("");
        player.sendMessage("§6Arena Creation:");
        player.sendMessage("  §e/legionadmin create <name> §8» §7Start arena setup");
        player.sendMessage("  §e/legionadmin setpos1 §8» §7Set corner 1 of arena");
        player.sendMessage("  §e/legionadmin setpos2 §8» §7Set corner 2 of arena");
        player.sendMessage("  §e/legionadmin setlobby §8» §7Set lobby spawn point");
        player.sendMessage("  §e/legionadmin finish §8» §7Complete arena setup");
        player.sendMessage("  §e/legionadmin cancel §8» §7Cancel setup process");
        player.sendMessage("");
        player.sendMessage("§6Configuration:");
        player.sendMessage("  §e/legionadmin reload §8» §7Reload config.yml");
        player.sendMessage("");
        player.sendMessage("§7§oTip: Use the GUI for easier arena management!");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "setpos1", "setpos2", "setlobby", "finish", "cancel", "manage", "delete", "reload", "help");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            List<String> arenaNames = new ArrayList<>();
            plugin.getArenaManager().getAllArenas().forEach(arena -> arenaNames.add(arena.getName()));
            return arenaNames;
        }

        return null;
    }

    public AdminMenuGUI getAdminMenu() {
        return adminMenu;
    }
}
