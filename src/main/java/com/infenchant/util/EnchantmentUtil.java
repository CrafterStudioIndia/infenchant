package com.infenchant.util;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public final class EnchantmentUtil {

    private EnchantmentUtil() {
    }

    public static List<Enchantment> getAllEnchantments() {
        var registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        List<Enchantment> list = new ArrayList<>();
        for (Enchantment e : registry) {
            list.add(e);
        }
        list.sort(Comparator.comparing(e -> e.getKey().getKey()));
        return Collections.unmodifiableList(list);
    }

    public static Enchantment findEnchantment(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        String query = input.trim().toLowerCase(Locale.ROOT);
        var registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

        NamespacedKey key = query.contains(":")
                ? NamespacedKey.fromString(query)
                : NamespacedKey.minecraft(query);

        if (key != null) {
            Enchantment found = registry.get(key);
            if (found != null) return found;
        }

        String stripped = query.replace("_", "").replace("-", "");
        for (Enchantment e : registry) {
            String k = e.getKey().getKey().toLowerCase(Locale.ROOT);
            if (k.equalsIgnoreCase(query) || k.replace("_", "").equalsIgnoreCase(stripped)) {
                return e;
            }
        }

        return null;
    }

    public static boolean applyEnchantment(ItemStack item, Enchantment enchantment, int level) {
        if (item == null || item.getType().isAir() || enchantment == null) {
            return false;
        }

        int clamped = Math.clamp(level, 1, 255);
        ItemMeta meta = item.getItemMeta();

        if (meta instanceof EnchantmentStorageMeta storage) {
            storage.addStoredEnchant(enchantment, clamped, true);
            item.setItemMeta(storage);
            return true;
        }

        item.addUnsafeEnchantment(enchantment, clamped);
        return true;
    }

    public static boolean removeEnchantment(ItemStack item, Enchantment enchantment) {
        if (item == null || item.getType().isAir() || enchantment == null) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof EnchantmentStorageMeta storage) {
            boolean removed = storage.removeStoredEnchant(enchantment);
            item.setItemMeta(storage);
            return removed;
        }

        return item.removeEnchantment(enchantment) > 0;
    }

    public static int getEnchantmentLevel(ItemStack item, Enchantment enchantment) {
        if (item == null || item.getType().isAir() || enchantment == null) {
            return 0;
        }

        if (item.getItemMeta() instanceof EnchantmentStorageMeta storage) {
            return storage.getStoredEnchantLevel(enchantment);
        }

        return item.getEnchantmentLevel(enchantment);
    }

    public static int clearAllEnchantments(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof EnchantmentStorageMeta storage) {
            int count = storage.getStoredEnchants().size();
            for (Enchantment e : new ArrayList<>(storage.getStoredEnchants().keySet())) {
                storage.removeStoredEnchant(e);
            }
            item.setItemMeta(storage);
            return count;
        }

        int count = item.getEnchantments().size();
        for (Enchantment e : new ArrayList<>(item.getEnchantments().keySet())) {
            item.removeEnchantment(e);
        }
        return count;
    }

    public static int applyAllEnchantments(ItemStack item, int level) {
        if (item == null || item.getType().isAir()) {
            return 0;
        }

        int count = 0;
        for (Enchantment e : getAllEnchantments()) {
            applyEnchantment(item, e, level);
            count++;
        }
        return count;
    }

    public static String formatName(Enchantment enchantment) {
        if (enchantment == null) return "Unknown";
        String[] words = enchantment.getKey().getKey().split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                sb.append(Character.toUpperCase(words[i].charAt(0)))
                  .append(words[i].substring(1).toLowerCase(Locale.ROOT));
                if (i < words.length - 1) sb.append(' ');
            }
        }
        return sb.toString();
    }

    public static String formatLevel(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(level);
        };
    }
}
