package me.TyAlternative.matchBox.roles.list;

import me.TyAlternative.matchBox.roles.GameRole;
import me.TyAlternative.matchBox.roles.enums.Role;

public class Torche extends GameRole {
    public Torche() {
        super(Role.TORCHE, """
                Votre présence seule représente un danger pour les autres""");
    }
}
