package me.TyAlternative.matchBox.roles;

import me.TyAlternative.matchBox.MatchBox;
import me.TyAlternative.matchBox.gameplay.GameplayManager;
import me.TyAlternative.matchBox.gameplay.enums.DeathCause;
import me.TyAlternative.matchBox.gameplay.enums.ProtectCause;
import me.TyAlternative.matchBox.players.PlayerRoleData;
import me.TyAlternative.matchBox.roles.enums.Role;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class RoleManager {
    private final boolean debugComment = true;

    private final GameplayManager gameplayManager;
    private final JavaPlugin plugin = MatchBox.getInstance();
    public final Map<Role, GameRole> registeredRoles;
    public final Map<UUID, PlayerRoleData> playerRoles;

    public final Map<UUID, DeathCause> embrasedPlayers; // Joueurs embrasés cette manche
    public final Map<UUID, ProtectCause> protectedPlayers; // Joueurs protégés cette manche



    public RoleManager(GameplayManager gameplayManager) {
        this.gameplayManager = gameplayManager;
        this.registeredRoles = new HashMap<>();
        this.playerRoles = new HashMap<>();
        this.embrasedPlayers = new HashMap<>();
        this.protectedPlayers = new HashMap<>();
    }

    // Enregistrement des rôles
    public void registerRole(GameRole role) {
        role.setRoleManager(this);
        registeredRoles.put(role.getRoleId(), role);

        // On affiche un message dans la console que si le mode débug est activé.
        if (debugComment) plugin.getLogger().info("Rôle enregistré: " + role.getDisplayName());
    }


    // Attribution d'un rôle
    public void assignRole(Player player, Role roleId) {
        // On récupère la classe instanciée du role
        GameRole role = registeredRoles.get(roleId);
        // Et return s'il n'éxiste pas.
        if (role == null) return;

        // S'il existe, on crée un nouveau PlayerRoleData, qu'on ajoute à playersRoles
        PlayerRoleData data = new PlayerRoleData(player.getUniqueId(), role);
        playerRoles.put(player.getUniqueId(), data);

        // On appelle le hook correspondant à l'assignation du role.
        role.onRoleAssigned(player);
        if (debugComment) plugin.getLogger().info("Le rôle " + data.getRole().getDisplayName() + " a été assigné à " + data.getPlayer().getName() + ".");

        // TEXT ANNONCENT LE ROLE DONNE!

    }

    // Distribution aléatoire des rôles
    public boolean distributeRoles(List<Player> players, Map<Role, Integer> roleDistribution) {
        List<Role> rolesToAssign = new ArrayList<>();

        for (Map.Entry<Role, Integer> entry : roleDistribution.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                registerRole(entry.getKey().instantiateRole());
                rolesToAssign.add(entry.getKey());
            }
        }

        if (rolesToAssign.size() != players.size()) {
            plugin.getLogger().warning("Nombre de rôles != nombre de joueurs!");
            return false;
        }

        Collections.shuffle(rolesToAssign);
        Collections.shuffle(players);

        for (int i = 0; i < players.size(); i++) {
            assignRole(players.get(i), rolesToAssign.get(i));
        }
        return true;
    }


    // Getters
    public PlayerRoleData getPlayerRoleData(Player player) {
        return playerRoles.get(player.getUniqueId());
    }
    public GameRole getRole(Player player) {
        PlayerRoleData data = getPlayerRoleData(player);
        // si le PlayerRoleData du joueur n'existe pas, il n'a pas de role donc return null, sinon, on renvoie son role.
        return data != null ? data.getRole() : null;
    }


    // Gestion de l'embrasement
    public boolean embrasePlayer(Player target, DeathCause deathCause) {
        UUID targetId = target.getUniqueId();

        // Vérifie si le joueur a déja été Embrasé
        if (isEmbrased(target)) return false;

        // Sinon, ajoute le joueur au set de joueur Embrasé
        embrasedPlayers.put(targetId, deathCause);
        if (debugComment) plugin.getLogger().info("Le joueur " + target.getName() + " a été Embrasé avec succès.");
        return true;

    }
    public boolean isEmbrased(Player player) {
        return embrasedPlayers.containsKey(player.getUniqueId());
    }

    // Gestion de la protection
    public boolean protectPlayer(Player target, ProtectCause protectCause) {
        UUID targetId = target.getUniqueId();

        // Vérifie si le joueur a déja été Protégé
        if (isProtected(target)) return false;

        // Sinon, ajoute le joueur au set de joueur Protégé
        protectedPlayers.put(targetId, protectCause);
        if (debugComment) plugin.getLogger().info("Le joueur " + target.getName() + " a été Protégé avec succès.");
        return true;

    }
    public boolean isProtected(Player player) {
        return protectedPlayers.containsKey(player.getUniqueId());
    }








    public void resetRound() {
//        playerRoles.clear();
        embrasedPlayers.clear();
        protectedPlayers.clear();
    }
    public void reset() {
        registeredRoles.clear();
        playerRoles.clear();
        embrasedPlayers.clear();
        protectedPlayers.clear();
    }
}