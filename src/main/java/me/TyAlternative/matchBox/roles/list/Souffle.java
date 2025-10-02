package me.TyAlternative.matchBox.roles.list;

import me.TyAlternative.matchBox.roles.GameRole;
import me.TyAlternative.matchBox.roles.enums.Role;

public class Souffle extends GameRole {
    public Souffle() {
        super(Role.SOUFFLE, """
                Il s'agit de l'un des alliés le plus fort pour contrer l'Etincelle.""");
    }
}