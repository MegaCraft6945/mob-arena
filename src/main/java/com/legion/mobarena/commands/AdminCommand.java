package com.legion.mobarena.commands;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.gui.AdminMenuGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final LegionMobArena plugin;
    private final AdminMenuGUI adminMenu;
    private final Map<UUID, Long> resetConfirmations = new HashMap<>();
    private static final long CONFIRMATION_TIMEOUT = 30000; // 30 seconds

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

            case "reset":
                handleResetCommand(player, args);
                break;

            case "help":
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleResetCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /legionadmin reset <all|player> [confirm]");
            player.sendMessage("§cExamples:");
            player.sendMessage("§c  /legionadmin reset all §7- Reset all player stats");
            player.sendMessage("§c  /legionadmin reset PlayerName §7- Reset specific player stats");
            return;
        }

        String target = args[1].toLowerCase();

        if (target.equals("all")) {
            if (args.length >= 3 && args[2].equalsIgnoreCase("confirm")) {
                // Check if confirmation is still valid
                if (!resetConfirmations.containsKey(player.getUniqueId()) ||
                    System.currentTimeMillis() - resetConfirmations.get(player.getUniqueId()) > CONFIRMATION_TIMEOUT) {
                    player.sendMessage("§cConfirmation expired! Please run the command again.");
                    resetConfirmations.remove(player.getUniqueId());
                    return;
                }

                // Reset all stats
                int count = plugin.getPlayerDataManager().getAllPlayerData().size();
                plugin.getPlayerDataManager().resetAllStats();

                resetConfirmations.remove(player.getUniqueId());

                player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("§a§lSTATS RESET COMPLETE");
                player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("§7All player statistics have been reset!");
                player.sendMessage("§7Players affected: §e" + count);
                player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

                plugin.getLogger().warning(player.getName() + " reset ALL player statistics!");
            } else {
                // Ask for confirmation
                resetConfirmations.put(player.getUniqueId(), System.currentTimeMillis());

                player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("§c§l⚠ WARNING - RESET ALL STATS ⚠");
                player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("");
                player.sendMessage("§7You are about to reset §cALL§7 player statistics!");
                player.sendMessage("§7This will delete:");
                player.sendMessage("§c  • All player kills, deaths, and rounds");
                player.sendMessage("§c  • All gems and unlocked kits");
                player.sendMessage("§c  • All game history and leaderboards");
                player.sendMessage("");
                player.sendMessage("§7Players affected: §e" + plugin.getPlayerDataManager().getAllPlayerData().size());
                player.sendMessage("");
                player.sendMessage("§c§lTHIS CANNOT BE UNDONE!");
                player.sendMessage("");
                player.sendMessage("§7To confirm, type:");
                player.sendMessage("§e/legionadmin reset all confirm");
                player.sendMessage("");
                player.sendMessage("§7This confirmation expires in 30 seconds.");
                player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            }
        } else {
            // Reset specific player
            String playerName = args[1];
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerName);

            if (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline()) {
                player.sendMessage("§cPlayer §e" + playerName + " §cnot found or has never played!");
                return;
            }

            boolean hasData = plugin.getPlayerDataManager().getPlayerData(targetPlayer.getUniqueId()) != null;

            if (!hasData) {
                player.sendMessage("§cPlayer §e" + targetPlayer.getName() + " §chas no stats to reset!");
                return;
            }

            plugin.getPlayerDataManager().resetPlayerStats(targetPlayer.getUniqueId());

            player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage("§a§lPLAYER STATS RESET");
            player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage("§7Reset statistics for: §e" + targetPlayer.getName());
            player.sendMessage("§7All stats, gems, and kits have been reset!");
            player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            plugin.getLogger().warning(player.getName() + " reset stats for player: " + targetPlayer.getName());

            // Notify the player if they're online
            if (targetPlayer.isOnline()) {
                Player onlineTarget = (Player) targetPlayer;
                onlineTarget.sendMessage("§c§lYour Legion Mob Arena statistics have been reset by an administrator!");
            }
        }
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
        player.sendMessage("§6Stats Management:");
        player.sendMessage("  §e/legionadmin reset all §8» §7Reset all player stats");
        player.sendMessage("  §e/legionadmin reset <player> §8» §7Reset specific player");
        player.sendMessage("");
        player.sendMessage("§7§oTip: Use the GUI for easier arena management!");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "setpos1", "setpos2", "setlobby", "finish", "cancel", "manage", "delete", "reload", "reset", "help");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            List<String> arenaNames = new ArrayList<>();
            plugin.getArenaManager().getAllArenas().forEach(arena -> arenaNames.add(arena.getName()));
            return arenaNames;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            List<String> options = new ArrayList<>();
            options.add("all");
            // Add online player names
            for (Player p : Bukkit.getOnlinePlayers()) {
                options.add(p.getName());
            }
            return options;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("reset") && args[1].equalsIgnoreCase("all")) {
            return Arrays.asList("confirm");
        }

        return null;
    }

    public AdminMenuGUI getAdminMenu() {
        return adminMenu;
    }
}
