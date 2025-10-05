package me.TyAlternative.matchBox.roles.list;

import me.TyAlternative.matchBox.players.PlayerRoleData;
import me.TyAlternative.matchBox.roles.GameRole;
import me.TyAlternative.matchBox.roles.enums.AbilityType;
import me.TyAlternative.matchBox.roles.enums.Role;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.awt.*;
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
//        self.sendMessage("""
//                §8§m§l----------§r§8§l / Role / §m----------
//                §l
//                §r§8§l- §r§7Vous êtes §c§nL'Etincelle
//                §r§8§l- §r§7Objectif : §rVous êtes le traître de la partie. Embrasez tous les autres §eBâtons§f et gagner seul.
//                §l
//                §r§8§l- §r§7Pour ce faire vous disposez de trois capacitées:
//                §6§n§lEmbrasement:§r§f A chaque phase de Gameplay, vous pouvez click droit sur un joueur avec une main vide. Ce dernier sera éliminé à la fin de cette phase. §8(Actif)
//                §6§n§lPoudre de cheminée:§r§f Vous pouvez échanger votre position avec celle d'un autre joueur toutes les 20 secondes. §8(Actif) §6§l(""" + getSpecialAbilityKeyBind() + """
//                )
//                §6§n§lClairvoyance:§r§f Vous connaissez la durée restante de la phase de Gameplay en cours. §8(Passif)
//                §l""");
        self.sendMessage(Component.text("§8§m§l----------§r§8§l / Role / §m----------").appendNewline()
                .appendNewline()
                .append(Component.text("§r§8§l- §r§7Vous êtes §c§nL'Etincelle")).appendNewline()
                .append(Component.text("§r§8§l- §r§7Objectif : §rVous êtes le traître de la partie. Embrasez tous les autres §eBâtons§f et gagner seul.")).appendNewline()
                .appendNewline()
                .append(Component.text("§r§8§l- §r§7Pour ce faire vous disposez de trois capacitées:")).appendNewline()
                .append(Component.text("§6§n§lEmbrasement:§r§f A chaque phase de Gameplay, vous pouvez click droit sur un joueur avec une main vide. Ce dernier sera éliminé à la fin de cette phase. §8(Actif)")).appendNewline()
                .append(Component.text("§6§n§lPoudre de cheminée:§r§f Vous pouvez échanger votre position avec celle d'un autre joueur toutes les 20 secondes. §8(Actif) §d§l(")).append(getSpecialAbilityKeyBind().style(Style.style(TextDecoration.BOLD).color(TextColor.color(255,85,255)))).append(Component.text("§d§l)")).appendNewline()
                .append(Component.text("§6§n§lClairvoyance:§r§f Vous connaissez la durée restante de la phase de Gameplay en cours. §8(Passif)")).appendNewline()
                .appendNewline()
        );

    }

    @Override
    public boolean onRightClick(Player self, Player target, boolean emptyHand) {
        if (!emptyHand) return false;
        PlayerRoleData targetData = roleManager.getPlayerRoleData(target);
        if (targetData == null || !targetData.isAlive()) {
            self.sendMessage("§cCible invalide!");
            return false;
        }
        self.sendMessage("§cCible trouvé!");
        return true;
    }

    @Override
    public boolean onSwapHand(Player self) {
        self.sendMessage("Special Ability");
        return true;
    }
}
