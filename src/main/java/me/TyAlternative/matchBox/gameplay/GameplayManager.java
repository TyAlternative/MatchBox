package me.TyAlternative.matchBox.gameplay;

import me.TyAlternative.matchBox.gameplay.enums.DeathCause;
import me.TyAlternative.matchBox.players.PlayerRoleData;
import me.TyAlternative.matchBox.roles.RoleManager;
import me.TyAlternative.matchBox.roles.enums.AbilityType;
import me.TyAlternative.matchBox.states.PlayerStates;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;

public class GameplayManager {
    public List<UUID> players = new ArrayList<>();
    public List<UUID> alivePlayers = new ArrayList<>();
    private RoleManager roleManager = new RoleManager(this);
    private HidePlayerManager hidePlayerManager = new HidePlayerManager(this);


    public long gameplayPhaseEndTime;




    public void startGameplayPhase(int durationSeconds) {
        roleManager.embrasedPlayers.clear();
        roleManager.protectedPlayers.clear();
        gameplayPhaseEndTime = System.currentTimeMillis() + (durationSeconds * 1000L);

        for (PlayerRoleData data : roleManager.playerRoles.values()) {
            if (data.isAlive()) {
                Player p = Bukkit.getPlayer(data.getPlayerId());
                if (p != null) {
                    data.getRole().onGameplayPhaseStart(p);
                }
            }
        }
    }



    public void endGameplayPhase() {
        // Appeler les hooks de fin de phase
        for (PlayerRoleData data : roleManager.playerRoles.values()) {
            if (data.isAlive()) {
                Player p = Bukkit.getPlayer(data.getPlayerId());
                if (p != null) {
                    data.getRole().onGameplayPhaseEnd(p);
                }
            }
        }

        checkDeath();
    }



    public void checkDeath() {
        // Éliminer les joueurs embrasés (sauf les calcinés qui retardent)
        Map<UUID,DeathCause> toEliminate = new HashMap<>();

        // On parcourt chaque joueur en vie
        for (UUID embrasedId : alivePlayers) {
            // On récupère son role
            PlayerRoleData data = roleManager.playerRoles.get(embrasedId);

            // On vérifie, au cas où, qu'il n'est pas mort
            if (data == null || !data.isAlive()) continue;


            // On vérifie s'il possède la capacité du calciné :
            if (data.getRole().hasAbility(AbilityType.CALCINE)) {

                // On vérifie si l'embrasement a été retardé, (Calciné de la manche précédente) :
                Boolean pendingEmbrasement = (Boolean) data.getCustomData("pending_embrasement");
                if (pendingEmbrasement != null && pendingEmbrasement) {

                    // Si c'est le cas, on récupère sa cause de mort et l'envoie dans la Map toEliminate
                    DeathCause deathCause = (DeathCause) data.getCustomData("death_cause");
                    if (deathCause == null) deathCause = DeathCause.ETINCELLE;
                    toEliminate.put(embrasedId, deathCause);

                    // Et on reset le pending et le calcine delayed
                    data.setCustomData("pending_embrasement", false);
                    data.setCustomData("calcine_delayed", false);
                    continue;
                }

            }

            // On vérifie qu'il n'a pas été protégé
            if (roleManager.protectedPlayers.containsKey(embrasedId)) {
                // Modification des pourcentages possible (par exemple réduire le taux de protection du souffle de 20%)
                continue;
            }


            // On vérifie qu'il est bien embrasé
            if (!roleManager.embrasedPlayers.containsKey(embrasedId)) continue;

            DeathCause deathCause = roleManager.embrasedPlayers.get(embrasedId);

            // Vérifier si le joueur est Calciné
            if (data.getRole().hasAbility(AbilityType.CALCINE)) {
                Boolean hasDelayed = (Boolean) data.getCustomData("calcine_delayed");
                if (hasDelayed == null || !hasDelayed) {
                    // Premier embrasement : on retarde
                    data.setCustomData("death_cause", deathCause);
                    data.setCustomData("calcine_delayed", true);
                    data.setCustomData("pending_embrasement", true);
                }
            } else {
                toEliminate.put(embrasedId, deathCause);
            }

        }

        // Éliminer les joueurs
        for (UUID playerId : toEliminate.keySet()) {
            DeathCause deathCause = toEliminate.get(playerId);
            eliminatePlayer(playerId, deathCause);
        }

    }


    // Élimination d'un joueur
    public void eliminatePlayer(UUID playerId, DeathCause deathCause) {
        PlayerRoleData data = roleManager.playerRoles.get(playerId);
        if (data == null || !data.isAlive()) return;

        data.setPlayerStates(PlayerStates.SPECTATOR);
        Player eliminated = Bukkit.getPlayer(playerId);

        if (eliminated != null) {
            data.getRole().onPlayerElimination(eliminated, deathCause);
            eliminated.sendMessage("§cVous avez été éliminé!");
            eliminated.setGameMode(GameMode.SPECTATOR);
            alivePlayers.remove(playerId);

            // Notification aux autres joueurs (sans révéler le rôle)
            String eliminationType = switch (deathCause) {
                case ETINCELLE,TORCHE -> "§cEmbrasé";
                case VOTE -> "§baux Votes";
            };
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;
                player.sendMessage("§e" + eliminated.getName() + "§7 a été éliminé " + eliminationType + "!");
            }
        }

        // Notifier tous les rôles de l'élimination
        for (PlayerRoleData otherData : roleManager.playerRoles.values()) {
            if (otherData.isAlive()) {
                Player other = Bukkit.getPlayer(otherData.getPlayerId());
                if (other != null) {
                    otherData.getRole().onOtherPlayerElimination(eliminated, deathCause,other);
                }
            }
        }
    }


    // Temps restant de la phase
    public long getRemainingGameplayTimeInMillis() {
        return Math.max(0, gameplayPhaseEndTime - System.currentTimeMillis());
    }


    public String getFormattedRemainingTime() {
        long millis = getRemainingGameplayTimeInMillis();
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
