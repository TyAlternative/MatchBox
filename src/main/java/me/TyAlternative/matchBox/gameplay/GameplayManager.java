package me.TyAlternative.matchBox.gameplay;

import me.TyAlternative.matchBox.gameplay.enums.DeathCause;
import me.TyAlternative.matchBox.players.PlayerRoleData;
import me.TyAlternative.matchBox.roles.RoleManager;
import me.TyAlternative.matchBox.roles.enums.AbilityType;
import me.TyAlternative.matchBox.roles.enums.Role;
import me.TyAlternative.matchBox.roles.enums.TeamType;
import me.TyAlternative.matchBox.states.PlayerStates;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.*;

public class GameplayManager {
//    public List<UUID> players = new ArrayList<>();
//    public List<UUID> alivePlayers = new ArrayList<>();
    private final RoleManager roleManager = new RoleManager(this);
    private final HidePlayerManager hidePlayerManager = new HidePlayerManager(this);


    private static final GameplayManager instance = new GameplayManager();

    public static GameplayManager getInstance() {
        return instance;
    }

    public long gameplayPhaseEndTime;
    public long votePhaseEndTime;



    public void startGame(List<Player> players, Map<Role, Integer> roleDistribution) {
        roleManager.distributeRoles(players, roleDistribution);
        startGameplayPhase();
    }


    public void startGameplayPhase() {
        roleManager.embrasedPlayers.clear();
        roleManager.protectedPlayers.clear();

        int phaseDurationInSeconds = 30;
        gameplayPhaseEndTime = System.currentTimeMillis() + (phaseDurationInSeconds * 1000L);

        // On parcourt tous les joueurs
        for (PlayerRoleData data : roleManager.playerRoles.values()) {
            if (data.isAlive()) {
                Player player = Bukkit.getPlayer(data.getPlayerId());
                if (player != null) {
                    // Call le hook de debut de phase de Gameplay.
                    data.getRole().onGameplayPhaseStart(player);
                }
            }
        }
    }



    public void endGameplayPhase() {
        // On parcourt tous les joueurs
        for (PlayerRoleData data : roleManager.playerRoles.values()) {
            if (data.isAlive()) {
                Player player = Bukkit.getPlayer(data.getPlayerId());
                if (player != null) {
                    // Call le hook de fin de phase de Gameplay.
                    data.getRole().onGameplayPhaseEnd(player);
                }
            }
        }

        checkDeath();

        startVotePhase();
    }

    public void startVotePhase() {
        int phaseDurationInSeconds = 30;
        votePhaseEndTime = System.currentTimeMillis() + (phaseDurationInSeconds * 1000L);

        // On parcourt tous les joueurs
        for (PlayerRoleData data : roleManager.playerRoles.values()) {
            if (data.isAlive()) {
                Player player = Bukkit.getPlayer(data.getPlayerId());
                if (player != null) {
                    // Call le hook de debut de phase de Vote.
                    data.getRole().onVotePhaseStart(player);

                    // Modifie les attributs du joueur pour qu'il puisse plus facilement voter
                    AttributeInstance attribute = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
                    if (attribute != null) attribute.setBaseValue(20.0D);
                }
            }
        }
    }

    public void endVotePhase() {
        // On parcourt tous les joueurs
        for (PlayerRoleData data : roleManager.playerRoles.values()) {
            if (data.isAlive()) {
                Player player = Bukkit.getPlayer(data.getPlayerId());
                if (player != null) {
                    // Call le hook de fin de phase de Vote.
                    data.getRole().onVotePhaseEnd(player);


                    // Modifie les attributs du joueur pour qu'il puisse normalement jouer la partie.
                    AttributeInstance attribute = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
                    if (attribute != null) attribute.setBaseValue(3.0D);
                }
            }
        }

        startGameplayPhase();
    }



    public void checkDeath() {
        // Éliminer les joueurs embrasés (sauf les calcinés qui retardent)
        Map<UUID,DeathCause> toEliminate = new HashMap<>();

        // On parcourt chaque joueur en vie
        for (UUID embrasedId : getAlivePlayersUUID()) {
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

            // Notification aux autres joueurs (sans révéler le rôle)
            String eliminationType = switch (deathCause) {
                case ETINCELLE,TORCHE -> "§cEmbrasé";
                case VOTE -> "§baux Votes";
            };
            for (PlayerRoleData otherData : roleManager.playerRoles.values()) {
                Player player = otherData.getPlayer();
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



    // Vérification des conditions de victoire
    public GameEndResult checkWinCondition() {
        long flammeCount = roleManager.playerRoles.values().stream()
                .filter(data -> data.isAlive() && data.getRole().getTeam() == TeamType.FLAMME)
                .count();

        long batonCount = roleManager.playerRoles.values().stream()
                .filter(data -> data.isAlive() && data.getRole().getTeam() == TeamType.BATON)
                .count();

        if (flammeCount == 0) {
            return new GameEndResult(true, TeamType.BATON, "Toutes les Flammes ont été éliminées!");
        }

        if (flammeCount == 1 && batonCount == 0) {
            return new GameEndResult(true, TeamType.FLAMME, "La dernière Flamme a gagné!");
        }

        return new GameEndResult(false, null, null);
    }



    // Temps restant de la phase
    public long getRemainingGameplayTimeInMillis() {
        return Math.max(0, gameplayPhaseEndTime - System.currentTimeMillis());
    }
    // Temps restant de la phase
    public long getRemainingVoteTimeInMillis() {
        return Math.max(0, votePhaseEndTime - System.currentTimeMillis());
    }


    public String getFormattedRemainingGameplayTime() {
        long millis = getRemainingGameplayTimeInMillis();
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    public String getFormattedRemainingVoteTime() {
        long millis = getRemainingVoteTimeInMillis();
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }



    public PlayerRoleData getPlayerRoleData(Player player) {
        return roleManager.getPlayerRoleData(player);
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (PlayerRoleData playerRoleData : roleManager.playerRoles.values()) {
            Player player = playerRoleData.getPlayer();
            if (player != null) players.add(player);
        }
        return players;
    }

    public List<UUID> getPlayersUUID() {
        List<UUID> players = new ArrayList<>();
        for (PlayerRoleData playerRoleData : roleManager.playerRoles.values()) {
            Player player = playerRoleData.getPlayer();
            if (player != null) players.add(player.getUniqueId());
        }
        return players;
    }

    public List<Player> getAlivePlayers() {
        List<Player> players = new ArrayList<>();
        for (PlayerRoleData playerRoleData : roleManager.playerRoles.values()) {
            if (playerRoleData != null && playerRoleData.isAlive()) {
                Player player = playerRoleData.getPlayer();
                if (player != null) players.add(player);
            }
        }
        return players;
    }
    public List<UUID> getAlivePlayersUUID() {
        List<UUID> players = new ArrayList<>();
        for (PlayerRoleData playerRoleData : roleManager.playerRoles.values()) {
            if (playerRoleData != null && playerRoleData.isAlive()) {
                Player player = playerRoleData.getPlayer();
                if (player != null) players.add(player.getUniqueId());
            }
        }
        return players;
    }
}
