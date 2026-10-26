/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
 *  net.minecraft.client.gui.screens.inventory.InventoryScreen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.client.event.ClientChatReceivedEvent
 *  net.minecraftforge.client.event.RegisterClientCommandsEvent
 *  net.minecraftforge.client.event.RenderGuiOverlayEvent$Pre
 *  net.minecraftforge.client.event.RenderLivingEvent$Post
 *  net.minecraftforge.client.event.RenderNameTagEvent
 *  net.minecraftforge.client.event.ScreenEvent$Opening
 *  net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 */
package com.ag.saomcui.client;

import com.ag.saomcui.client.CreativeChoiceModalScreen;
import com.ag.saomcui.client.SAOChatManager;
import com.ag.saomcui.client.SAOClientPreferences;
import com.ag.saomcui.client.SAODrawingUtils;
import com.ag.saomcui.client.SAOHealthFocus;
import com.ag.saomcui.client.SAOHealthModel;
import com.ag.saomcui.client.SAOMainMenuScreen;
import com.ag.saomcui.client.SAOText;
import com.ag.saomcui.client.WebTextureHelper;
import com.ag.saomcui.network.ChatPacket;
import com.ag.saomcui.social.SocialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;

public class SAOClientEventHandler {
    public static boolean bypassCreativeInventory = false;

    @SubscribeEvent
    public static void onClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register((LiteralArgumentBuilder)Commands.m_82127_((String)"saoui").then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"healthbar").executes(ctx -> SAOClientEventHandler.setHealthBars(!SAOClientPreferences.healthBars()))).then(Commands.m_82127_((String)"on").executes(ctx -> SAOClientEventHandler.setHealthBars(true)))).then(Commands.m_82127_((String)"off").executes(ctx -> SAOClientEventHandler.setHealthBars(false)))));
    }

    private static int setHealthBars(boolean enabled) {
        SAOClientPreferences.setHealthBars(enabled);
        SAOClientPreferences.save();
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        if (player != null) {
            player.m_5661_((Component)Component.m_237113_((String)("SAO HUD: " + (enabled ? "ON" : "OFF (Minecraft HUD restored)"))), false);
        }
        return 1;
    }

    @SubscribeEvent
    public static void onClientChatReceived(ClientChatReceivedEvent event) {
        String msg = event.getMessage().getString();
        SAOChatManager.receiveMessage(ChatPacket.Channel.SYSTEM, "", msg, "");
    }

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen) {
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91074_ != null) {
                if (bypassCreativeInventory) {
                    bypassCreativeInventory = false;
                } else {
                    event.setNewScreen((Screen)new CreativeChoiceModalScreen());
                }
            }
        } else if (event.getScreen() instanceof InventoryScreen) {
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91074_ != null) {
                if (mc.f_91074_.m_7500_()) {
                    event.setNewScreen((Screen)new CreativeChoiceModalScreen());
                } else {
                    event.setNewScreen((Screen)new SAOMainMenuScreen());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGuiOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (!SAOClientPreferences.healthBars()) {
            return;
        }
        ResourceLocation overlayId = event.getOverlay().id();
        if (SAOClientPreferences.healthBars() && VanillaGuiOverlay.PLAYER_HEALTH.id().equals((Object)overlayId) || VanillaGuiOverlay.ARMOR_LEVEL.id().equals((Object)overlayId) || VanillaGuiOverlay.FOOD_LEVEL.id().equals((Object)overlayId) || VanillaGuiOverlay.HOTBAR.id().equals((Object)overlayId)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        ResourceLocation emblemTex;
        Player otherPlayer;
        String emblemUrl;
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity == null) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer localPlayer = mc.f_91074_;
        if (localPlayer == null) {
            return;
        }
        if (livingEntity == localPlayer || !livingEntity.m_6084_() || livingEntity.m_20145_()) {
            return;
        }
        if (mc.f_91066_.f_92062_ || !SAOClientPreferences.healthBars()) {
            return;
        }
        if (localPlayer.m_20270_((Entity)livingEntity) > 10.0f) {
            return;
        }
        PoseStack poseStack = event.getPoseStack();
        EntityRenderDispatcher dispatcher = mc.m_91290_();
        Matrix4f entityOrigin = new Matrix4f((Matrix4fc)poseStack.m_85850_().m_252922_());
        poseStack.m_85836_();
        poseStack.m_85837_(0.0, (double)livingEntity.m_20206_() + 0.85, 0.0);
        Quaternionf rotation = dispatcher.m_253208_();
        poseStack.m_252781_(rotation);
        poseStack.m_85841_(-0.025f, -0.025f, 0.025f);
        float hp = livingEntity.m_21223_();
        float maxHp = livingEntity.m_21233_();
        float pct = SAOText.healthFraction(hp, maxHp);
        Matrix4f matrix = poseStack.m_85850_().m_252922_();
        if (SAOHealthFocus.lookingAt(livingEntity, event.getPartialTick())) {
            SAOHealthModel.renderRibbon(SAOHealthModel.shoulderTransform(entityOrigin, rotation, livingEntity.m_20205_(), livingEntity.m_20206_()), pct, 0.85f);
        }
        if (livingEntity instanceof Player && (emblemUrl = SocialManager.getGlobalEmblem((otherPlayer = (Player)livingEntity).m_20148_())) != null && !emblemUrl.isEmpty() && (emblemTex = WebTextureHelper.getOrCreateWebTexture(emblemUrl)) != null) {
            float emblemSize = 12.0f;
            SAODrawingUtils.drawTexture(matrix, emblemTex, -emblemSize / 2.0f, -6.0f, emblemSize, emblemSize);
        }
        poseStack.m_85849_();
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onRenderNameTag(RenderNameTagEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity living = (LivingEntity)entity;
        Minecraft mc = Minecraft.m_91087_();
        if (living == mc.f_91074_) {
            return;
        }
        if (living instanceof Player) {
            Player player = (Player)living;
            if (SocialManager.isClientInParty()) {
                for (String n : SocialManager.getClientPartyMembers()) {
                    String memberName = n.startsWith("[L] ") ? n.substring(4) : (n.startsWith("[O] ") ? n.substring(4) : n);
                    if (!memberName.equalsIgnoreCase(player.m_36316_().getName())) continue;
                    Component original = event.getContent();
                    event.setContent(SAOText.partyName(original, player.m_36316_().getName()));
                    break;
                }
            }
        }
    }
}

