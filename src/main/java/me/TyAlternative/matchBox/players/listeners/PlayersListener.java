package me.TyAlternative.matchBox.players.listeners;

import me.TyAlternative.matchBox.gameplay.GameplayManager;
import me.TyAlternative.matchBox.players.PlayerRoleData;
import me.TyAlternative.matchBox.roles.GameRole;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayersListener implements Listener {
    private final GameplayManager gameplayManager= GameplayManager.getInstance();


    @EventHandler
    public void onLeftClick(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && event.getEntity() instanceof Player targetPlayer) {
            event.setCancelled(true);


            PlayerRoleData dataPlayer = gameplayManager.getPlayerRoleData(player);
            PlayerRoleData dataTarget = gameplayManager.getPlayerRoleData(targetPlayer);
            if ( dataPlayer == null || dataTarget == null ) return;
            if ( !dataPlayer.isAlive() ||!dataTarget.isAlive() ) return;

            boolean isEmptyHand = player.getInventory().getItemInMainHand().isEmpty();

            GameRole rolePlayer = dataPlayer.getRole();
            rolePlayer.onLeftClick(player, targetPlayer, isEmptyHand);

            GameRole roleTarget = dataTarget.getRole();
            roleTarget.leftClickedOn(targetPlayer, player, isEmptyHand);


        }
    }
    @EventHandler
    public void onRightClick(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        else if (event.getRightClicked() instanceof Player targetPlayer) {
            Player player = event.getPlayer();

            PlayerRoleData dataPlayer = gameplayManager.getPlayerRoleData(player);
            PlayerRoleData dataTarget = gameplayManager.getPlayerRoleData(targetPlayer);

            if ( dataPlayer == null || dataTarget == null ) return;
            if ( !dataPlayer.isAlive() ||!dataTarget.isAlive() ) return;

            boolean isEmptyHand = player.getInventory().getItemInMainHand().isEmpty();

            GameRole rolePlayer = dataPlayer.getRole();
            rolePlayer.onRightClick(player, targetPlayer, isEmptyHand);

            GameRole roleTarget = dataTarget.getRole();
            roleTarget.rightClickedOn(targetPlayer, player, isEmptyHand);
        }
    }

    @EventHandler
    public void onPlyerSwapHand(PlayerSwapHandItemsEvent event) {

        event.setCancelled(true);

        Player player = event.getPlayer();
        PlayerRoleData dataPlayer = gameplayManager.getPlayerRoleData(player);
        if (dataPlayer == null) return;

        dataPlayer.getRole().onSwapHand(player);


    }


}
