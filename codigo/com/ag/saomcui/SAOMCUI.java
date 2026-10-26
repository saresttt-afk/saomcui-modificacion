/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterGuiOverlaysEvent
 *  net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.server.ServerStartedEvent
 *  net.minecraftforge.event.server.ServerStoppingEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 */
package com.ag.saomcui;

import com.ag.saomcui.SAOSounds;
import com.ag.saomcui.client.SAOClientEventHandler;
import com.ag.saomcui.client.SAOHudRenderer;
import com.ag.saomcui.command.SAOCommand;
import com.ag.saomcui.config.SAOConfig;
import com.ag.saomcui.inventory.SAOInventory;
import com.ag.saomcui.network.ConfigSyncPacket;
import com.ag.saomcui.network.SAOPacketHandler;
import com.ag.saomcui.social.SocialStorage;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value="saomcui")
public class SAOMCUI {
    public static final String MODID = "saomcui";

    public SAOMCUI() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SAOSounds.SOUNDS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onRegisterCapabilities);
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(SAOInventory.class);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        SAOConfig.load();
        SAOPacketHandler.register();
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        SocialStorage.load(event.getServer());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        SocialStorage.save();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        SAOCommand.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerJoinConfigSync(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)player;
            SAOPacketHandler.sendToPlayer(new ConfigSyncPacket(SAOConfig.enableCrafting), sp);
        }
    }

    @Mod.EventBusSubscriber(modid="saomcui", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "sao_hud", (gui, graphics, partialTicks, width, height) -> SAOHudRenderer.renderSAOHud(graphics, partialTicks));
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MinecraftForge.EVENT_BUS.register(SAOClientEventHandler.class);
        }
    }
}

