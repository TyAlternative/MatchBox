package me.TyAlternative.matchBox.gameplay;

import me.TyAlternative.matchBox.roles.enums.TeamType;

public class GameEndResult {
    private final boolean gameEnded;
    private final TeamType winner;
    private final String message;

    public GameEndResult(boolean gameEnded, TeamType winner, String message) {
        this.gameEnded = gameEnded;
        this.winner = winner;
        this.message = message;
    }

    public boolean hasGameEnded() { return gameEnded; }
    public TeamType getWinner() { return winner; }
    public String getMessage() { return message; }
}