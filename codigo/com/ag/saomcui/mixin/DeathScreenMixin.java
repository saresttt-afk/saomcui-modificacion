/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.DeathScreen
 *  net.minecraft.network.chat.Component
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.ag.saomcui.mixin;

import com.ag.saomcui.client.SAOClientPreferences;
import com.ag.saomcui.client.SAODeathBanner;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={DeathScreen.class})
public class DeathScreenMixin {
    @Redirect(method={"render"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V", ordinal=0))
    private void saomcui$deathTitle(GuiGraphics g, Font font, Component text, int x, int y, int color) {
        if (SAOClientPreferences.healthBars()) {
            SAODeathBanner.render(g, x);
        } else {
            g.m_280653_(font, text, x, y, color);
        }
    }
}

