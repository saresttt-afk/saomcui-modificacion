/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.ParticleStatus
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  net.minecraftforge.client.event.RenderLivingEvent$Pre
 *  net.minecraftforge.client.event.RenderPlayerEvent$Pre
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ag.saomcui.client;

import com.ag.saomcui.client.SAOClientPreferences;
import com.ag.saomcui.client.SAODeathGlow;
import com.ag.saomcui.network.DeathEffectPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Mod.EventBusSubscriber(modid="saomcui", value={Dist.CLIENT})
public final class SAODeathEffects {
    private static final Map<UUID, Burst> BURSTS = new LinkedHashMap<UUID, Burst>();
    private static Object level;
    private static boolean drawingCorpse;

    public static void receive(DeathEffectPacket data) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null || !mc.f_91073_.m_46472_().m_135782_().equals((Object)data.dimension()) || mc.f_91074_ == null || !SAOClientPreferences.healthBars() || mc.f_91074_.m_20275_(data.x(), data.y(), data.z()) > 2304.0) {
            return;
        }
        if (level != mc.f_91073_) {
            BURSTS.clear();
            level = mc.f_91073_;
        }
        if (BURSTS.containsKey(data.entity())) {
            return;
        }
        if (BURSTS.size() >= 24) {
            BURSTS.remove(BURSTS.keySet().iterator().next());
        }
        BURSTS.put(data.entity(), new Burst(data));
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (level != mc.f_91073_ || !SAOClientPreferences.healthBars()) {
            BURSTS.clear();
            level = mc.f_91073_;
        }
        if (!mc.m_91104_()) {
            BURSTS.values().removeIf(b -> ++b.age > 60);
        }
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
        BURSTS.clear();
        level = null;
    }

    @SubscribeEvent
    public static void corpse(RenderLivingEvent.Pre<?, ?> event) {
        Burst burst = BURSTS.get(event.getEntity().m_20148_());
        if (!drawingCorpse && burst != null && burst.age >= 16 && event.getEntity().m_21224_()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void playerCorpse(RenderPlayerEvent.Pre event) {
        Burst burst = BURSTS.get(event.getEntity().m_20148_());
        if (!drawingCorpse && burst != null && burst.age >= 16 && event.getEntity().m_21224_()) {
            event.setCanceled(true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SubscribeEvent
    public static void restingBody(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES || BURSTS.isEmpty()) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null) {
            return;
        }
        Vec3 camera = event.getCamera().m_90583_();
        Matrix4f inverseView = new Matrix4f((Matrix4fc)event.getPoseStack().m_85850_().m_252922_()).invert();
        MultiBufferSource.BufferSource buffers = mc.m_91269_().m_110104_();
        for (Burst burst : BURSTS.values()) {
            LivingEntity entity = burst.corpse;
            if (entity == null || !entity.m_21224_() || burst.age < 16 || burst.age >= 32) continue;
            int old = entity.f_20919_;
            PoseStack pose = event.getPoseStack();
            pose.m_85836_();
            try {
                float age;
                entity.f_20919_ = 20;
                drawingCorpse = true;
                if (burst.age < 22) {
                    burst.x = entity.m_20185_();
                    burst.y = entity.m_20186_();
                    burst.z = entity.m_20189_();
                }
                pose.m_85837_(burst.x - camera.f_82479_, burst.y - camera.f_82480_, burst.z - camera.f_82481_);
                EntityRenderer renderer = mc.m_91290_().m_114382_((Entity)entity);
                if (burst.age < 22) {
                    renderer.m_7392_((Entity)entity, entity.m_146908_(), event.getPartialTick(), pose, (MultiBufferSource)buffers, mc.m_91290_().m_114394_((Entity)entity, event.getPartialTick()));
                    buffers.m_109911_();
                }
                if (!((age = (float)burst.age + event.getPartialTick()) >= 16.0f)) continue;
                float alpha = SAODeathEffects.smooth(age < 22.0f ? (age - 16.0f) / 6.0f : (32.0f - age) / 10.0f);
                ArrayList points = new ArrayList();
                renderer.m_7392_((Entity)entity, entity.m_146908_(), event.getPartialTick(), pose, SAODeathGlow.buffers((MultiBufferSource)buffers, alpha, burst.sampled ? null : p -> {
                    Vector3f local = SAODeathEffects.sampleToCorpse(p, inverseView, camera.f_82479_ - burst.x, camera.f_82480_ - burst.y, camera.f_82481_ - burst.z);
                    float limit = Math.max(3.0f, burst.data.height() + burst.data.width());
                    if (points.size() < 2048 && local.isFinite() && local.lengthSquared() < limit * limit) {
                        points.add(local);
                    }
                }), 0xF000F0);
                buffers.m_109911_();
                if (points.isEmpty()) continue;
                Random random = new Random(burst.data.entity().getLeastSignificantBits());
                for (int i = 0; i < burst.shards.size(); ++i) {
                    Shard oldShard = burst.shards.get(i);
                    burst.shards.set(i, new Shard(new Vector3f((Vector3fc)points.get(random.nextInt(points.size()))), oldShard.velocity, oldShard.size, oldShard.spin));
                }
                burst.sampled = true;
            }
            finally {
                drawingCorpse = false;
                entity.f_20919_ = old;
                pose.m_85849_();
            }
        }
        buffers.m_109911_();
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || BURSTS.isEmpty()) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null || mc.f_91066_.f_92062_) {
            return;
        }
        Vec3 camera = event.getCamera().m_90583_();
        PoseStack pose = event.getPoseStack();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::m_172811_);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        BufferBuilder buffer = Tesselator.m_85913_().m_85915_();
        buffer.m_166779_(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.f_85815_);
        for (Burst burst : BURSTS.values()) {
            float age = (float)burst.age + event.getPartialTick();
            if (age < 20.0f) continue;
            float seconds = Math.max(0.0f, (age - 22.0f) / 20.0f);
            float fade = SAODeathEffects.smooth((60.0f - age) / 18.0f) * SAODeathEffects.smooth((age - 20.0f) / 4.0f);
            DeathEffectPacket d = burst.data;
            for (Shard shard : burst.shards) {
                pose.m_85836_();
                pose.m_85837_(burst.x - camera.f_82479_ + (double)shard.start.x + (double)(shard.velocity.x * seconds), burst.y - camera.f_82480_ + (double)shard.start.y + (double)(shard.velocity.y * seconds) - 0.15 * (double)seconds * (double)seconds, burst.z - camera.f_82481_ + (double)shard.start.z + (double)(shard.velocity.z * seconds));
                pose.m_252781_(event.getCamera().m_253121_());
                pose.m_252781_(Axis.f_252403_.m_252977_(shard.spin + seconds * 170.0f));
                float size = shard.size * (1.0f + 0.8f * (1.0f - SAODeathEffects.smooth((age - 22.0f) / 6.0f)));
                float light = 1.0f - 0.35f * SAODeathEffects.smooth((age - 22.0f) / 8.0f);
                int a = (int)(Math.max(0.0f, fade) * 220.0f);
                Matrix4f matrix = pose.m_85850_().m_252922_();
                buffer.m_252986_(matrix, -size, -size * 0.5f, 0.0f).m_6122_((int)(100.0f * light), 235, 255, a).m_5752_();
                buffer.m_252986_(matrix, size, -size * 0.4f, 0.0f).m_6122_(190, 255, 255, a).m_5752_();
                buffer.m_252986_(matrix, 0.0f, size, 0.0f).m_6122_(90, 190, 255, a).m_5752_();
                pose.m_85849_();
            }
        }
        Tesselator.m_85913_().m_85914_();
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableCull();
    }

    public static float smooth(float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        return t * t * (3.0f - 2.0f * t);
    }

    public static Vector3f sampleToCorpse(Vector3f point, Matrix4f inverseView, double x, double y, double z) {
        return inverseView.transformPosition(new Vector3f((Vector3fc)point)).add((float)x, (float)y, (float)z);
    }

    private static final class Burst {
        final DeathEffectPacket data;
        final List<Shard> shards = new ArrayList<Shard>();
        int age;
        LivingEntity corpse;
        double x;
        double y;
        double z;
        boolean sampled;

        Burst(DeathEffectPacket data) {
            this.data = data;
            Random random = new Random(data.entity().getLeastSignificantBits());
            this.x = data.x();
            this.y = data.y();
            this.z = data.z();
            for (Entity entity : Minecraft.m_91087_().f_91073_.m_104735_()) {
                LivingEntity living;
                if (!entity.m_20148_().equals(data.entity()) || !(entity instanceof LivingEntity)) continue;
                this.corpse = living = (LivingEntity)entity;
                break;
            }
            float w = Math.min(4.0f, data.width());
            float h = Math.min(6.0f, data.height());
            float yaw = this.corpse == null ? 0.0f : (float)Math.toRadians(this.corpse.f_20883_);
            int count = Minecraft.m_91087_().f_91066_.m_231929_().m_231551_() == ParticleStatus.MINIMAL ? 20 : 64;
            for (int i = 0; i < count; ++i) {
                this.shards.add(new Shard(new Vector3f((random.nextFloat() - 0.2f) * h, 0.12f + random.nextFloat() * w, (random.nextFloat() - 0.5f) * w).rotateY(-yaw), new Vector3f((random.nextFloat() - 0.5f) * 2.3f, 0.4f + random.nextFloat() * 1.4f, (random.nextFloat() - 0.5f) * 2.3f), 0.025f + random.nextFloat() * 0.08f, random.nextFloat() * 360.0f));
            }
        }
    }

    private record Shard(Vector3f start, Vector3f velocity, float size, float spin) {
    }
}

