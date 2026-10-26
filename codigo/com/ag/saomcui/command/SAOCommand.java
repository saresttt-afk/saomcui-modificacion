/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.EntityArgument
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleContainer
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ChestMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.item.ItemStack
 */
package com.ag.saomcui.command;

import com.ag.saomcui.config.SAOConfig;
import com.ag.saomcui.inventory.SAOInventory;
import com.ag.saomcui.network.ConfigSyncPacket;
import com.ag.saomcui.network.SAOPacketHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class SAOCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"saocraft").requires(source -> source.m_6761_(2))).then(Commands.m_82129_((String)"enabled", (ArgumentType)BoolArgumentType.bool()).executes(context -> {
            boolean enabled;
            SAOConfig.enableCrafting = enabled = BoolArgumentType.getBool((CommandContext)context, (String)"enabled");
            SAOConfig.save();
            ConfigSyncPacket pkt = new ConfigSyncPacket(enabled);
            ((CommandSourceStack)context.getSource()).m_81377_().m_6846_().m_11314_().forEach(player -> SAOPacketHandler.sendToPlayer(pkt, player));
            ((CommandSourceStack)context.getSource()).m_288197_(() -> Component.m_237113_((String)("\u00a7aSAO Crafting menu is now " + (enabled ? "enabled" : "disabled") + " globally.")), true);
            return 1;
        })));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"sao").requires(source -> source.m_6761_(2))).then(Commands.m_82127_((String)"inv").then(Commands.m_82129_((String)"target", (ArgumentType)EntityArgument.m_91466_()).executes(context -> {
            final ServerPlayer target = EntityArgument.m_91474_((CommandContext)context, (String)"target");
            ServerPlayer source = ((CommandSourceStack)context.getSource()).m_81375_();
            target.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
                final SimpleContainer container = new SimpleContainer(27);
                for (int i = 0; i < 27; ++i) {
                    SAOInventory.SAOStack saoStack = inv.getStackInSlot(i);
                    if (saoStack == null || saoStack.item.m_41619_() || saoStack.count <= 0) continue;
                    ItemStack copy = saoStack.item.m_41777_();
                    copy.m_41764_(Math.min(saoStack.count, copy.m_41741_()));
                    container.m_6836_(i, copy);
                }
                source.m_5893_(new MenuProvider(){

                    public Component m_5446_() {
                        return Component.m_237113_((String)("SAO Inv: " + target.m_7755_().getString()));
                    }

                    public AbstractContainerMenu m_7208_(int id, Inventory playerInv, Player player) {
                        return new ChestMenu(MenuType.f_39959_, id, playerInv, (Container)container, 3);
                    }
                });
            });
            return 1;
        }))));
    }
}

