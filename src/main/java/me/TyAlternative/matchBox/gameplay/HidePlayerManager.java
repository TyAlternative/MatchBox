package me.TyAlternative.matchBox.gameplay;
import fr.skytasul.glowingentities.GlowingEntities;
import me.TyAlternative.matchBox.Keys;
import me.TyAlternative.matchBox.MatchBox;
import me.TyAlternative.matchBox.PDM;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class HidePlayerManager {

    private final GameplayManager gameplayManager;

    private final GlowingEntities glowingEntities = new GlowingEntities(MatchBox.getInstance());

    // Maps pour stocker les configurations
    private Map<UUID, Map<UUID, NameTag>> nameTagConfigs = new ConcurrentHashMap<>();

    // Map pour stocker les TextDisplay entities : ViewerUUID -> TargetUUID -> TextDisplay
    private Map<UUID, Map<UUID, TextDisplay>> displayEntities = new ConcurrentHashMap<>();


    HidePlayerManager(GameplayManager gameplayManager) {
        this.gameplayManager = gameplayManager;
    }

    /**
     * Configuration d'un nametag
     */

    private class NameTag {
        String customName;
        boolean hidden;
        boolean seeTrough;

        NameTag(String customName, boolean hidden) {
            this.customName = customName;
            this.hidden = hidden;
            this.seeTrough = true;
        }
        NameTag(String customName, boolean hidden, boolean seeTrough) {
            this.customName = customName;
            this.hidden = hidden;
            this.seeTrough = seeTrough;
        }
    }



    /**
     * MÉTHODE PRINCIPALE : Affiche un nametag personnalisé
     */
    public void sendCustomNameTag(Player viewer, Player target, String customName) {
        if (viewer == null || target == null || customName == null) {
            return;
        }

        // Supprimer l'ancienne entity s'il en existe une
        removeDisplayEntity(viewer, target);

        // Stocker la configuration
        nameTagConfigs.computeIfAbsent(viewer.getUniqueId(), k -> new ConcurrentHashMap<>())
                .put(target.getUniqueId(), new NameTag(customName, false));

        // Créer la nouvelle TextDisplay entity
        createDisplayEntity(viewer, target, customName);

        viewer.sendMessage(ChatColor.GREEN + "✓ Nametag personnalisé: " + customName);
    }

    /**
     * MÉTHODE RESET : Restaure le nametag original
     */
    public void resetNameTag(Player viewer, Player target) {
        if (viewer == null || target == null) {
            return;
        }

        // Supprimer la configuration
        Map<UUID, NameTag> viewerConfigs = nameTagConfigs.get(viewer.getUniqueId());
        if (viewerConfigs != null) {
            viewerConfigs.remove(target.getUniqueId());
        }

        // Supprimer l'entity personnalisée
        removeDisplayEntity(viewer, target);

        // Créer une entity avec le nom original
        String originalName = target.getDisplayName() != null ? target.getDisplayName() : target.getName();
        createDisplayEntity(viewer, target, originalName);

        viewer.sendMessage(ChatColor.GREEN + "✓ Nametag restauré pour " + target.getName());
    }

    /**
     * MÉTHODE HIDE : Cache complètement le nametag
     */
    public void hideNameTag(Player viewer, Player target) {
        if (viewer == null || target == null) {
            return;
        }

        // Stocker la configuration de masquage
        nameTagConfigs.computeIfAbsent(viewer.getUniqueId(), k -> new ConcurrentHashMap<>())
                .put(target.getUniqueId(), new NameTag("", true));

        // Supprimer complètement l'entity
        removeDisplayEntity(viewer, target);

        viewer.sendMessage(ChatColor.GREEN + "✓ Nametag caché pour " + target.getName());
    }


    /**
     * Crée une TextDisplay entity qui "ride" sur le joueur target
     */
    private void createDisplayEntity(Player viewer, Player target, String displayText) {
        try {

            // Position initiale au-dessus de la tête du joueur
            Location spawnLoc = target.getLocation().add(0, target.getHeight() + 0.3, 0);


            // Créer la TextDisplay entity
            TextDisplay textDisplay = target.getWorld().spawn(spawnLoc, TextDisplay.class, entity -> {
                // Configuration de l'entity AVANT le spawn

                // 1. Texte à afficher (support couleurs)
                Component textComponent = LegacyComponentSerializer.legacySection().deserialize(displayText);
                entity.text(textComponent);

                // 2. Configuration d'affichage optimisée pour le riding
                entity.setBillboard(Display.Billboard.CENTER); // Centré et face au joueur
                entity.setSeeThrough(false);                    // Transparence à travers les blocs
                entity.setDefaultBackground(false);             // Pas de fond par défaut
                entity.setShadowed(false);                      // Ombre pour meilleure lisibilité

                // 3. Échelle optimisée pour le riding (légèrement plus petite)
                Transformation transformation = new Transformation(
                        new Vector3f(0, 0.3f, 0),         // Translation légèrement vers le haut
                        new AxisAngle4f(0, 0, 0, 1),      // Pas de rotation gauche
                        new Vector3f(1.0f, 1.0f, 1.0f),   // Échelle réduite (80%)
                        new AxisAngle4f(0, 0, 0, 1)       // Pas de rotation droite
                );
                entity.setTransformation(transformation);

                // 4. Propriétés d'entité optimisées pour riding
                entity.setGravity(false);          // Pas de gravité (important pour riding)
                entity.setInvulnerable(true);      // Indestructible
                entity.setSilent(true);            // Silencieux
                entity.setPersistent(false);       // Ne persiste pas après redémarrage
                entity.setVisibleByDefault(false); // INVISIBLE PAR DÉFAUT

                // 5. Métadonnées pour identification
                entity.setCustomName("riding_nametag_" + target.getName() + "_for_" + viewer.getName());
                entity.setCustomNameVisible(false);
            });


            // CRUCIAL : Rendre visible SEULEMENT pour le viewer spécifique
            textDisplay.setVisibleByDefault(false);
            viewer.showEntity(MatchBox.getInstance(), textDisplay);

            // Faire rider l'entity sur le joueur !
            target.addPassenger(textDisplay);

            // Stocker la référence
            displayEntities.computeIfAbsent(viewer.getUniqueId(), k -> new ConcurrentHashMap<>())
                    .put(target.getUniqueId(), textDisplay);

        } catch (Exception e) {
            MatchBox.getInstance().getLogger().warning("Erreur création TextDisplay riding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Supprime une TextDisplay entity qui ride sur un joueur
     */
    private void removeDisplayEntity(Player viewer, Player target) {

        Map<UUID, TextDisplay> viewerEntities = displayEntities.get(viewer.getUniqueId());
        if (viewerEntities == null) return;

        TextDisplay entity = viewerEntities.remove(target.getUniqueId());
        if (entity == null || !entity.isValid()) return;

        try {
            // Faire descendre l'entity du joueur AVANT de la supprimer
            if (target.isOnline() && target.getPassengers().contains(entity)) {
                target.removePassenger(entity);
            }

            // Cacher l'entity du viewer
            if (viewer.isOnline()) {
                viewer.hideEntity(MatchBox.getInstance(), entity);
            }

            // Puis la supprimer complètement
            entity.remove();

        } catch (Exception e) {
            MatchBox.getInstance().getLogger().warning("Erreur suppression TextDisplay riding: " + e.getMessage());
        }
    }

    /**
     * Task simplifiée - Plus besoin de mettre à jour les positions !
     * Les entities riding suivent automatiquement le joueur
     */
    private void startPositionUpdateTask() {
        // Task allégée pour nettoyer les entities invalides
        Bukkit.getScheduler().runTaskTimer(MatchBox.getInstance(), () -> {
            for (UUID viewerUuid : displayEntities.keySet()) {
                Player viewer = Bukkit.getPlayer(viewerUuid);
                if (viewer == null || !viewer.isOnline()) continue;

                Map<UUID, TextDisplay> viewerEntities = displayEntities.get(viewerUuid);
                Iterator<Map.Entry<UUID, TextDisplay>> iterator = viewerEntities.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<UUID, TextDisplay> entry = iterator.next();
                    Player target = Bukkit.getPlayer(entry.getKey());
                    TextDisplay entity = entry.getValue();

                    if (target == null || !target.isOnline() || entity == null || !entity.isValid()) {
                        // Nettoyer les entities invalides
                        if (entity != null && entity.isValid()) {
                            // Faire descendre l'entity avant suppression
                            if (target != null && target.isOnline()) {
                                target.removePassenger(entity);
                            }
                            entity.remove();
                        }
                        iterator.remove();
                        continue;
                    }

                    // Vérifier que l'entity est toujours en train de rider le target
                    if (!target.getPassengers().contains(entity)) {
                        // Re-faire rider l'entity si elle n'est plus sur le joueur
                        try {
                            target.addPassenger(entity);
                        } catch (Exception e) {
                            MatchBox.getInstance().getLogger().warning("Impossible de re-faire rider l'entity: " + e.getMessage());
                            // Si ça échoue, supprimer l'entity
                            entity.remove();
                            iterator.remove();
                        }
                    }
                }
            }
        }, 20L, 20L); // Toutes les secondes (plus besoin de fréquence élevée)
    }

    /**
     * Vérifie et fix les entities riding après un changement de monde
     */
    private void checkAndFixRidingEntities(Player target) {
        // Vérifier toutes les entities qui devraient rider sur ce target
        for (Map<UUID, TextDisplay> viewerEntities : displayEntities.values()) {
            TextDisplay entity = viewerEntities.get(target.getUniqueId());
            if (entity != null && entity.isValid()) {
                // Si l'entity n'est plus sur le joueur, la remettre
                if (!target.getPassengers().contains(entity)) {
                    try {
                        target.addPassenger(entity);
                    } catch (Exception e) {
                        MatchBox.getInstance().getLogger().warning("Impossible de remettre l'entity riding après changement de monde: " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Nettoyage complet d'un joueur avec gestion du riding
     */
    public void cleanupPlayer(Player player) {
        UUID playerUuid = player.getUniqueId();

        // Nettoyer en tant que viewer (supprimer toutes ses TextDisplay entities)
        Map<UUID, TextDisplay> viewerEntities = displayEntities.remove(playerUuid);
        if (viewerEntities != null) {
            for (Map.Entry<UUID, TextDisplay> entry : viewerEntities.entrySet()) {
                TextDisplay entity = entry.getValue();
                if (entity != null && entity.isValid()) {
                    // Faire descendre l'entity du joueur target
                    Player target = Bukkit.getPlayer(entry.getKey());
                    if (target != null && target.isOnline() && target.getPassengers().contains(entity)) {
                        target.removePassenger(entity);
                    }
                    entity.remove();
                }
            }
        }

        // Nettoyer les configurations
        nameTagConfigs.remove(playerUuid);

        // Nettoyer en tant que target (faire descendre toutes les entities qui ridaient sur lui)
        if (player.isOnline()) {
            List<TextDisplay> ridingEntities = new ArrayList<>();
            for (org.bukkit.entity.Entity passenger : player.getPassengers()) {
                if (passenger instanceof TextDisplay) {
                    TextDisplay textDisplay = (TextDisplay) passenger;
                    // Vérifier si c'est une de nos entities nametag
                    if (textDisplay.getCustomName() != null &&
                            textDisplay.getCustomName().contains("riding_nametag_" + player.getName())) {
                        ridingEntities.add(textDisplay);
                    }
                }
            }

            // Faire descendre et supprimer toutes nos entities
            for (TextDisplay entity : ridingEntities) {
                player.removePassenger(entity);
                entity.remove();
            }
        }

        // Nettoyer les références dans les autres maps
        for (Map<UUID, TextDisplay> otherViewerEntities : displayEntities.values()) {
            TextDisplay entityToRemove = otherViewerEntities.remove(playerUuid);
            if (entityToRemove != null && entityToRemove.isValid()) {
                entityToRemove.remove();
            }
        }

        // Nettoyer les configurations où ce joueur est target
        for (Map<UUID, NameTag> viewerConfigs : nameTagConfigs.values()) {
            viewerConfigs.remove(playerUuid);
        }
    }


    /**
     * Change la couleur d'arrière-plan d'une TextDisplay entity
     */
    public void setDisplaySeeTroughWalls(Player viewer, Player target, boolean SeeThroughWalls) {
        Map<UUID, TextDisplay> viewerEntities = displayEntities.get(viewer.getUniqueId());
        if (viewerEntities == null) return;

        TextDisplay entity = viewerEntities.get(target.getUniqueId());
        if (entity == null || !entity.isValid()) return;

        try {
            if (SeeThroughWalls) {
                entity.setSeeThrough(true);
            } else {
                entity.setSeeThrough(false);
            }

        } catch (Exception e) {
            MatchBox.getInstance().getLogger().warning("Erreur changement See Trough: " + e.getMessage());
        }
    }

    public String getCustomNameTag(Player viewer, Player target) {
        Map<UUID, NameTag> viewerConfigs = nameTagConfigs.get(viewer.getUniqueId());
        if (viewerConfigs == null) return null;

        NameTag config = viewerConfigs.get(target.getUniqueId());
        return config != null ? config.customName : null;
    }

    public boolean isNameTagHidden(Player viewer, Player target) {
        Map<UUID, NameTag> viewerConfigs = nameTagConfigs.get(viewer.getUniqueId());
        if (viewerConfigs == null) return false;

        NameTag config = viewerConfigs.get(target.getUniqueId());
        return config != null && config.hidden;
    }



    public void hidePlayerSkin(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set steve " + player.getPlayer().getName());
        PDM.setBool(player.getPlayer(), Keys.IS_SKIN_HIDDEN, true);
    }

    public void hideAllPlayerSkin() {
        for (UUID uuid : gameplayManager.players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            hidePlayerSkin(player);
        }
    }

    public void showPlayerSkin(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin clear " + player.getPlayer().getName());
        PDM.setBool(player.getPlayer(), Keys.IS_SKIN_HIDDEN, false);
    }

    public void showAllPlayersSkin() {
        for (UUID uuid : gameplayManager.players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            showPlayerSkin(player);
        }
    }


    public void makePlayerGlow(Player viewer, Player target, ChatColor color){
        try {
            glowingEntities.setGlowing(target.getPlayer(), viewer.getPlayer(), color);
        } catch (ReflectiveOperationException e) {
            MatchBox.getInstance().getLogger().info("§cThere was a problem with GlowingEntities (setGlowing)");
        }
    }
    public void resetPlayerGlow(Player viewer, Player target){
        try {
            glowingEntities.unsetGlowing(target.getPlayer(), viewer.getPlayer());
        } catch (ReflectiveOperationException e) {
            MatchBox.getInstance().getLogger().info("§cThere was a problem with GlowingEntities (unsetGlowing)");
        }
    }



}
