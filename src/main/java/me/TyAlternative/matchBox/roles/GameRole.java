package me.TyAlternative.matchBox.roles;

import me.TyAlternative.matchBox.gameplay.enums.DeathCause;
import me.TyAlternative.matchBox.roles.enums.AbilityType;
import me.TyAlternative.matchBox.roles.enums.Role;
import me.TyAlternative.matchBox.roles.enums.TeamType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class GameRole {
    private final Role role;
    private final TeamType team;
    private final String displayName;
    private final String description;
    protected RoleManager roleManager;

    private boolean isDrunk;


    public GameRole(Role role, String description) {
        this.role = role;
        this.team = role.getTeamType();
        this.displayName = role.getDisplayName();
        this.description = description;
    }

    public void setRoleManager(RoleManager roleManager) { this.roleManager = roleManager; }

    public Role getRoleId() { return role; }
    public TeamType getTeam() { return team; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }

    public boolean isDrunk() { return isDrunk; }
    public void setIsDrunk(boolean value) { isDrunk = value; }


    // Hooks pour les événements
    /** Hook qui detect quand le joueur reçoit son role. */
    public void onRoleAssigned(Player self) {}

    /** Hook qui detect quand la début de la phase de Gameplay a lieu. */
    public void onGameplayPhaseStart(Player self) {}

    /** Hook qui detect quand la fin de la phase de Gameplay a lieu. Appelé avant le calcul des morts. */
    public void onGameplayPhaseEnd(Player self) {}

    /** Hook qui detect quand la début de la phase de Vote a lieu. */
    public void onVotePhaseStart(Player self) {}

    /** Hook qui detect quand la fin de la phase de Vote a lieu. Appelé avant le calcul des morts. */
    public void onVotePhaseEnd(Player self) {}

    /** Hook qui detect quand un autre joueur meurt. */
    public void onOtherPlayerElimination(Player eliminatedPlayer, DeathCause deathCause, Player self) {}

    /** Hook qui detect quand le joueur meurt. */
    public void onPlayerElimination(Player self, DeathCause deathCause) {}


    // Interactions avec clic droit
    public boolean onRightClick(Player self, Player target, boolean emptyHand) { return false; }


    // Interactions avec clic gauche
    public boolean onLeftClick(Player self, Player target, boolean emptyHand) { return false; }



    // A été clic droit
    public boolean rightClickedOn(Player self, Player target, boolean emptyHand) { return false; }

    // A été clic gauche
    public boolean leftClickedOn(Player self, Player target, boolean emptyHand) { return false; }

    // Interactions avec Swap Hand
    public boolean onSwapHand(Player self) { return false; }

    public boolean hasAbility(AbilityType type) { return false; }

    public List<AbilityType> getAbilities() { return new ArrayList<>(); }



}
