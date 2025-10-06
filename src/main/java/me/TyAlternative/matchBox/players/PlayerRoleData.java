package me.TyAlternative.matchBox.players;

import me.TyAlternative.matchBox.gameplay.GameplayManager;
import me.TyAlternative.matchBox.roles.GameRole;
import me.TyAlternative.matchBox.states.PlayerStates;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRoleData {
    private final UUID playerId;
    private final GameRole role;
    private PlayerStates playerStates;
    private int remainingSpectralArrow;
    private Map<String, Object> customData;
    
    public boolean canVote = true;
    public int voteWeight = 1;
    public boolean alreadyVote = false;
    public Player votedPlayer = null;

    public PlayerRoleData(UUID playerId, GameRole role) {
        this.playerId = playerId;
        this.role = role;
        this.playerStates = PlayerStates.PLAYING;
        this.remainingSpectralArrow = 1;
        this.customData = new HashMap<>();
    }


    public UUID getPlayerId() { return playerId; }
    public Player getPlayer() { return Bukkit.getPlayer(playerId); }
    public GameRole getRole() { return role; }

    public PlayerStates getPlayerStates() { return playerStates; }
    public void setPlayerStates(PlayerStates playerStates) {this.playerStates = playerStates; }

    public boolean isAlive() { return this.playerStates == PlayerStates.PLAYING; }

    public int getRemainingSpectralArrow() { return remainingSpectralArrow; }
    public void setRemainingSpectralArrow(int newValue) { this.remainingSpectralArrow = newValue; }
    public void removeOneSpectralArrow() { this.remainingSpectralArrow--; }
    public void addOneSpectralArrow() { this.remainingSpectralArrow++; }

    public void setCustomData(String key, Object value) { customData.put(key, value); }
    public Object getCustomData(String key) { return customData.get(key); }


    public void votePlayer(Player clickedPlayer) {
        if (!canVote || clickedPlayer == null) return;
        else if (this.votedPlayer == clickedPlayer) return;
        else if (this.votedPlayer != null) unvotePlayer();


        this.votedPlayer = clickedPlayer;
        GameplayManager.getInstance().makePlayerGlow(getPlayer(), clickedPlayer, ChatColor.YELLOW);
    }
    public void unvotePlayer() {
        if (!canVote || this.votedPlayer == null) return;

        GameplayManager.getInstance().resetPlayerGlow(getPlayer(), this.votedPlayer);
        this.votedPlayer = null;

    }
    
    public void clickVote(Player clickedPlayer) {

        Player previousClickedPlayer = this.votedPlayer;
        if (clickedPlayer == null) return;
        if (previousClickedPlayer == clickedPlayer) {
            getPlayer().sendMessage("§e[Boite d'Allumettes] §7Vous avez retiré votre vote contre §e" + clickedPlayer.getName());
            unvotePlayer();
        } else {
            getPlayer().sendMessage("§e[Boite d'Allumettes] §7Vous avez voté contre §e" + clickedPlayer.getName());
            votePlayer(clickedPlayer);
        }
    }

}