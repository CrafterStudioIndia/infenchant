package com.infenchant.gui;

import com.infenchant.InfEnchant;
import com.infenchant.util.EnchantmentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class EnchantmentGui {

    public static final NamespacedKey ENCHANT_KEY = new NamespacedKey("infenchant", "enchant_key");
    public static final NamespacedKey ACTION_KEY = new NamespacedKey("infenchant", "action_key");

    public static final String ACTION_CATEGORY_SWITCH = "category_switch";
    public static final String ACTION_PAGE_PREV = "page_prev";
    public static final String ACTION_PAGE_NEXT = "page_next";
    public static final String ACTION_APPLY_ALL = "apply_all";
    public static final String ACTION_CLEAR_ALL = "clear_all";
    public static final String ACTION_LEVEL_SUB_10 = "level_sub_10";
    public static final String ACTION_LEVEL_SUB_1 = "level_sub_1";
    public static final String ACTION_LEVEL_ADD_1 = "level_add_1";
    public static final String ACTION_LEVEL_ADD_10 = "level_add_10";
    public static final String ACTION_LEVEL_SET_1 = "level_set_1";
    public static final String ACTION_LEVEL_SET_50 = "level_set_50";
    public static final String ACTION_LEVEL_SET_100 = "level_set_100";
    public static final String ACTION_LEVEL_SET_225 = "level_set_225";
    public static final String ACTION_LEVEL_SET_255 = "level_set_255";

    private static final int PAGE_SIZE = 36;

    private EnchantmentGui() {
    }

    public static void open(InfEnchant plugin, Player player, int initialLevel) {
        EnchantmentGuiHolder holder = new EnchantmentGuiHolder(plugin, player, initialLevel);
        Component title = MiniMessage.miniMessage().deserialize(
                "<gradient:#a855f7:#ec4899><bold>InfEnchant</bold></gradient> <dark_gray>•</dark_gray> <gray>Enchantment Switcher</gray>"
        );
        Inventory inv = Bukkit.createInventory(holder, 54, title);
        holder.setInventory(inv);
        populate(holder);
        player.openInventory(inv);
    }

    public static void populate(EnchantmentGuiHolder holder) {
        Inventory inv = holder.getInventory();
        inv.clear();

        Player player = holder.getPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();
        int targetLevel = holder.getLevel();
        EnchantmentCategory category = holder.getCategory();

        List<Enchantment> enchantments = EnchantmentUtil.getAllEnchantments().stream()
                .filter(category::matches)
                .toList();

        int totalPages = Math.max(1, (int) Math.ceil((double) enchantments.size() / PAGE_SIZE));
        if (holder.getPage() >= totalPages) {
            holder.setPage(totalPages - 1);
        }
        int page = holder.getPage();

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, enchantments.size());

        for (int i = 0; i < PAGE_SIZE; i++) {
            int idx = start + i;
            if (idx < end) {
                inv.setItem(i, enchantmentCard(enchantments.get(idx), held, targetLevel));
            }
        }

        inv.setItem(36, categoryButton(category));
        inv.setItem(37, page > 0
                ? actionButton(Material.ARROW, "<yellow><bold>⬅ Previous Page</bold></yellow>", List.of("<gray>Page " + page + "/" + totalPages + "</gray>"), ACTION_PAGE_PREV)
                : divider("<dark_gray>No Previous Page</dark_gray>"));

        inv.setItem(38, divider(null));
        inv.setItem(39, actionButton(
                Material.NETHER_STAR,
                "<light_purple><bold>Apply All Enchantments</bold></light_purple>",
                List.of("<gray>Infuse item with all enchantments at level <white>" + targetLevel + "</white>.</gray>", "", "<yellow>Click to apply</yellow>"),
                ACTION_APPLY_ALL
        ));

        inv.setItem(40, heldItemPreview(held));

        inv.setItem(41, actionButton(
                Material.GRINDSTONE,
                "<red><bold>Clear All Enchantments</bold></red>",
                List.of("<gray>Strips all enchantments from your held item.</gray>", "", "<red>Click to clear</red>"),
                ACTION_CLEAR_ALL
        ));
        inv.setItem(42, divider(null));
        inv.setItem(43, page < totalPages - 1
                ? actionButton(Material.ARROW, "<yellow><bold>Next Page ➡</bold></yellow>", List.of("<gray>Page " + (page + 2) + "/" + totalPages + "</gray>"), ACTION_PAGE_NEXT)
                : divider("<dark_gray>No Next Page</dark_gray>"));
        inv.setItem(44, divider(null));

        inv.setItem(45, actionButton(Material.RED_CONCRETE, "<red><bold>-10 Level</bold></red>", List.of("<gray>Decrease level by 10</gray>"), ACTION_LEVEL_SUB_10));
        inv.setItem(46, actionButton(Material.ORANGE_CONCRETE, "<gold><bold>-1 Level</bold></gold>", List.of("<gray>Decrease level by 1</gray>"), ACTION_LEVEL_SUB_1));

        inv.setItem(47, presetButton(1, targetLevel, Material.LIME_DYE, ACTION_LEVEL_SET_1));
        inv.setItem(48, presetButton(50, targetLevel, Material.LIGHT_BLUE_DYE, ACTION_LEVEL_SET_50));
        inv.setItem(49, presetButton(100, targetLevel, Material.CYAN_DYE, ACTION_LEVEL_SET_100));
        inv.setItem(50, presetButton(225, targetLevel, Material.PURPLE_DYE, ACTION_LEVEL_SET_225));
        inv.setItem(51, presetButton(255, targetLevel, Material.MAGENTA_DYE, ACTION_LEVEL_SET_255));

        inv.setItem(52, actionButton(Material.LIME_CONCRETE, "<green><bold>+1 Level</bold></green>", List.of("<gray>Increase level by 1</gray>"), ACTION_LEVEL_ADD_1));
        inv.setItem(53, actionButton(Material.EMERALD_BLOCK, "<dark_green><bold>+10 Level</bold></dark_green>", List.of("<gray>Increase level by 10</gray>"), ACTION_LEVEL_ADD_10));
    }

    private static ItemStack enchantmentCard(Enchantment ench, ItemStack held, int level) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();

        String name = EnchantmentUtil.formatName(ench);
        int currentLevel = EnchantmentUtil.getEnchantmentLevel(held, ench);

        meta.displayName(MiniMessage.miniMessage().deserialize("<gradient:#38bdf8:#818cf8><bold>" + name + "</bold></gradient>"));

        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize("<gray>ID: <yellow>" + ench.getKey().getKey() + "</yellow></gray>"));
        lore.add(MiniMessage.miniMessage().deserialize("<gray>Vanilla Cap: <green>" + ench.getMaxLevel() + "</green></gray>"));
        lore.add(MiniMessage.miniMessage().deserialize("<gray>Target Level: <light_purple><bold>" + level + "</bold></light_purple></gray>"));

        if (currentLevel > 0) {
            lore.add(MiniMessage.miniMessage().deserialize("<gray>On Item: <green><bold>Active (" + EnchantmentUtil.formatLevel(currentLevel) + ")</bold></green></gray>"));
        } else {
            lore.add(MiniMessage.miniMessage().deserialize("<gray>On Item: <dark_gray>Not present</dark_gray></gray>"));
        }

        lore.add(Component.empty());
        lore.add(MiniMessage.miniMessage().deserialize("<yellow>▶ Left-Click</yellow> <gray>to apply level " + level + "</gray>"));
        if (currentLevel > 0) {
            lore.add(MiniMessage.miniMessage().deserialize("<red>▶ Right-Click</red> <gray>to remove</gray>"));
        }

        meta.lore(lore);
        meta.getPersistentDataContainer().set(ENCHANT_KEY, PersistentDataType.STRING, ench.getKey().toString());
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack categoryButton(EnchantmentCategory current) {
        ItemStack item = new ItemStack(current.getIcon());
        ItemMeta meta = item.getItemMeta();

        meta.displayName(MiniMessage.miniMessage().deserialize(
                "<gold><bold>Category:</bold> <yellow>" + current.getDisplayName() + "</yellow></gold>"
        ));

        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize("<gray>Filter enchantments:</gray>"));
        for (EnchantmentCategory cat : EnchantmentCategory.values()) {
            if (cat == current) {
                lore.add(MiniMessage.miniMessage().deserialize("<green> ▶ <bold>" + cat.getDisplayName() + "</bold></green>"));
            } else {
                lore.add(MiniMessage.miniMessage().deserialize("<dark_gray>   " + cat.getDisplayName() + "</dark_gray>"));
            }
        }
        lore.add(Component.empty());
        lore.add(MiniMessage.miniMessage().deserialize("<yellow>Click to switch category</yellow>"));

        meta.lore(lore);
        meta.getPersistentDataContainer().set(ACTION_KEY, PersistentDataType.STRING, ACTION_CATEGORY_SWITCH);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack presetButton(int preset, int active, Material material, String action) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        boolean isCurrent = preset == active;
        String label = (isCurrent ? "<green><bold>" : "<white>") + "Level " + preset +
                (preset == 225 ? " (Vanilla Max)" : (preset == 255 ? " (Max)" : "")) +
                (isCurrent ? "</bold></green>" : "</white>");

        meta.displayName(MiniMessage.miniMessage().deserialize(label));

        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize(isCurrent
                ? "<green>✔ Active selection</green>"
                : "<gray>Click to select level <white>" + preset + "</white></gray>"));

        meta.lore(lore);
        meta.getPersistentDataContainer().set(ACTION_KEY, PersistentDataType.STRING, action);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack heldItemPreview(ItemStack held) {
        if (held == null || held.getType().isAir()) {
            ItemStack barrier = new ItemStack(Material.BARRIER);
            ItemMeta meta = barrier.getItemMeta();
            meta.displayName(MiniMessage.miniMessage().deserialize("<red><bold>No Item in Hand</bold></red>"));
            meta.lore(List.of(MiniMessage.miniMessage().deserialize("<gray>Hold an item in your main hand.</gray>")));
            barrier.setItemMeta(meta);
            return barrier;
        }

        ItemStack copy = held.clone();
        ItemMeta meta = copy.getItemMeta();
        List<Component> existing = meta.lore();
        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize("<gradient:#f59e0b:#ef4444><bold>-- Held Item Preview --</bold></gradient>"));
        if (existing != null) {
            lore.addAll(existing);
        }
        lore.add(Component.empty());
        lore.add(MiniMessage.miniMessage().deserialize("<gray>Enchantments will apply to this item.</gray>"));
        meta.lore(lore);
        copy.setItemMeta(meta);
        return copy;
    }

    private static ItemStack actionButton(Material material, String name, List<String> loreStrings, String action) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize(name));

        List<Component> lore = new ArrayList<>();
        for (String s : loreStrings) {
            lore.add(MiniMessage.miniMessage().deserialize(s));
        }
        meta.lore(lore);
        meta.getPersistentDataContainer().set(ACTION_KEY, PersistentDataType.STRING, action);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack divider(String title) {
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        meta.displayName(title != null ? MiniMessage.miniMessage().deserialize(title) : Component.empty());
        pane.setItemMeta(meta);
        return pane;
    }
}
