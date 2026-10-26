/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.ag.saomcui.client;

import com.ag.saomcui.client.SAOClientPreferences;
import com.ag.saomcui.client.SAOText;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="saomcui", value={Dist.CLIENT})
public final class SAOCriticalWarning {
    private static Warning sound;
    private static long lastStart;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean critical(float fraction, boolean alreadyPlaying) {
        if (!Float.isFinite(fraction)) return false;
        if (!(fraction > 0.0f)) return false;
        float f = alreadyPlaying ? 0.3f : 0.25f;
        if (!(fraction <= f)) return false;
        return true;
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        boolean play;
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        boolean bl = SAOClientPreferences.healthBars() && player != null && mc.f_91073_ != null && player.m_6084_() && !player.m_7500_() && !player.m_5833_() && !mc.m_91104_() && SAOCriticalWarning.critical(SAOText.healthFraction(player.m_21223_() + player.m_6103_(), player.m_21233_()), sound != null) ? true : (play = false);
        if (!play) {
            SAOCriticalWarning.stop();
            return;
        }
        long now = Util.m_137550_();
        if (sound != null && now - lastStart > 1000L && !mc.m_91106_().m_120403_((SoundInstance)sound)) {
            SAOCriticalWarning.stop();
        }
        if (sound == null && now - lastStart > 500L) {
            sound = new Warning();
            lastStart = now;
            mc.m_91106_().m_120367_((SoundInstance)sound);
        }
    }

    private static void stop() {
        if (sound != null) {
            sound.finish();
            Minecraft.m_91087_().m_91106_().m_120399_((SoundInstance)sound);
            sound = null;
        }
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
        SAOCriticalWarning.stop();
    }

    private static final class Warning
    extends AbstractTickableSoundInstance {
        Warning() {
            super(SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("saomcui", "ui.critical_health")), SoundSource.PLAYERS, RandomSource.m_216327_());
            this.f_119578_ = true;
            this.f_119579_ = 0;
            this.f_119582_ = true;
            this.f_119580_ = SoundInstance.Attenuation.NONE;
            this.f_119573_ = 0.65f;
            this.f_119574_ = 1.0f;
        }

        public void m_7788_() {
        }

        void finish() {
            this.m_119609_();
        }
    }
}

