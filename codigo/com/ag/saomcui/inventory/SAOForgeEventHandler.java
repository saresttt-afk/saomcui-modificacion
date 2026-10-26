/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.common.capabilities.ICapabilityProvider
 *  net.minecraftforge.event.AttachCapabilitiesEvent
 *  net.minecraftforge.event.entity.player.EntityItemPickupEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$Clone
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerRespawnEvent
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.ag.saomcui.inventory;

import com.ag.saomcui.inventory.SAOInventory;
import com.ag.saomcui.inventory.SAOInventoryProvider;
import com.ag.saomcui.network.SAOInventorySyncPacket;
import com.ag.saomcui.network.SAOPacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="saomcui", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class SAOForgeEventHandler {
    public static final ResourceLocation INVENTORY_CAP = new ResourceLocation("saomcui", "sao_inventory");

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player && !((Entity)event.getObject()).getCapability(SAOInventory.CAPABILITY).isPresent()) {
            event.addCapability(INVENTORY_CAP, (ICapabilityProvider)new SAOInventoryProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        oldPlayer.reviveCaps();
        oldPlayer.getCapability(SAOInventory.CAPABILITY).ifPresent(oldInv -> newPlayer.getCapability(SAOInventory.CAPABILITY).ifPresent(newInv -> newInv.deserializeNBT(oldInv.serializeNBT())));
        oldPlayer.invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)player;
            SAOForgeEventHandler.syncInventory(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)player;
            SAOForgeEventHandler.syncInventory(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)player;
            SAOForgeEventHandler.syncInventory(sp);
        }
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        if (player.m_7500_()) {
            return;
        }
        ItemEntity itemEntity = event.getItem();
        ItemStack stack = itemEntity.m_32055_();
        player.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
            ItemStack remaining = inv.insertItem(stack);
            if (remaining.m_41619_()) {
                itemEntity.m_146870_();
                event.setResult(Event.Result.ALLOW);
                if (player instanceof ServerPlayer) {
                    ServerPlayer sp = (ServerPlayer)player;
                    SAOForgeEventHandler.syncInventory(sp);
                }
            } else if (remaining.m_41613_() < stack.m_41613_()) {
                itemEntity.m_32045_(remaining);
                event.setResult(Event.Result.ALLOW);
                if (player instanceof ServerPlayer) {
                    ServerPlayer sp = (ServerPlayer)player;
                    SAOForgeEventHandler.syncInventory(sp);
                }
            } else {
                event.setCanceled(true);
            }
        });
    }

    public static void syncInventory(ServerPlayer player) {
        player.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> SAOPacketHandler.sendToPlayer(new SAOInventorySyncPacket(inv.serializeNBT()), player));
    }
}

