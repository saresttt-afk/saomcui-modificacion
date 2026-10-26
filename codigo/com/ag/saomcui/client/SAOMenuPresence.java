/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.ag.saomcui.client;

import com.ag.saomcui.client.SAOHologramRenderer;
import com.ag.saomcui.client.SAOMainMenuScreen;
import com.ag.saomcui.network.MenuActivityPacket;
import com.ag.saomcui.network.PublicMenuView;
import com.ag.saomcui.network.SAOPacketHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="saomcui", value={Dist.CLIENT})
public final class SAOMenuPresence {
    private static final Map<UUID, Presence> PLAYERS = new HashMap<UUID, Presence>();
    private static MenuActivityPacket.Section sent = MenuActivityPacket.Section.CLOSED;
    private static int ticks;

    public static void receive(MenuActivityPacket.Delivery data) {
        long now = Util.m_137550_();
        Presence old = PLAYERS.get(data.player);
        if (data.section == MenuActivityPacket.Section.CLOSED) {
            if (old != null && old.closed == 0L) {
                old.closed = now;
            }
            return;
        }
        if (old == null || old.closed != 0L) {
            if (PLAYERS.size() < 64) {
                PLAYERS.put(data.player, new Presence(data, now));
            }
            return;
        }
        old.fromX = old.cursorX(now);
        old.fromY = old.cursorY(now);
        old.updated = now;
        old.data = data;
        old.expires = now + 3000L;
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        SAOMainMenuScreen screen;
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        long now = Util.m_137550_();
        PLAYERS.values().removeIf(p -> p.expires < now || p.closed != 0L && now - p.closed > 300L);
        if (mc.f_91074_ == null || mc.m_91403_() == null) {
            return;
        }
        Screen screen2 = mc.f_91080_;
        SAOMainMenuScreen menu = screen2 instanceof SAOMainMenuScreen ? (screen = (SAOMainMenuScreen)screen2) : null;
        MenuActivityPacket.Section section = menu == null ? MenuActivityPacket.Section.CLOSED : menu.activitySection();
        if (section == MenuActivityPacket.Section.CLOSED && sent != section || section != MenuActivityPacket.Section.CLOSED && ++ticks >= 4) {
            SAOPacketHandler.sendToServer(new MenuActivityPacket(section, menu == null ? PublicMenuView.EMPTY : menu.publicView()));
            sent = section;
            ticks = 0;
        }
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
        PLAYERS.clear();
        sent = MenuActivityPacket.Section.CLOSED;
        ticks = 0;
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null || mc.f_91074_ == null || mc.f_91066_.f_92062_) {
            return;
        }
        long now = Util.m_137550_();
        for (AbstractClientPlayer player : mc.f_91073_.m_6907_()) {
            Presence p = PLAYERS.get(player.m_20148_());
            if (p == null || p.expires < now || player == mc.f_91074_ || player.m_20177_((Player)mc.f_91074_) || player.m_20163_() || !player.m_6084_() || mc.f_91074_.m_20280_((Entity)player) > 256.0) continue;
            SAOHologramRenderer.render(event, (Player)player, p, now);
        }
    }

    static final class Presence {
        MenuActivityPacket.Delivery data;
        long expires;
        long opened;
        long closed;
        long updated;
        float fromX;
        float fromY;

        Presence(MenuActivityPacket.Delivery data, long now) {
            this.data = data;
            this.opened = this.updated = now;
            this.expires = now + 3000L;
            this.fromX = data.view.cursorX();
            this.fromY = data.view.cursorY();
        }

        float cursorX(long now) {
            return this.fromX + ((float)this.data.view.cursorX() - this.fromX) * Math.min(1.0f, (float)(now - this.updated) / 200.0f);
        }

        float cursorY(long now) {
            return this.fromY + ((float)this.data.view.cursorY() - this.fromY) * Math.min(1.0f, (float)(now - this.updated) / 200.0f);
        }
    }
}

