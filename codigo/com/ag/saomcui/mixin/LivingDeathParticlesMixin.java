/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.ag.saomcui.mixin;

import com.ag.saomcui.client.SAOClientPreferences;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LivingEntity.class})
public abstract class LivingDeathParticlesMixin {
    @Inject(method={"makePoofParticles"}, at={@At(value="HEAD")}, cancellable=true)
    private void sao$replaceDeathPoof(CallbackInfo ci) {
        if (SAOClientPreferences.healthBars()) {
            ci.cancel();
        }
    }
}

