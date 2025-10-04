package me.TyAlternative.matchBox.composition;

import me.TyAlternative.matchBox.gameplay.GameplayManager;
import me.TyAlternative.matchBox.roles.RoleManager;
import me.TyAlternative.matchBox.roles.enums.Role;
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

public class CompoCommandListener implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        Player player = (Player) commandSender;

        switch (args.length) {
            case 0 -> {
                CompositionGUI.openGUI(player);
            }
            case 1 -> {
                if (args[0].equalsIgnoreCase("display")) {
                    player.sendMessage("§7-------- §eComposition §7--------");
                    CompositionManager.displayCompo(player);
                } else if (args[0].equalsIgnoreCase("start")) {
                    List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    GameplayManager.getInstance().startGame(players, CompositionManager.getRoleDistribution());
                }

            }
            case 2 -> {
                String keyword = null;
                Role role = null;
                int value = 0;
                try {
                    keyword = args[0];
                    role = Role.fromString(args[1]);
                    if (keyword.equalsIgnoreCase("add") &&
                            role != null) {
                        CompositionManager.addRole(role);
                        player.sendMessage("§eVous avez ajouté §c1 §d" + role);
                    }
                    else if (keyword.equalsIgnoreCase("remove") &&
                            role != null ) {
                        CompositionManager.removeRole(role);
                        player.sendMessage("§eVous avez retiré §c1 §d" + role);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            case 3 -> {
                String keyword = null;
                Role role = null;
                int value = 0;
                try {
                    keyword = args[0];
                    role = Role.fromString(args[1]);
                    value = Integer.parseInt(args[2]);
                    if (keyword.equalsIgnoreCase("add") &&
                            role != null &&
                            value > 0 ) {
                        CompositionManager.addRole(role, value);
                        player.sendMessage("§eVous avez ajouté §c" + value + " §d" + role);
                    }
                    else if (keyword.equalsIgnoreCase("remove") &&
                            role != null &&
                            value > 0 ) {
                        CompositionManager.removeRole(role, value);
                        player.sendMessage("§eVous avez retiré §c" + value + " §d" + role);
                    }
                    if (keyword.equalsIgnoreCase("set") &&
                            role != null &&
                            value > 0 ) {
                        CompositionManager.setRole(role, value);
                        player.sendMessage("§eVous avez défini le nombre de §d" + role + " §eà §c" + value);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }
}
