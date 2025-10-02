package me.TyAlternative.matchBox.players;

import me.TyAlternative.matchBox.roles.GameRole;
import me.TyAlternative.matchBox.states.PlayerStates;
import org.bukkit.Bukkit;
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

    public void setCustomData(String key, Object value) { customData.put(key, value); }
    public Object getCustomData(String key) { return customData.get(key); }



}