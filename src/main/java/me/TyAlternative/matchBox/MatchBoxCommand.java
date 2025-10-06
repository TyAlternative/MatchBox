package me.TyAlternative.matchBox;

import me.TyAlternative.matchBox.composition.CompositionManager;
import me.TyAlternative.matchBox.gameplay.GameEndResult;
import me.TyAlternative.matchBox.gameplay.GameplayManager;
import me.TyAlternative.matchBox.roles.enums.TeamType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MatchBoxCommand implements CommandExecutor, TabExecutor {

    GameplayManager gameplayManager = GameplayManager.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length >= 1)
            switch (args[0]) {
                case "clearSign" -> gameplayManager.breakEveryPlacedSign();
                case "start" -> {
                    List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    gameplayManager.startGame(players, CompositionManager.getRoleDistribution());
                }
                case "stop" -> {
                    gameplayManager.endGame(new GameEndResult(false, TeamType.NONE, "Partie annulée prématurément"));
                }
                case "role" -> {
                    gameplayManager.getPlayerRoleData(player).getRole().printRoleDescription(player);
                }
            }

        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> tabCompletion = new ArrayList<>();
        if (args.length == 1) {
            tabCompletion = List.of("clearSign","start","stop","role");
        }


        List<String> tabCompletionFinal = new ArrayList<>();
        for (String tabSuggestion : tabCompletion) {
            if (tabSuggestion.toUpperCase().startsWith(args[args.length-1].toUpperCase())) {
                tabCompletionFinal.add(tabSuggestion);
            }
        }

        return tabCompletionFinal;
    }
}
