/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Matrix4f
 */
package com.ag.saomcui.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public final class SAODeathBanner {
    private static final ResourceLocation IMAGE = new ResourceLocation("saomcui", "textures/effects/you_are_dead.png");
    private static final float[][] OUTLINE = new float[][]{{830.0f, 481.0f}, {3037.0f, 481.0f}, {3383.0f, 1092.0f}, {3037.0f, 1706.0f}, {830.0f, 1706.0f}, {470.0f, 1092.0f}};

    public static void render(GuiGraphics graphics, int centerX) {
        float width = Math.min(90.0f, (float)centerX * 1.65f);
        float scale = width / 2913.0f;
        graphics.m_280262_();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::m_172817_);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)IMAGE);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        BufferBuilder b = Tesselator.m_85913_().m_85915_();
        b.m_166779_(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.f_85817_);
        Matrix4f matrix = graphics.m_280168_().m_85850_().m_252922_();
        for (int i = 0; i < OUTLINE.length; ++i) {
            for (float[] p : new float[][]{{1926.0f, 1092.0f}, OUTLINE[i], OUTLINE[(i + 1) % OUTLINE.length]}) {
                b.m_252986_(matrix, (float)centerX + (p[0] - 1926.0f) * scale, 2.0f + (p[1] - 481.0f) * scale, 0.0f).m_7421_(p[0] / 3840.0f, p[1] / 2160.0f).m_5752_();
            }
        }
        Tesselator.m_85913_().m_85914_();
        RenderSystem.enableCull();
    }
}

