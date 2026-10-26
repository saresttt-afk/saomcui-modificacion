/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 */
package com.ag.saomcui.client;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public final class SAOUISounds {
    private static long lastMove;

    private SAOUISounds() {
    }

    public static void play(String path) {
        Minecraft.m_91087_().m_91106_().m_120367_((SoundInstance)SimpleSoundInstance.m_119752_((SoundEvent)SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("saomcui", path)), (float)1.0f));
    }

    public static void move() {
        long now = Util.m_137550_();
        if (now - lastMove >= 120L) {
            lastMove = now;
            SAOUISounds.play("ui.move");
        }
    }
}

