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
        player.sendMessage("§6§l=== Legion Mob Arena Admin Commands ===");
        player.sendMessage("§e/legionadmin §7- Open admin menu");
        player.sendMessage("§e/legionadmin create <name> §7- Start creating an arena");
        player.sendMessage("§e/legionadmin setpos1 §7- Set position 1");
        player.sendMessage("§e/legionadmin setpos2 §7- Set position 2");
        player.sendMessage("§e/legionadmin setlobby §7- Set lobby spawn");
        player.sendMessage("§e/legionadmin finish §7- Complete arena setup");
        player.sendMessage("§e/legionadmin cancel §7- Cancel arena setup");
        player.sendMessage("§e/legionadmin manage §7- Manage arenas");
        player.sendMessage("§e/legionadmin delete <name> §7- Delete an arena");
        player.sendMessage("§e/legionadmin reload §7- Reload configuration");
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
