/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.entity.PlayerRideableJumping
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.ag.saomcui.mixin;

import com.ag.saomcui.client.SAOClientPreferences;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.PlayerRideableJumping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Gui.class})
public class GuiMixin {
    @Inject(method={"renderPlayerHealth"}, at={@At(value="HEAD")}, cancellable=true)
    private void saomcui$onRenderPlayerHealth(GuiGraphics p_283143_, CallbackInfo ci) {
        if (SAOClientPreferences.healthBars()) {
            ci.cancel();
        }
    }

    @Inject(method={"renderExperienceBar"}, at={@At(value="HEAD")}, cancellable=true)
    private void saomcui$onRenderExperienceBar(GuiGraphics p_281906_, int p_282731_, CallbackInfo ci) {
        if (SAOClientPreferences.healthBars()) {
            ci.cancel();
        }
    }

    @Inject(method={"renderJumpMeter"}, at={@At(value="HEAD")}, cancellable=true)
    private void saomcui$onRenderJumpMeter(PlayerRideableJumping p_282774_, GuiGraphics p_282939_, int p_283351_, CallbackInfo ci) {
        if (SAOClientPreferences.healthBars()) {
            ci.cancel();
        }
    }
}

