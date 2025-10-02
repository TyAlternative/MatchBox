package me.TyAlternative.matchBox.composition;

import me.TyAlternative.matchBox.Keys;
import me.TyAlternative.matchBox.MatchBox;
import me.TyAlternative.matchBox.PDM;
import me.TyAlternative.matchBox.roles.enums.Role;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class CompositionGUI implements Listener {
    private static final String GUI_TITLE = "§8Composition des Rôles";
    private static final int GUI_SIZE = 54; // Large Chest

    private static int flammesPageCount = 0;
    private static int batonsPageCount = 0;


    public static void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);

        // Remplir les bordures avec des panneaux gris
        fillBorders(inv);

        // Mettre à jour les rôles
        updateInventory(inv);

        // Boutons en bas
        ItemStack resetButton = createItem(Material.BARRIER, "§c§lRéinitialiser",
                "§7Clic pour effacer toute la composition");
        inv.setItem(45, resetButton);

        ItemStack infoButton = createItem(Material.COMPASS, "§a§lValider & Fermer",
                "§7Clic pour sauvegarder et fermer");
        inv.setItem(53, infoButton);

        ItemStack helpButton = createItem(Material.BOOK, "§e§lAide",
                "§7Clic gauche: §c-1",
                        "§7Clic droit: §a+1");
//                        "§7Shift + Gauche: §c-5",
//                        "§7Shift + Droit: §a+5");
        inv.setItem(49, helpButton);

        player.openInventory(inv);
    }

    private static void fillBorders(Inventory inv) {
        ItemStack border = getBorderItem();

        ItemMeta meta = border.getItemMeta();
        meta.setHideTooltip(true);
        border.setItemMeta(meta);
        int[] borders = {
                1,1,1,1,1,1,1,1,1,
                0,0,0,0,0,0,0,0,0,
                1,0,0,0,0,0,0,0,1,
                0,0,0,0,0,0,0,0,0,
                1,0,0,0,0,0,0,0,1,
                0,1,1,1,0,1,1,1,0
        };


        for (int i = 0; i < borders.length; i++) {
            if (borders[i] == 1) {
                inv.setItem(i, border);
            }
        }
    }
    private static ItemStack getBorderItem() {
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

        ItemMeta meta = border.getItemMeta();
        meta.setHideTooltip(true);
        border.setItemMeta(meta);

        return border;

    }

    private static ItemStack getArrowNext() {
        ItemStack item = createItem(Material.YELLOW_STAINED_GLASS_PANE,"§6Prochaine Page");
        return item;
    }
    private static ItemStack getArrowPrevious() {
        ItemStack item = createItem(Material.YELLOW_STAINED_GLASS_PANE,"§6Précédente Page");
        return item;
    }
    private static ItemStack getBlockedArrowNext() {
        ItemStack item = createItem(Material.ORANGE_STAINED_GLASS_PANE," ");
        return item;
    }
    private static ItemStack getBlockedArrowPrevious() {
        ItemStack item = createItem(Material.ORANGE_STAINED_GLASS_PANE," ");
        return item;
    }
    private static ItemStack getRolePresent() {
        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        PDM.setBool(item, Keys.INFO_ROLE_GUI_ITEM, true);
        return item;
    }
    private static ItemStack getRoleNotPresent() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        PDM.setBool(item, Keys.INFO_ROLE_GUI_ITEM, true);
        return item;
    }

    private static void updateInventory(Inventory inv) {
        Map<Role, Integer> distribution = CompositionManager.getRoleDistribution();



        Role[] roles = Role.values();
        List<Role> flammesRoles = new ArrayList<>();
        List<Role> batonsRoles = new ArrayList<>();

        for (Role role : roles) {
            switch (role.getTeamType()) {
                case FLAMME ->  flammesRoles.add(role);
                case BATON ->  batonsRoles.add(role);
            }
        }

        int[] flammesAvailableSlots = {10, 11, 12, 13, 14, 15, 16};
        int[] batonsAvailableSlots = {28, 29, 30, 31, 32, 33, 34,};

        // Afficher les boutons de changement de pages
        if (flammesPageCount == 0) {
            inv.setItem(9,getBlockedArrowPrevious());
        }
        else {
            inv.setItem(9,getArrowPrevious());
        }
        if ((flammesPageCount + 1) * flammesAvailableSlots.length >= flammesRoles.size()) {
            inv.setItem(17,getBlockedArrowNext());
        }
        else {
            inv.setItem(17,getArrowNext());
        }


        if (batonsPageCount == 0) {
            inv.setItem(27,getBlockedArrowPrevious());
        }
        else {
            inv.setItem(27,getArrowPrevious());
        }
        if ((batonsPageCount + 1) * batonsAvailableSlots.length >= batonsRoles.size()) {
            inv.setItem(35,getBlockedArrowNext());
        }
        else {
            inv.setItem(35, getArrowNext());
        }

        // Slots disponibles (excluant les bordures)

        int listIndex = 0;
        int slotIndex = 0;
        for (Role flammesRole : flammesRoles) {
            if (flammesPageCount * flammesAvailableSlots.length <= listIndex && (flammesPageCount + 1) * flammesAvailableSlots.length > listIndex ) {
                Integer count = distribution.getOrDefault(flammesRole, 0);
                if (count == 0) {
                    inv.setItem(flammesAvailableSlots[slotIndex]+9, getRoleNotPresent());
                } else {
                    inv.setItem(flammesAvailableSlots[slotIndex]+9, getRolePresent());
                }
                ItemStack item = createRoleItem(flammesRole, count);
                inv.setItem(flammesAvailableSlots[slotIndex], item);

                slotIndex++;
            }

            listIndex++;
        }
        while (slotIndex < 7) {
            inv.setItem(slotIndex+10, getBorderItem());
            inv.setItem(slotIndex+19, getBorderItem());
            slotIndex++;
        }
        listIndex = 0;
        slotIndex = 0;
        for (Role batonsRole : batonsRoles) {
            if (batonsPageCount * batonsAvailableSlots.length <= listIndex && (batonsPageCount + 1) * batonsAvailableSlots.length > listIndex ) {
                Integer count = distribution.getOrDefault(batonsRole, 0);
                if (count == 0) {
                    inv.setItem(batonsAvailableSlots[slotIndex]+9, getRoleNotPresent());
                } else {
                    inv.setItem(batonsAvailableSlots[slotIndex]+9, getRolePresent());
                }
                ItemStack item = createRoleItem(batonsRole, count);
                inv.setItem(batonsAvailableSlots[slotIndex], item);

                slotIndex++;
            }

            listIndex++;
        }
        while (slotIndex < 7) {
            inv.setItem(slotIndex+28, getBorderItem());
            inv.setItem(slotIndex+37, getBorderItem());
            slotIndex++;
        }

    }

    private static ItemStack createRoleItem(Role role, int count) {
        Material material = role.getGuiItem();
        String displayName = "§d§l" + role.getDisplayName();

        List<String> lore = new ArrayList<>();
        lore.add("§7Quantité: §e" + count);
        lore.add("");
        if (count > 0) {
            lore.add("§a✓ §7Actif dans la composition");
            lore.add("");
        }
        lore.add("§7Clic gauche: §c-1");
        lore.add("§7Clic droit: §a+1");
//        lore.add("§7Shift + Gauche: §c-5");
//        lore.add("§7Shift + Droit: §a+5");

        ItemStack item = new ItemStack(material, Math.max(1, count));
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(displayName);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        PDM.setString(item, Keys.ROLE_GUI_ITEM, role.toString());

        return item;
    }

    private static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                List<String> loreList = new ArrayList<>();
                for (String line : lore) {
                    loreList.add(line);
                }
                meta.setLore(loreList);
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        int slot = event.getRawSlot();

        // Bouton reset
        if (slot == 45) {
            CompositionManager.getRoleDistribution().clear();
            player.sendMessage("§c✖ §7Composition réinitialisée !");
            updateInventory(event.getInventory());
            return;
        }

        // Bouton valider et fermer
        if (slot == 53) {
            player.closeInventory();
            player.sendMessage("§a✓ §7Composition sauvegardée !");
            CompositionManager.displayCompo(player);
            return;
        }

        // Bouton aide
        if (slot == 49) {
            return;
        }

        // Pages
        if (slot == 9) {
            if (clicked.getType() == getArrowPrevious().getType()) {
                flammesPageCount = Math.max(0, flammesPageCount - 1);
            }
            updateInventory(event.getInventory());
            return;
        }
        if (slot == 17) {
            if (clicked.getType() == getArrowNext().getType()) {
                flammesPageCount = Math.max(0, flammesPageCount - 1);
            }
            updateInventory(event.getInventory());
            return;

        }
        if (slot == 27) {
            if (clicked.getType() == getArrowPrevious().getType()) {
                batonsPageCount--;
            }
            updateInventory(event.getInventory());
            return;

        }
        if (slot == 35) {
            if (clicked.getType() == getArrowNext().getType()) {
                batonsPageCount++;
            }
            updateInventory(event.getInventory());
            return;
        }

        // Slots disponibles pour les rôles
        Role role = Role.fromString(PDM.getStringOrDefault(clicked, Keys.ROLE_GUI_ITEM, ""));
        ClickType clickType = event.getClick();
        if (role != null) {

            int change = 0;
            switch (clickType) {
                case LEFT:
                    change = -1;
                    CompositionManager.removeRole(role, 1);
                    break;
                case RIGHT:
                    change = 1;
                    CompositionManager.addRole(role, 1);
                    break;
                case SHIFT_LEFT:
                    change = -5;
                    CompositionManager.removeRole(role, 5);
                    break;
                case SHIFT_RIGHT:
                    change = 5;
                    CompositionManager.addRole(role, 5);
                    break;
                default:
                    return;
            }

            updateInventory(event.getInventory());

            // Message de feedback
//            int currentCount = CompositionManager.getRoleDistribution().getOrDefault(role, 0);
//            String changeStr = change > 0 ? "§a+" + change : "§c" + change;
//            player.sendMessage("§d" + role.toString() + " §7" + changeStr + " §8→ §e" + currentCount);
        }


    }
}