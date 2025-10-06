package me.TyAlternative.matchBox.roles.enums;

import net.kyori.adventure.text.format.TextColor;

public enum TeamType {
    BATON("Bâton", TextColor.color(230, 201, 14)),
    FLAMME("Flamme", TextColor.color(208, 4, 17)),
    NONE("Égalité", TextColor.color(255, 255, 255));

    private final String displayName;
    private final TextColor color;

    TeamType(String displayName, TextColor color) {
        this.displayName = displayName;

        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public TextColor getColor() { return color; }
}
