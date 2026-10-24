package com.infenchant;

import com.infenchant.command.InfEnchantCommand;
import com.infenchant.gui.EnchantmentGuiListener;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class InfEnchant extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EnchantmentGuiListener(), this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                    new InfEnchantCommand(this).build(),
                    "InfEnchant operator enchantment switcher",
                    List.of("ie", "infiniteenchant")
            );
        });

        getComponentLogger().info(MiniMessage.miniMessage().deserialize(
                "<gradient:#a855f7:#ec4899><bold>InfEnchant</bold></gradient> <green>loaded for Paper 1.21.11.</green>"
        ));
    }

    @Override
    public void onDisable() {
        getComponentLogger().info(MiniMessage.miniMessage().deserialize(
                "<red>InfEnchant disabled.</red>"
        ));
    }
}
