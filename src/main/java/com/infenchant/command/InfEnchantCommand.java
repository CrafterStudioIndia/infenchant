package com.infenchant.command;

import com.infenchant.InfEnchant;
import com.infenchant.gui.EnchantmentGui;
import com.infenchant.util.EnchantmentUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class InfEnchantCommand {

    private static final List<String> LEVEL_SUGGESTIONS = List.of("225", "255", "100", "50", "10", "1");
    private static final List<String> KEYWORDS = List.of("gui", "all", "clear", "225", "255");

    private final InfEnchant plugin;

    public InfEnchantCommand(InfEnchant plugin) {
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("infenchant")
                .requires(source -> {
                    CommandSender sender = source.getSender();
                    return sender.isOp() || sender.hasPermission("infenchant.admin");
                })
                .executes(this::openDefaultGui)
                .then(Commands.literal("clear")
                        .executes(this::clearEnchantments))
                .then(Commands.literal("all")
                        .executes(ctx -> applyAll(ctx, 225))
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 255))
                                .suggests((ctx, builder) -> {
                                    LEVEL_SUGGESTIONS.forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> applyAll(ctx, IntegerArgumentType.getInteger(ctx, "level")))))
                .then(Commands.literal("gui")
                        .executes(this::openDefaultGui)
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 255))
                                .suggests((ctx, builder) -> {
                                    LEVEL_SUGGESTIONS.forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                                        ctx.getSource().getSender().sendMessage(MiniMessage.miniMessage().deserialize("<red>Only in-game players can open the GUI.</red>"));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    EnchantmentGui.open(plugin, player, IntegerArgumentType.getInteger(ctx, "level"));
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.argument("enchantment", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            String token = builder.getRemaining().toLowerCase();
                            for (String kw : KEYWORDS) {
                                if (kw.startsWith(token)) builder.suggest(kw);
                            }
                            for (Enchantment e : EnchantmentUtil.getAllEnchantments()) {
                                String key = e.getKey().getKey();
                                if (key.startsWith(token) || key.replace("_", "").startsWith(token)) {
                                    builder.suggest(key);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(this::handleSingleArg)
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 255))
                                .suggests((ctx, builder) -> {
                                    LEVEL_SUGGESTIONS.forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "enchantment");
                                    int level = IntegerArgumentType.getInteger(ctx, "level");
                                    return applySingleEnchantment(ctx, name, level);
                                })))
                .build();
    }

    private int openDefaultGui(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getSender() instanceof Player player)) {
            ctx.getSource().getSender().sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can open this menu.</red>"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (inHand.getType().isAir()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Hold an item in your main hand first.</red>"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return Command.SINGLE_SUCCESS;
        }

        EnchantmentGui.open(plugin, player, 225);
        return Command.SINGLE_SUCCESS;
    }

    private int handleSingleArg(CommandContext<CommandSourceStack> ctx) {
        String input = StringArgumentType.getString(ctx, "enchantment");

        try {
            int level = Integer.parseInt(input);
            if (ctx.getSource().getSender() instanceof Player player) {
                EnchantmentGui.open(plugin, player, level);
                return Command.SINGLE_SUCCESS;
            }
        } catch (NumberFormatException ignored) {
        }

        return applySingleEnchantment(ctx, input, 225);
    }

    private int applySingleEnchantment(CommandContext<CommandSourceStack> ctx, String name, int level) {
        if (!(ctx.getSource().getSender() instanceof Player player)) {
            ctx.getSource().getSender().sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can enchant items.</red>"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Hold an item in your main hand first.</red>"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return Command.SINGLE_SUCCESS;
        }

        Enchantment ench = EnchantmentUtil.findEnchantment(name);
        if (ench == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Unknown enchantment: <yellow>" + name + "</yellow></red>"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return Command.SINGLE_SUCCESS;
        }

        EnchantmentUtil.applyEnchantment(item, ench, level);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1.2f);
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Applied <light_purple>" + EnchantmentUtil.formatName(ench) + " " + level + "</light_purple> to your held item.</green>"
        ));
        return Command.SINGLE_SUCCESS;
    }

    private int applyAll(CommandContext<CommandSourceStack> ctx, int level) {
        if (!(ctx.getSource().getSender() instanceof Player player)) {
            ctx.getSource().getSender().sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can use this command.</red>"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Hold an item in your main hand first.</red>"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return Command.SINGLE_SUCCESS;
        }

        int count = EnchantmentUtil.applyAllEnchantments(item, level);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Infused item with <light_purple>" + count + " enchantments</light_purple> at level <gold>" + level + "</gold>!</green>"
        ));
        return Command.SINGLE_SUCCESS;
    }

    private int clearEnchantments(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getSender() instanceof Player player)) {
            ctx.getSource().getSender().sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can use this command.</red>"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Hold an item in your main hand first.</red>"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return Command.SINGLE_SUCCESS;
        }

        int count = EnchantmentUtil.clearAllEnchantments(item);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1f, 1f);
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<yellow>Removed <gold>" + count + "</gold> enchantments from your item.</yellow>"
        ));
        return Command.SINGLE_SUCCESS;
    }
}

