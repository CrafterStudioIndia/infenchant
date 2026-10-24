package com.infenchant.gui;

import com.infenchant.util.EnchantmentUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EnchantmentGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof EnchantmentGuiHolder holder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir() || !clicked.hasItemMeta()) {
            return;
        }

        PersistentDataContainer pdc = clicked.getItemMeta().getPersistentDataContainer();

        if (pdc.has(EnchantmentGui.ENCHANT_KEY, PersistentDataType.STRING)) {
            String keyStr = pdc.get(EnchantmentGui.ENCHANT_KEY, PersistentDataType.STRING);
            Enchantment ench = EnchantmentUtil.findEnchantment(keyStr);
            if (ench == null) return;

            ItemStack held = player.getInventory().getItemInMainHand();
            if (held.getType().isAir()) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Hold an item in your main hand first.</red>"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

            if (event.isRightClick()) {
                if (EnchantmentUtil.removeEnchantment(held, ench)) {
                    player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1f, 1f);
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(
                            "<yellow>Removed <light_purple>" + EnchantmentUtil.formatName(ench) + "</light_purple></yellow>"
                    ));
                    holder.refresh();
                }
            } else {
                EnchantmentUtil.applyEnchantment(held, ench, holder.getLevel());
                player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1.2f);
                player.sendActionBar(MiniMessage.miniMessage().deserialize(
                        "<green>Applied <light_purple>" + EnchantmentUtil.formatName(ench) + " " + holder.getLevel() + "</light_purple></green>"
                ));
                holder.refresh();
            }
            return;
        }

        if (pdc.has(EnchantmentGui.ACTION_KEY, PersistentDataType.STRING)) {
            String action = pdc.get(EnchantmentGui.ACTION_KEY, PersistentDataType.STRING);
            if (action == null) return;

            ItemStack held = player.getInventory().getItemInMainHand();

            switch (action) {
                case EnchantmentGui.ACTION_CATEGORY_SWITCH -> {
                    holder.setCategory(holder.getCategory().next());
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1f);
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_PAGE_PREV -> {
                    holder.setPage(holder.getPage() - 1);
                    player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 0.8f, 1f);
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_PAGE_NEXT -> {
                    holder.setPage(holder.getPage() + 1);
                    player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 0.8f, 1f);
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_APPLY_ALL -> {
                    if (held.getType().isAir()) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Hold an item in your main hand first.</red>"));
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        return;
                    }
                    int count = EnchantmentUtil.applyAllEnchantments(held, holder.getLevel());
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.5f);
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(
                            "<green>Infused item with <light_purple>ALL " + count + " enchantments</light_purple> at level " + holder.getLevel() + "!</green>"
                    ));
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_CLEAR_ALL -> {
                    if (held.getType().isAir()) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Hold an item in your main hand first.</red>"));
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        return;
                    }
                    int count = EnchantmentUtil.clearAllEnchantments(held);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 0.6f, 1f);
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(
                            "<yellow>Cleared <gold>" + count + "</gold> enchantments.</yellow>"
                    ));
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_LEVEL_SUB_10 -> {
                    holder.adjustLevel(-10);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6f, 1.5f);
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_LEVEL_SUB_1 -> {
                    holder.adjustLevel(-1);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6f, 1.6f);
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_LEVEL_ADD_1 -> {
                    holder.adjustLevel(1);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6f, 1.8f);
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_LEVEL_ADD_10 -> {
                    holder.adjustLevel(10);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6f, 2f);
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_LEVEL_SET_1 -> {
                    holder.setLevel(1);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6f, 1.7f);
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_LEVEL_SET_50 -> {
                    holder.setLevel(50);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6f, 1.7f);
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_LEVEL_SET_100 -> {
                    holder.setLevel(100);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6f, 1.7f);
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_LEVEL_SET_225 -> {
                    holder.setLevel(225);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.7f, 1.9f);
                    holder.refresh();
                }
                case EnchantmentGui.ACTION_LEVEL_SET_255 -> {
                    holder.setLevel(255);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.7f, 2f);
                    holder.refresh();
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof EnchantmentGuiHolder) {
            event.setCancelled(true);
        }
    }
}
