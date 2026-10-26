/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Vector3f
 */
package com.ag.saomcui.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

final class SAODeathGlow
implements VertexConsumer {
    private static ResourceLocation white;
    private final VertexConsumer out;
    private final int alpha;
    private Consumer<Vector3f> capture;

    private SAODeathGlow(VertexConsumer out, float alpha) {
        this.out = out;
        this.alpha = (int)(255.0f * Math.max(0.0f, Math.min(1.0f, alpha)));
    }

    static MultiBufferSource buffers(MultiBufferSource source, float alpha) {
        return SAODeathGlow.buffers(source, alpha, null);
    }

    static MultiBufferSource buffers(MultiBufferSource source, float alpha, Consumer<Vector3f> capture) {
        if (white == null) {
            NativeImage pixel = new NativeImage(1, 1, false);
            pixel.m_84988_(0, 0, -1);
            white = Minecraft.m_91087_().m_91097_().m_118490_("sao_death_glow", new DynamicTexture(pixel));
        }
        return type -> {
            if (type.m_110508_() != DefaultVertexFormat.f_85812_) {
                return source.m_6299_(type);
            }
            SAODeathGlow consumer = new SAODeathGlow(source.m_6299_(RenderType.m_110473_((ResourceLocation)white)), alpha);
            consumer.capture = capture;
            return consumer;
        };
    }

    public VertexConsumer m_5483_(double x, double y, double z) {
        if (this.capture != null) {
            this.capture.accept(new Vector3f((float)x, (float)y, (float)z));
        }
        this.out.m_5483_(x, y, z);
        return this;
    }

    public VertexConsumer m_6122_(int r, int g, int b, int a) {
        this.out.m_6122_(155, 245, 255, this.alpha);
        return this;
    }

    public VertexConsumer m_7421_(float u, float v) {
        this.out.m_7421_(0.0f, 0.0f);
        return this;
    }

    public VertexConsumer m_7122_(int u, int v) {
        this.out.m_7122_(0, 10);
        return this;
    }

    public VertexConsumer m_7120_(int u, int v) {
        this.out.m_7120_(240, 240);
        return this;
    }

    public VertexConsumer m_5601_(float x, float y, float z) {
        this.out.m_5601_(x, y, z);
        return this;
    }

    public void m_5752_() {
        this.out.m_5752_();
    }

    public void m_7404_(int r, int g, int b, int a) {
        this.out.m_7404_(155, 245, 255, this.alpha);
    }

    public void m_141991_() {
        this.out.m_141991_();
    }
}

