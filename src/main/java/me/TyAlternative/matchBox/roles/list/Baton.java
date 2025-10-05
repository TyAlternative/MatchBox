package me.TyAlternative.matchBox.roles.list;

import me.TyAlternative.matchBox.roles.GameRole;
import me.TyAlternative.matchBox.roles.enums.Role;
import org.bukkit.entity.Player;

public class Baton  extends GameRole {
    public Baton() {
        super(Role.BATON, """
                L’objet inflammable par excellence, il n’est bon qu'à être brûlé.""");
    }


    @Override
    public void onRoleAssigned(Player self) {
        self.sendMessage("§8-------------------------------------------------");
        self.sendMessage("§e[Boite d'Allumettes] §7Vous etes " + getDisplayName() + "§!\n" + getDescription());
        self.sendMessage("§8-------------------------------------------------");
    }
}
