package me.TyAlternative.matchBox.players.listeners;

import me.TyAlternative.matchBox.gameplay.GameplayManager;
import me.TyAlternative.matchBox.gameplay.enums.GamePhase;
import me.TyAlternative.matchBox.players.PlayerRoleData;
import me.TyAlternative.matchBox.roles.GameRole;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class PlayersListener implements Listener {
    private final GameplayManager gameplayManager= GameplayManager.getInstance();

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof SpectralArrow arrow)) return;
        if (event.getHitBlock() != null) {
            arrow.remove();
        }
    }


    @EventHandler
    // LEFT CLICK DETECTOR AND MORE
    public void onDamage(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player player && event.getEntity() instanceof Player targetPlayer) {
            event.setCancelled(true);

            if (gameplayManager.gamePhase == GamePhase.GAMEPLAY) {


                PlayerRoleData dataPlayer = gameplayManager.getPlayerRoleData(player);
                PlayerRoleData dataTarget = gameplayManager.getPlayerRoleData(targetPlayer);
                if (dataPlayer == null || dataTarget == null) return;
                if (!dataPlayer.isAlive() || !dataTarget.isAlive()) return;

                boolean isEmptyHand = player.getInventory().getItemInMainHand().isEmpty();

                GameRole rolePlayer = dataPlayer.getRole();
                rolePlayer.onLeftClick(player, targetPlayer, isEmptyHand);

                GameRole roleTarget = dataTarget.getRole();
                roleTarget.leftClickedOn(targetPlayer, player, isEmptyHand);
            } else if (gameplayManager.gamePhase == GamePhase.VOTE) {

                PlayerRoleData dataPlayer = gameplayManager.getPlayerRoleData(player);
                if (dataPlayer == null) return;
                dataPlayer.clickVote(targetPlayer);
            }
        }

        if (event.getDamager() instanceof SpectralArrow spectralArrow && event.getEntity() instanceof Player targetPlayer) {
            ProjectileSource shooter = spectralArrow.getShooter();
            if (shooter instanceof Player damager) {
                event.setCancelled(true);
                spectralArrow.remove();

                PlayerRoleData damagerData = gameplayManager.getPlayerRoleData(damager);
                damagerData.removeOneSpectralArrow();
                gameplayManager.discoverPlayerBySpectralArrow(targetPlayer);
            }
        }

    }
    @EventHandler
    public void onRightClick(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        else if (event.getRightClicked() instanceof Player targetPlayer) {
            Player player = event.getPlayer();

            if (gameplayManager.gamePhase == GamePhase.GAMEPLAY) {

                PlayerRoleData dataPlayer = gameplayManager.getPlayerRoleData(player);
                PlayerRoleData dataTarget = gameplayManager.getPlayerRoleData(targetPlayer);

                if (dataPlayer == null || dataTarget == null) return;
                if (!dataPlayer.isAlive() || !dataTarget.isAlive()) return;

                boolean isEmptyHand = player.getInventory().getItemInMainHand().isEmpty();

                GameRole rolePlayer = dataPlayer.getRole();
                rolePlayer.onRightClick(player, targetPlayer, isEmptyHand);

                GameRole roleTarget = dataTarget.getRole();
                roleTarget.rightClickedOn(targetPlayer, player, isEmptyHand);
            } else if (gameplayManager.gamePhase == GamePhase.VOTE) {

                PlayerRoleData dataPlayer = gameplayManager.getPlayerRoleData(player);
                if (dataPlayer == null) return;
                dataPlayer.clickVote(targetPlayer);
            }
        }
    }

    @EventHandler
    public void onPlyerSwapHand(PlayerSwapHandItemsEvent event) {

        if (gameplayManager.gamePhase != GamePhase.GAMEPLAY && gameplayManager.gamePhase != GamePhase.VOTE) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        PlayerRoleData dataPlayer = gameplayManager.getPlayerRoleData(player);
        if (dataPlayer == null) return;

        dataPlayer.getRole().onSwapHand(player);


    }


    // Gameplay Mechanics
    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (gameplayManager.gamePhase != GamePhase.GAMEPLAY && gameplayManager.gamePhase != GamePhase.VOTE) return;
        if (gameplayManager.gamePhase == GamePhase.VOTE) {
            event.setCancelled(true);
            return;
        }
        Block placedBlock = event.getBlock();

        if (placedBlock.getType() == Material.OAK_SIGN || placedBlock.getType() == Material.OAK_WALL_SIGN) {

            Location placedSignLocation = placedBlock.getLocation();
            if (!gameplayManager.hasPlacedSignLocation(placedSignLocation)) {
                gameplayManager.addPlacedSignLocation(placedSignLocation);
                for (Location location : gameplayManager.getPlacedSignLocation()) {
                    event.getPlayer().sendMessage("§7" + location.getX() + " " + location.getY() + " " + location.getZ() + " in " + location.getWorld().getName());
                }
            }

        } else {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (gameplayManager.gamePhase != GamePhase.GAMEPLAY && gameplayManager.gamePhase != GamePhase.VOTE) return;
        if (gameplayManager.gamePhase == GamePhase.VOTE) {
            event.setCancelled(true);
            return;
        }
        Block brokeBlock = event.getBlock();

        if (brokeBlock.getType() == Material.OAK_SIGN || brokeBlock.getType() == Material.OAK_WALL_SIGN) {

            Location brokeSignLocation = brokeBlock.getLocation();
            if (gameplayManager.hasPlacedSignLocation(brokeSignLocation)) {
                gameplayManager.removePlacedSignLocation(brokeSignLocation);
                for (Location location : gameplayManager.getPlacedSignLocation()) {
                    event.getPlayer().sendMessage("§7" + location.getX() + " " + location.getY() + " " + location.getZ() + " in " + location.getWorld().getName());
                }
            }

        } else {
            event.setCancelled(true);
        }
    }





    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (gameplayManager.gamePhase != GamePhase.GAMEPLAY && gameplayManager.gamePhase != GamePhase.VOTE) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;
            if (clickedBlock.getType().toString().toUpperCase().contains("TRAPDOOR")) {
                event.setCancelled(true);
            }
        }


//
//
//
//        if (player.getGameMode() != GameMode.SPECTATOR) return;
//
//        switch (event.getAction()) {
//            case LEFT_CLICK_AIR:
//                player.sendMessage("§eClic gauche air");
//                break;
//            case RIGHT_CLICK_AIR:
//                player.sendMessage("§eClic droit air");
//                break;
//            case LEFT_CLICK_BLOCK:
//                player.sendMessage("§eClic gauche bloc: " + event.getClickedBlock().getType());
//                break;
//            case RIGHT_CLICK_BLOCK:
//                player.sendMessage("§eClic droit bloc: " + event.getClickedBlock().getType());
//                break;
//        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode() != GameMode.SPECTATOR) return;
        player.sendMessage("§e[Boite d'Allumettes] §7" + event.getSlot() + " - " + event.getSlotType().toString());

    }


}
