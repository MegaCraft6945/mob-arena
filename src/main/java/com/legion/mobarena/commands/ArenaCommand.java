package com.legion.mobarena.commands;

import com.legion.mobarena.LegionMobArena;
import com.legion.mobarena.gui.ArenaSelectionGUI;
import com.legion.mobarena.models.Arena;
import com.legion.mobarena.models.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArenaCommand implements CommandExecutor, TabCompleter {

    private final LegionMobArena plugin;
    private final ArenaSelectionGUI arenaSelection;

    public ArenaCommand(LegionMobArena plugin) {
        this.plugin = plugin;
        this.arenaSelection = new ArenaSelectionGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("legion.play")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            arenaSelection.openArenaSelection(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "join":
                if (args.length < 2) {
                    arenaSelection.openArenaSelection(player);
                } else {
                    String arenaName = args[1];
                    Arena arena = plugin.getArenaManager().getArena(arenaName);
                    if (arena == null) {
                        player.sendMessage("§cArena not found!");
                        return true;
                    }
                    plugin.getGameManager().joinGame(player, arena);
                }
                break;

            case "leave":
            case "quit":
                if (!plugin.getGameManager().isPlayerInGame(player.getUniqueId())) {
                    player.sendMessage("§cYou are not in a game!");
                    return true;
                }
                plugin.getGameManager().onPlayerDeath(player);
                player.sendMessage("§eYou left the game!");
                break;

            case "stats":
                showStats(player);
                break;

            case "help":
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void showStats(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) {
            player.sendMessage("§cNo stats found! Play a game to start tracking your stats.");
            return;
        }

        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§6§l       Your Legion Mob Arena Stats");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("");
        player.sendMessage("§6Currency:");
        player.sendMessage("  §eGems: §a" + data.getGems() + " §7(Use to unlock kits)");
        player.sendMessage("");
        player.sendMessage("§6Progress:");
        player.sendMessage("  §eHighest Round: §a" + data.getHighestRound() + " §7/ §a50");
        player.sendMessage("  §eUnlocked Kits: §a" + data.getUnlockedKits().size() + " §7/ §a9");
        player.sendMessage("");
        player.sendMessage("§6Combat Stats:");
        player.sendMessage("  §eTotal Kills: §a" + data.getTotalKills());
        player.sendMessage("  §eTotal Deaths: §a" + data.getTotalDeaths());
        double kdr = data.getTotalDeaths() > 0 ? (double) data.getTotalKills() / data.getTotalDeaths() : data.getTotalKills();
        player.sendMessage("  §eK/D Ratio: §a" + String.format("%.2f", kdr));
        player.sendMessage("");
        player.sendMessage("§6Game History:");
        player.sendMessage("  §eGames Played: §a" + data.getGamesPlayed());
        player.sendMessage("  §eGames Won: §a" + data.getGamesWon());
        double winRate = data.getGamesPlayed() > 0 ? (double) data.getGamesWon() / data.getGamesPlayed() * 100 : 0;
        player.sendMessage("  §eWin Rate: §a" + String.format("%.1f", winRate) + "%");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private void sendHelp(Player player) {
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§6§l       Legion Mob Arena Commands");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§e/legion §8» §7Open arena selection GUI");
        player.sendMessage("§e/legion join [arena] §8» §7Join an arena directly");
        player.sendMessage("§e/legion leave §8» §7Leave your current game");
        player.sendMessage("§e/legion stats §8» §7View your statistics");
        player.sendMessage("§e/legion help §8» §7Show this help message");
        player.sendMessage("");
        player.sendMessage("§7§oTip: Survive 50 rounds to win!");
        player.sendMessage("§7§oCollect gold nuggets to upgrade your gear!");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("join", "leave", "stats", "help");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            List<String> arenaNames = new ArrayList<>();
            plugin.getArenaManager().getAllArenas().forEach(arena -> arenaNames.add(arena.getName()));
            return arenaNames;
        }

        return null;
    }

    public ArenaSelectionGUI getArenaSelection() {
        return arenaSelection;
    }
}
