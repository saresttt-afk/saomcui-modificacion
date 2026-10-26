/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.blockentity.BeaconRenderer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.ag.saomcui.client.map;

import com.ag.saomcui.client.map.MapClient;
import com.ag.saomcui.client.map.MapStore;
import com.ag.saomcui.map.Waypoint;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="saomcui", value={Dist.CLIENT})
public final class WaypointBeaconRenderer {
    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.m_91087_();
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS || mc.f_91073_ == null || mc.f_91074_ == null || mc.f_91066_.f_92062_) {
            return;
        }
        Camera camera = event.getCamera();
        Vec3 origin = camera.m_90583_();
        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.m_91269_().m_110104_();
        List<Waypoint> points = MapStore.data.waypoints.stream().filter(w -> w.visible && w.dimension.equals(MapClient.dimension())).sorted(Comparator.comparingDouble(w -> origin.m_82531_((double)w.x + 0.5, (double)w.y, (double)w.z + 0.5))).limit(64L).toList();
        for (Waypoint w2 : points) {
            double distance = Math.sqrt(origin.m_82531_((double)w2.x + 0.5, (double)w2.y, (double)w2.z + 0.5));
            if (distance > 2048.0) continue;
            pose.m_85836_();
            pose.m_85837_((double)w2.x - origin.f_82479_, (double)w2.y - origin.f_82480_, (double)w2.z - origin.f_82481_);
            float[] color = new float[]{(float)(w2.color >> 16 & 0xFF) / 255.0f, (float)(w2.color >> 8 & 0xFF) / 255.0f, (float)(w2.color & 0xFF) / 255.0f};
            BeaconRenderer.m_112184_((PoseStack)pose, (MultiBufferSource)buffers, (ResourceLocation)BeaconRenderer.f_112102_, (float)event.getPartialTick(), (float)1.0f, (long)mc.f_91073_.m_46467_(), (int)0, (int)Math.max(48, mc.f_91073_.m_151558_() - w2.y), (float[])color, (float)0.16f, (float)0.32f);
            pose.m_85849_();
            pose.m_85836_();
            pose.m_85837_((double)w2.x + 0.5 - origin.f_82479_, (double)(w2.y + 3) - origin.f_82480_, (double)w2.z + 0.5 - origin.f_82481_);
            pose.m_252781_(camera.m_253121_());
            float scale = (float)Math.max(0.025, Math.min(1.5, distance * 0.0015));
            pose.m_85841_(-scale, -scale, scale);
            String label = w2.name + "  " + (int)Math.hypot((double)w2.x - mc.f_91074_.m_20185_(), (double)w2.z - mc.f_91074_.m_20189_()) + "m";
            mc.f_91062_.m_271703_(label, (float)(-mc.f_91062_.m_92895_(label)) / 2.0f, 0.0f, -1, false, pose.m_85850_().m_252922_(), (MultiBufferSource)buffers, Font.DisplayMode.SEE_THROUGH, -1340594128, 0xF000F0);
            pose.m_85849_();
        }
        buffers.m_109911_();
    }
}

