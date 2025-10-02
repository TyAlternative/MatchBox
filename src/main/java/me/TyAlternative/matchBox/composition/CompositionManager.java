package me.TyAlternative.matchBox.composition;

import me.TyAlternative.matchBox.MatchBox;
import me.TyAlternative.matchBox.roles.enums.Role;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CompositionManager {
    private static Map<Role, Integer> roleDistribution = new HashMap<>();

    public static Map<Role, Integer> getRoleDistribution() {
        return roleDistribution;
    }
    public static void addRole(Role role) {
        roleDistribution.compute(role, (key, oldValue) -> (oldValue == null) ? 1 : oldValue + 1);
    }
    public static void addRole(Role role, int amount) {
        roleDistribution.compute(role, (key, oldValue) -> (oldValue == null || oldValue + amount <= 0) ? amount : oldValue + amount);
    }

    public static void removeRole(Role role) {
        roleDistribution.compute(role, (key, oldValue) -> (oldValue == null || oldValue - 1 <= 0) ? null : oldValue - 1);
    }
    public static void removeRole(Role role, int amount) {
        roleDistribution.compute(role, (key, oldValue) -> (oldValue == null || oldValue - amount <= 0) ? null : oldValue - amount);
    }

    public static void setRole(Role role, int amount) {
        roleDistribution.compute(role, (key, oldValue) -> (amount <= 0) ? null : amount);
    }

    public static void displayCompo(Player player) {
        if (player == null) {
            for (Map.Entry<Role, Integer> roleIntegerEntry : roleDistribution.entrySet()) {
                MatchBox.getInstance().getLogger().info(" - " + roleIntegerEntry.getKey().toString() + " x" + roleIntegerEntry.getValue());
            }

        } else {
            for (Map.Entry<Role, Integer> roleIntegerEntry : roleDistribution.entrySet()) {
                player.sendMessage("§7 - §c" + roleIntegerEntry.getValue() + "x §d" + roleIntegerEntry.getKey().toString());
            }
        }
    }
}
