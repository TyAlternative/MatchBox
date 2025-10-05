package me.TyAlternative.matchBox.players.listeners;

import me.TyAlternative.matchBox.gameplay.GameplayManager;
import me.TyAlternative.matchBox.players.PlayerRoleData;
import me.TyAlternative.matchBox.roles.GameRole;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p.getGameMode() != GameMode.SPECTATOR) return;

        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
                p.sendMessage("§eClic gauche air");
                break;
            case RIGHT_CLICK_AIR:
                p.sendMessage("§eClic droit air");
                break;
            case LEFT_CLICK_BLOCK:
                p.sendMessage("§eClic gauche bloc: " + event.getClickedBlock().getType());
                break;
            case RIGHT_CLICK_BLOCK:
                p.sendMessage("§eClic droit bloc: " + event.getClickedBlock().getType());
                break;
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode() != GameMode.SPECTATOR) return;
        player.sendMessage("§e[Boite d'Allumettes] §7" + event.getSlot() + " - " + event.getSlotType().toString());

    }


}
