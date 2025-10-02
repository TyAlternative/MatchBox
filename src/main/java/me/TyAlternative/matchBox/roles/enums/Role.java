package me.TyAlternative.matchBox.roles.enums;

import me.TyAlternative.matchBox.roles.GameRole;
import me.TyAlternative.matchBox.roles.list.*;
import org.bukkit.Material;

public enum Role {
    ETINCELLE("L'Étincelle", TeamType.FLAMME, Material.BLAZE_POWDER),
    TORCHE("La Torche", TeamType.FLAMME, Material.TORCH),

    SOUFFLE("Le Souffle", TeamType.BATON, Material.WIND_CHARGE),
    CENDRE("La Cendre", TeamType.BATON, Material.GUNPOWDER),
    CALCINE("Le Calciné", TeamType.BATON, Material.GRAY_CONCRETE_POWDER),
    AURORE("L'Aurore", TeamType.BATON, Material.MAGENTA_STAINED_GLASS),
    POMPIER("Le Pompier", TeamType.BATON, Material.SPLASH_POTION),
    NUEE("La Nuée", TeamType.BATON, Material.COAL_BLOCK),
    AUBE("L'Aube", TeamType.BATON, Material.ORANGE_STAINED_GLASS),
    BATON("Le Bâton", TeamType.BATON, Material.STICK);

    private String displayName;
    private TeamType teamType;
    private Material guiItem;

    Role(String displayName, TeamType teamType, Material guiItem) {
        this.displayName = displayName;
        this.teamType = teamType;
        this.guiItem = guiItem;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TeamType getTeamType() {
        return teamType;
    }

    public Material getGuiItem() {
        return guiItem;
    }

    public GameRole instantiateRole() {
        return switch (this) {
            case ETINCELLE -> new Etincelle();
            case TORCHE -> new Torche();
            case SOUFFLE -> new Souffle();
            case CENDRE -> new Cendre();
            case CALCINE -> new Calcine();
            case AURORE -> new Aurore();
            case BATON -> new Baton();
            default -> new Baton();
        };
    }

    public static Role fromString(String string) {
        try {
            return Role.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}