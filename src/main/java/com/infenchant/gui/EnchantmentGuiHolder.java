package com.infenchant.gui;

import com.infenchant.InfEnchant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class EnchantmentGuiHolder implements InventoryHolder {

    private final InfEnchant plugin;
    private final Player player;
    private int level;
    private EnchantmentCategory category;
    private int page;
    private Inventory inventory;

    public EnchantmentGuiHolder(InfEnchant plugin, Player player, int initialLevel) {
        this.plugin = plugin;
        this.player = player;
        this.level = Math.max(1, Math.min(255, initialLevel));
        this.category = EnchantmentCategory.ALL;
        this.page = 0;
    }

    public InfEnchant getPlugin() {
        return plugin;
    }

    public Player getPlayer() {
        return player;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(255, level));
    }

    public void adjustLevel(int delta) {
        setLevel(this.level + delta);
    }

    public EnchantmentCategory getCategory() {
        return category;
    }

    public void setCategory(EnchantmentCategory category) {
        this.category = category;
        this.page = 0; // Reset page on category switch
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(0, page);
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void refresh() {
        if (inventory != null) {
            EnchantmentGui.populate(this);
        }
    }
}

