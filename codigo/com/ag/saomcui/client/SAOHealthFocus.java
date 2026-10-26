/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.projectile.ProjectileUtil
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.ag.saomcui.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

final class SAOHealthFocus {
    private static long frame;
    private static Entity target;

    SAOHealthFocus() {
    }

    static boolean lookingAt(LivingEntity entity, float partialTick) {
        Minecraft mc = Minecraft.m_91087_();
        Entity camera = mc.m_91288_();
        if (camera == null || mc.f_91080_ != null) {
            return false;
        }
        long now = System.nanoTime() / 8000000L;
        if (now != frame || target != null && target.m_9236_() != mc.f_91073_) {
            frame = now;
            Vec3 start = camera.m_20299_(partialTick);
            Vec3 delta = camera.m_20252_(partialTick).m_82490_(10.0);
            HitResult block = camera.m_19907_(10.0, partialTick, false);
            EntityHitResult hit = ProjectileUtil.m_37287_((Entity)camera, (Vec3)start, (Vec3)start.m_82549_(delta), (AABB)camera.m_20191_().m_82369_(delta).m_82400_(1.0), e -> e instanceof LivingEntity && e.m_6084_() && !e.m_20145_() && !e.m_5833_(), (double)start.m_82557_(block.m_82450_()));
            target = hit == null ? null : hit.m_82443_();
        }
        return target == entity;
    }
}

