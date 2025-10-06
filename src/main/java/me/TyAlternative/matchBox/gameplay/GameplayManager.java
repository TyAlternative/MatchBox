package me.TyAlternative.matchBox.gameplay;

import me.TyAlternative.matchBox.MatchBox;
import me.TyAlternative.matchBox.gameplay.enums.DeathCause;
import me.TyAlternative.matchBox.gameplay.enums.GamePhase;
import me.TyAlternative.matchBox.players.PlayerRoleData;
import me.TyAlternative.matchBox.roles.RoleManager;
import me.TyAlternative.matchBox.roles.enums.AbilityType;
import me.TyAlternative.matchBox.roles.enums.Role;
import me.TyAlternative.matchBox.roles.enums.TeamType;
import me.TyAlternative.matchBox.states.PlayerStates;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GameplayManager {
//    public List<UUID> players = new ArrayList<>();
//    public List<UUID> alivePlayers = new ArrayList<>();
    private final MatchBox matchBox = MatchBox.getInstance();
    private final RoleManager roleManager = new RoleManager(this);
    private final HidePlayerManager hidePlayerManager = new HidePlayerManager(this);


    private static final GameplayManager instance = new GameplayManager();

    public static GameplayManager getInstance() {
        return instance;
    }

    public long gameplayPhaseEndTime;
    public long votePhaseEndTime;
    public GamePhase gamePhase = GamePhase.NOT_STARTED;
    private BukkitTask timePhaseTask;


    private final List<Location> placedSignLocations = new ArrayList<>();


    public void startGame(List<Player> players, Map<Role, Integer> roleDistribution) {
        roleManager.distributeRoles(players, roleDistribution);
        startGameplayPhase();
    }

    public void endGame(GameEndResult gameEndResult) {
        for (Player player : getPlayers()) {
            player.sendMessage("§e[Boite d'Allumettes] §7" + gameEndResult.getMessage());
        }

        hidePlayerManager.showAllPlayersSkin();
        hidePlayerManager.showAllPlayersNametag();

        resetGame();
    }


    public void startGameplayPhase() {
        roleManager.resetRound();

        hidePlayerManager.hideAllPlayerSkin();
        hidePlayerManager.hideAllPlayersNametag();

        gamePhase = GamePhase.GAMEPLAY;
        int phaseDurationInSeconds = 30;
        gameplayPhaseEndTime = System.currentTimeMillis() + (phaseDurationInSeconds * 1000L);

        // On parcourt tous les joueurs
        for (PlayerRoleData data : roleManager.playerRoles.values()) {
            Player player = Bukkit.getPlayer(data.getPlayerId());
            if (player == null) continue;
            givePlayerStartItem(player, data);

            if (data.isAlive()) {
                // Call le hook de debut de phase de Gameplay.
                data.getRole().onGameplayPhaseStart(player);
            }

            player.sendMessage("§e[Boite d'Allumettes] §7Début de la phase de §bGameplay!");
        }



        timePhaseTask = Bukkit.getScheduler().runTaskLater(matchBox, () -> {

            endGameplayPhase();

        },phaseDurationInSeconds * 20L);
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


        breakEveryPlacedSign();

        GameEndResult gameEndResult = checkWinCondition();
        if (gameEndResult.hasGameEnded()) {
            endGame(gameEndResult);
        } else {
            startVotePhase();
        }
    }

    public void startVotePhase() {

        gamePhase = GamePhase.VOTE;
        int phaseDurationInSeconds = 30;
        votePhaseEndTime = System.currentTimeMillis() + (phaseDurationInSeconds * 1000L);

        hidePlayerManager.showAllPlayersSkin();
        hidePlayerManager.showAllPlayersNametag();

        // On parcourt tous les joueurs
        for (PlayerRoleData data : roleManager.playerRoles.values()) {
            Player player = Bukkit.getPlayer(data.getPlayerId());
            if (player == null) continue;
            if (data.isAlive()) {
                player.getInventory().clear();
                // Call le hook de debut de phase de Vote.
                data.getRole().onVotePhaseStart(player);

                // Modifie les attributs du joueur pour qu'il puisse plus facilement voter
                AttributeInstance attribute = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
                if (attribute != null) attribute.setBaseValue(20.0D);
            }

            player.sendMessage("§e[Boite d'Allumettes] §7Début de la phase de §dVote!");
        }



        timePhaseTask = Bukkit.getScheduler().runTaskLater(matchBox, () -> {

            endVotePhase();


        },phaseDurationInSeconds * 20L);
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

        checkVote();

        GameEndResult gameEndResult = checkWinCondition();
        if (gameEndResult.hasGameEnded()) {
            endGame(gameEndResult);
        } else {
            startGameplayPhase();
        }

    }

    public void skipCurrentPhase() {
        timePhaseTask.cancel();switch (gamePhase) {
            case GAMEPLAY -> endGameplayPhase();
            case VOTE -> endVotePhase();
        }
    }


    public void givePlayerStartItem(Player player, PlayerRoleData data) {

        ItemStack signs = new ItemStack(Material.OAK_SIGN);
        signs.setAmount(16);
        player.getInventory().setItem(0,signs);

        ItemStack axe = new ItemStack(Material.NETHERITE_AXE);
        player.getInventory().setItem(1,axe);

        ItemStack spyglass = new ItemStack(Material.SPYGLASS);
        player.getInventory().setItem(8,spyglass);

        ItemStack crossbow = new ItemStack(Material.CROSSBOW);
        player.getInventory().setItem(7,crossbow);

        int remainingSpectralArrow = data.getRemainingSpectralArrow();
        if (remainingSpectralArrow > 0 ) {
            ItemStack arrows = new ItemStack(Material.SPECTRAL_ARROW);
            signs.setAmount(remainingSpectralArrow);
            player.getInventory().setItem(6, arrows);
        }
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

    public void resetPlayersVote() {
        for (Player alivePlayer : getAlivePlayers()) {
            PlayerRoleData data = getPlayerRoleData(alivePlayer);
            if (data == null) continue;
            data.unvotePlayer();
        }
    }


    public void checkVote() {
        resetPlayersVote();

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

        GameEndResult gameEndResult = null;
        if (flammeCount == 0) {
            gameEndResult = new GameEndResult(true, TeamType.BATON, "Toutes les Flammes ont été éliminées!");
        }

        if (flammeCount == 1 && batonCount == 0) {
            gameEndResult = new GameEndResult(true, TeamType.FLAMME, "La dernière Flamme a gagné!");
        }

        if (gameEndResult == null) {
            gameEndResult = new GameEndResult(false, null, null);
        }

        return gameEndResult;
    }


    public void discoverPlayerBySpectralArrow(Player target) {
        for (Player viewer : getAlivePlayers()) {
            hidePlayerManager.sendCustomNameTag(viewer, target, target.getName());
        }
    }



    public void resetGame() {
        gamePhase = GamePhase.NOT_STARTED;

        if (!timePhaseTask.isCancelled()) {
            timePhaseTask.cancel();
        }

        roleManager.reset();
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
        if (millis == 0) {
            return "Terminée";
        }
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getFormattedRemainingVoteTime() {
        long millis = getRemainingVoteTimeInMillis();
        if (millis == 0) {
            return "Terminée";
        }
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    public void addPlacedSignLocation(Location location) {
        placedSignLocations.add(location);
    }
    public void removePlacedSignLocation(Location location) {
        placedSignLocations.remove(location);
    }
    public boolean hasPlacedSignLocation(Location location) {
        return placedSignLocations.contains(location);
    }
    public List<Location> getPlacedSignLocation() {
        return placedSignLocations;
    }

    public void breakEveryPlacedSign() {
        for (Location placedSignLocation : placedSignLocations) {
            placedSignLocation.getBlock().setType(Material.AIR);

        }
        placedSignLocations.clear();
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

    public void makePlayerGlow(Player viewer, Player target, ChatColor color) {
        hidePlayerManager.makePlayerGlow(viewer, target, color);
    }
    public void resetPlayerGlow(Player viewer, Player target) {
        hidePlayerManager.resetPlayerGlow(viewer, target);
    }
}
