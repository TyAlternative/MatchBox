package me.TyAlternative.matchBox.roles.list;

import me.TyAlternative.matchBox.players.PlayerRoleData;
import me.TyAlternative.matchBox.roles.GameRole;
import me.TyAlternative.matchBox.roles.enums.AbilityType;
import me.TyAlternative.matchBox.roles.enums.Role;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Etincelle extends GameRole {
    public Etincelle() {
        super(Role.ETINCELLE, """
                        Le traître de la partie. Vous pouvez embraser et échanger de position""");
    }


    @Override
    public List<AbilityType> getAbilities() {
        return Arrays.asList(AbilityType.EMBRASEMENT, AbilityType.POUDRE_CHEMINEE, AbilityType.CLAIRVOYANCE);
    }
    @Override
    public boolean hasAbility(AbilityType type) {
        return getAbilities().contains(type);
    }

    @Override
    public void onRoleAssigned(Player self) {
        self.sendMessage("§8---------------------------------------------------------");
        self.sendMessage("§e[Boite d'Allumettes] §7Vous etes " + getDisplayName() + "§!\n" + getDescription());
        self.sendMessage("§8---------------------------------------------------------");
    }

    @Override
    public boolean onRightClick(Player self, Player target, boolean emptyHand) {
        PlayerRoleData targetData = roleManager.getPlayerRoleData(target);
        if (targetData == null || !targetData.isAlive()) {
            self.sendMessage("§cCible invalide!");
            return false;
        }
        self.sendMessage("§cCible trouvé!");
        return true;
    }

}
