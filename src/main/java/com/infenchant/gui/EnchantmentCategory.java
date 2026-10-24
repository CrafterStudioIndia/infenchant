package com.infenchant.gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.Locale;
import java.util.Set;

public enum EnchantmentCategory {
    ALL("All Enchantments", Material.NETHER_STAR, null),
    WEAPONS("Weapons & Combat", Material.DIAMOND_SWORD, Set.of(
            "sharpness", "smite", "bane_of_arthropods", "knockback", "fire_aspect",
            "looting", "sweeping_edge", "breach", "density", "wind_burst"
    )),
    ARMOR("Armor & Defense", Material.NETHERITE_CHESTPLATE, Set.of(
            "protection", "fire_protection", "feather_falling", "blast_protection",
            "projectile_protection", "respiration", "aqua_affinity", "thorns",
            "depth_strider", "frost_walker", "soul_speed", "swift_sneak"
    )),
    TOOLS("Tools & Mining", Material.NETHERITE_PICKAXE, Set.of(
            "efficiency", "silk_touch", "fortune"
    )),
    RANGED("Bows & Crossbows", Material.BOW, Set.of(
            "power", "punch", "flame", "infinity", "multishot", "quick_charge", "piercing"
    )),
    TRIDENT_MACE("Trident & Mace", Material.TRIDENT, Set.of(
            "loyalty", "impaling", "riptide", "channeling", "density", "breach", "wind_burst"
    )),
    MISC("Utility & Curses", Material.ENCHANTED_BOOK, Set.of(
            "unbreaking", "mending", "luck_of_the_sea", "lure", "curse_of_binding", "curse_of_vanishing"
    ));

    private final String displayName;
    private final Material icon;
    private final Set<String> keys;

    EnchantmentCategory(String displayName, Material icon, Set<String> keys) {
        this.displayName = displayName;
        this.icon = icon;
        this.keys = keys;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public boolean matches(Enchantment enchantment) {
        if (this == ALL) {
            return true;
        }
        String key = enchantment.getKey().getKey().toLowerCase(Locale.ROOT);
        if (keys != null && keys.contains(key)) {
            return true;
        }
        // If it's MISC, also include any enchantment that doesn't fit in other categories
        if (this == MISC) {
            for (EnchantmentCategory cat : values()) {
                if (cat != ALL && cat != MISC && cat.keys != null && cat.keys.contains(key)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public EnchantmentCategory next() {
        int nextIndex = (this.ordinal() + 1) % values().length;
        return values()[nextIndex];
    }

    public EnchantmentCategory previous() {
        int prevIndex = (this.ordinal() - 1 + values().length) % values().length;
        return values()[prevIndex];
    }
}

