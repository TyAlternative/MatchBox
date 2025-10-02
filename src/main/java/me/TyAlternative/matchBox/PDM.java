package me.TyAlternative.matchBox;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PDM {

    public static void resetEverything(Player player) {
        for (NamespacedKey key : player.getPersistentDataContainer().getKeys()) {
            player.getPersistentDataContainer().remove(key);
        }
    }

    public static void removeKey(Player player,NamespacedKey key) {
        player.getPersistentDataContainer().remove(key);
    }


    public static void set(Player player, NamespacedKey key, PersistentDataType persistentDataType, Object object) {
        player.getPersistentDataContainer().set(key, persistentDataType, object);
    }
    public static void setBool(Player player, NamespacedKey key, boolean bool) {
        player.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, bool);
    }
    public static void setString(Player player, NamespacedKey key, String string) {
        player.getPersistentDataContainer().set(key, PersistentDataType.STRING, string);
    }
    public static void setInt(Player player, NamespacedKey key, int integer) {
        player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, integer);
    }
    public static void setFloat(Player player, NamespacedKey key, float decimal) {
        player.getPersistentDataContainer().set(key, PersistentDataType.FLOAT, decimal);
    }
    public static void setDouble(Player player, NamespacedKey key, double decimal) {
        player.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, decimal);
    }


    public static Object get(Player player, NamespacedKey key, PersistentDataType persistentDataType) {
        return player.getPersistentDataContainer().get(key, persistentDataType);
    }
    public static boolean getBool(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN);
    }
    public static String getString(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }
    public static int getInt(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
    }
    public static float getFloat(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().get(key, PersistentDataType.FLOAT);
    }
    public static double getDouble(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().get(key, PersistentDataType.DOUBLE);
    }


    public static Object getOrDefault(Player player, NamespacedKey key, PersistentDataType persistentDataType, Object defaultValue) {
        return player.getPersistentDataContainer().getOrDefault(key, persistentDataType, defaultValue);
    }
    public static boolean getBoolOrDefault(Player player, NamespacedKey key, boolean defaultValue) {
        return player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BOOLEAN, defaultValue);
    }
    public static String getStringOrDefault(Player player, NamespacedKey key, String defaultValue) {
        return player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.STRING, defaultValue);
    }
    public static int getIntOrDefault(Player player, NamespacedKey key, int defaultValue) {
        return player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, defaultValue);
    }
    public static float getFloatOrDefault(Player player, NamespacedKey key, float defaultValue) {
        return player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.FLOAT, defaultValue);
    }
    public static double getDoubleOrDefault(Player player, NamespacedKey key, double defaultValue) {
        return player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.DOUBLE, defaultValue);
    }


    public static boolean has(Player player, NamespacedKey key, PersistentDataType persistentDataType) {
        return player.getPersistentDataContainer().has(key, persistentDataType);
    }
    public static boolean hasBool(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN);
    }
    public static boolean hasString(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }
    public static boolean hasInt(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
    }
    public static boolean hasFloat(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().has(key, PersistentDataType.FLOAT);
    }
    public static boolean hasDouble(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().has(key, PersistentDataType.DOUBLE);
    }




    public static void resetEverything(ItemStack item) {
        for (NamespacedKey key : item.getItemMeta().getPersistentDataContainer().getKeys()) {
            ItemMeta meta = item.getItemMeta();
            meta .getPersistentDataContainer().remove(key);
            item.setItemMeta(meta);
        }
    }

    public static void removeKey(ItemStack item,NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();
        meta .getPersistentDataContainer().remove(key);
        item.setItemMeta(meta);
    }


    public static void set(ItemStack item, NamespacedKey key, PersistentDataType persistentDataType, Object object) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, persistentDataType, object);
        item.setItemMeta(meta);
    }
    public static void setBool(ItemStack item, NamespacedKey key, boolean bool) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, bool);
        item.setItemMeta(meta);
    }
    public static void setString(ItemStack item, NamespacedKey key, String string) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, string);
        item.setItemMeta(meta);
    }
    public static void setInt(ItemStack item, NamespacedKey key, int integer) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, integer);
        item.setItemMeta(meta);
    }
    public static void setFloat(ItemStack item, NamespacedKey key, float decimal) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.FLOAT, decimal);
        item.setItemMeta(meta);
    }
    public static void setDouble(ItemStack item, NamespacedKey key, double decimal) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, decimal);
        item.setItemMeta(meta);
    }


    public static Object get(ItemStack item, NamespacedKey key, PersistentDataType persistentDataType) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(key, persistentDataType);
    }
    public static boolean getBool(ItemStack item, NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN);
    }
    public static String getString(ItemStack item, NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }
    public static int getInt(ItemStack item, NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
    }
    public static float getFloat(ItemStack item, NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(key, PersistentDataType.FLOAT);
    }
    public static double getDouble(ItemStack item, NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(key, PersistentDataType.DOUBLE);
    }


    public static Object getOrDefault(ItemStack item, NamespacedKey key, PersistentDataType persistentDataType, Object defaultValue) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().getOrDefault(key, persistentDataType, defaultValue);
    }
    public static boolean getBoolOrDefault(ItemStack item, NamespacedKey key, boolean defaultValue) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BOOLEAN, defaultValue);
    }
    public static String getStringOrDefault(ItemStack item, NamespacedKey key, String defaultValue) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.STRING, defaultValue);
    }
    public static int getIntOrDefault(ItemStack item, NamespacedKey key, int defaultValue) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, defaultValue);
    }
    public static float getFloatOrDefault(ItemStack item, NamespacedKey key, float defaultValue) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.FLOAT, defaultValue);
    }
    public static double getDoubleOrDefault(ItemStack item, NamespacedKey key, double defaultValue) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.DOUBLE, defaultValue);
    }


    public static boolean has(ItemStack item, NamespacedKey key, PersistentDataType persistentDataType) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(key, persistentDataType);
    }
    public static boolean hasBool(ItemStack item, NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN);
    }
    public static boolean hasString(ItemStack item, NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }
    public static boolean hasInt(ItemStack item, NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
    }
    public static boolean hasFloat(ItemStack item, NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(key, PersistentDataType.FLOAT);
    }
    public static boolean hasDouble(ItemStack item, NamespacedKey key) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(key, PersistentDataType.DOUBLE);
    }


}