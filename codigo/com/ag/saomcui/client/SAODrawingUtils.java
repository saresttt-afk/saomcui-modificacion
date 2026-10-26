/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
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
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class SAODrawingUtils {
    public static void fillQuad(Matrix4f matrix, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int color) {
        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::m_172811_);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder bufferbuilder = tesselator.m_85915_();
        bufferbuilder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        bufferbuilder.m_252986_(matrix, x1, y1, 0.0f).m_6122_(r, g, b, a).m_5752_();
        bufferbuilder.m_252986_(matrix, x2, y2, 0.0f).m_6122_(r, g, b, a).m_5752_();
        bufferbuilder.m_252986_(matrix, x3, y3, 0.0f).m_6122_(r, g, b, a).m_5752_();
        bufferbuilder.m_252986_(matrix, x4, y4, 0.0f).m_6122_(r, g, b, a).m_5752_();
        tesselator.m_85914_();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    public static void fillTriangle(Matrix4f matrix, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::m_172811_);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder bufferbuilder = tesselator.m_85915_();
        bufferbuilder.m_166779_(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.f_85815_);
        bufferbuilder.m_252986_(matrix, x1, y1, 0.0f).m_6122_(r, g, b, a).m_5752_();
        bufferbuilder.m_252986_(matrix, x2, y2, 0.0f).m_6122_(r, g, b, a).m_5752_();
        bufferbuilder.m_252986_(matrix, x3, y3, 0.0f).m_6122_(r, g, b, a).m_5752_();
        tesselator.m_85914_();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    public static void drawCurvedBar(Matrix4f matrix, float x, float y, float w, float h, float fillPct, int color) {
        int segments = 20;
        float segmentW = w / (float)segments;
        int a_bg = 77;
        int r_bg = 255;
        int g_bg = 255;
        int b_bg = 255;
        int a_fill = color >> 24 & 0xFF;
        int r_fill = color >> 16 & 0xFF;
        int g_fill = color >> 8 & 0xFF;
        int b_fill = color & 0xFF;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::m_172811_);
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder bb = tesselator.m_85915_();
        bb.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        for (int i = 0; i < segments; ++i) {
            float xStart = x + (float)i * segmentW;
            float xEnd = x + (float)(i + 1) * segmentW;
            float relStart = (float)i / (float)segments;
            float relEnd = (float)(i + 1) / (float)segments;
            float yOffStart = (float)(Math.sin((double)relStart * Math.PI) * 10.0);
            float yOffEnd = (float)(Math.sin((double)relEnd * Math.PI) * 10.0);
            bb.m_252986_(matrix, xStart, y - yOffStart, 0.0f).m_6122_(r_bg, g_bg, b_bg, a_bg).m_5752_();
            bb.m_252986_(matrix, xEnd, y - yOffEnd, 0.0f).m_6122_(r_bg, g_bg, b_bg, a_bg).m_5752_();
            bb.m_252986_(matrix, xEnd, y - yOffEnd + h, 0.0f).m_6122_(r_bg, g_bg, b_bg, a_bg).m_5752_();
            bb.m_252986_(matrix, xStart, y - yOffStart + h, 0.0f).m_6122_(r_bg, g_bg, b_bg, a_bg).m_5752_();
        }
        tesselator.m_85914_();
        if (fillPct > 0.0f) {
            float fillW = w * fillPct;
            bb.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
            for (int i = 0; i < segments; ++i) {
                float xStart = x + (float)i * segmentW;
                float xEnd = x + (float)(i + 1) * segmentW;
                if (xStart >= x + fillW) break;
                if (xEnd > x + fillW) {
                    xEnd = x + fillW;
                }
                float relStart = (float)i / (float)segments;
                float relEnd = (xEnd - x) / w;
                float yOffStart = (float)(Math.sin((double)relStart * Math.PI) * 10.0);
                float yOffEnd = (float)(Math.sin((double)relEnd * Math.PI) * 10.0);
                bb.m_252986_(matrix, xStart, y - yOffStart, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
                bb.m_252986_(matrix, xEnd, y - yOffEnd, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
                bb.m_252986_(matrix, xEnd, y - yOffEnd + h, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
                bb.m_252986_(matrix, xStart, y - yOffStart + h, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
            }
            tesselator.m_85914_();
        }
        RenderSystem.enableDepthTest();
    }

    public static void drawPremiumCurvedBar(Matrix4f matrix, float x, float y, float w, float h, float fillPct, int color) {
        float yE;
        float yS;
        float rE;
        float rS;
        float xE;
        float xS;
        int i;
        int segments = 24;
        float segmentW = w / (float)segments;
        int a_fill = color >> 24 & 0xFF;
        int r_fill = color >> 16 & 0xFF;
        int g_fill = color >> 8 & 0xFF;
        int b_fill = color & 0xFF;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::m_172811_);
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder bb = tesselator.m_85915_();
        float bPad = 1.0f;
        bb.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        for (i = 0; i < segments; ++i) {
            xS = x + (float)i * segmentW - bPad;
            xE = x + (float)(i + 1) * segmentW + bPad;
            rS = (float)i / (float)segments;
            rE = (float)(i + 1) / (float)segments;
            yS = (float)(Math.sin((double)rS * Math.PI) * 10.0);
            yE = (float)(Math.sin((double)rE * Math.PI) * 10.0);
            bb.m_252986_(matrix, xS, y - yS - bPad, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
            bb.m_252986_(matrix, xE, y - yE - bPad, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
            bb.m_252986_(matrix, xE, y - yE + h + bPad, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
            bb.m_252986_(matrix, xS, y - yS + h + bPad, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
        }
        tesselator.m_85914_();
        bb.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        for (i = 0; i < segments; ++i) {
            xS = x + (float)i * segmentW;
            xE = x + (float)(i + 1) * segmentW;
            rS = (float)i / (float)segments;
            rE = (float)(i + 1) / (float)segments;
            yS = (float)(Math.sin((double)rS * Math.PI) * 10.0);
            yE = (float)(Math.sin((double)rE * Math.PI) * 10.0);
            bb.m_252986_(matrix, xS, y - yS, 0.0f).m_6122_(0, 0, 0, 128).m_5752_();
            bb.m_252986_(matrix, xE, y - yE, 0.0f).m_6122_(0, 0, 0, 128).m_5752_();
            bb.m_252986_(matrix, xE, y - yE + h, 0.0f).m_6122_(0, 0, 0, 128).m_5752_();
            bb.m_252986_(matrix, xS, y - yS + h, 0.0f).m_6122_(0, 0, 0, 128).m_5752_();
        }
        tesselator.m_85914_();
        if (fillPct > 0.0f) {
            float fillW = w * fillPct;
            bb.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
            for (int i2 = 0; i2 < segments; ++i2) {
                float xS2 = x + (float)i2 * segmentW;
                float xE2 = x + (float)(i2 + 1) * segmentW;
                if (xS2 >= x + fillW) break;
                if (xE2 > x + fillW) {
                    xE2 = x + fillW;
                }
                float rS2 = (float)i2 / (float)segments;
                float rE2 = (xE2 - x) / w;
                float yS2 = (float)(Math.sin((double)rS2 * Math.PI) * 10.0);
                float yE2 = (float)(Math.sin((double)rE2 * Math.PI) * 10.0);
                bb.m_252986_(matrix, xS2, y - yS2, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
                bb.m_252986_(matrix, xE2, y - yE2, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
                bb.m_252986_(matrix, xE2, y - yE2 + h, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
                bb.m_252986_(matrix, xS2, y - yS2 + h, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
            }
            tesselator.m_85914_();
        }
        RenderSystem.enableDepthTest();
    }

    public static void drawSAOEntityHPBar(Matrix4f matrix, float x, float y, float w, float h, float fillPct, int color, boolean disableDepthTest) {
        int a_fill = color >> 24 & 0xFF;
        int r_fill = color >> 16 & 0xFF;
        int g_fill = color >> 8 & 0xFF;
        int b_fill = color & 0xFF;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (disableDepthTest) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::m_172811_);
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder bb = tesselator.m_85915_();
        float slant = 6.0f;
        float padding = 1.0f;
        matrix.translate(0.0f, 0.0f, -0.01f);
        bb.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        bb.m_252986_(matrix, x - padding, y + h + padding, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
        bb.m_252986_(matrix, x + w + padding + slant, y + h + padding, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
        bb.m_252986_(matrix, x + w + padding, y - padding, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
        bb.m_252986_(matrix, x - padding, y - padding, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
        tesselator.m_85914_();
        matrix.translate(0.0f, 0.0f, -0.01f);
        bb.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        bb.m_252986_(matrix, x, y + h, 0.0f).m_6122_(0, 0, 0, 200).m_5752_();
        bb.m_252986_(matrix, x + w + slant, y + h, 0.0f).m_6122_(0, 0, 0, 200).m_5752_();
        bb.m_252986_(matrix, x + w, y, 0.0f).m_6122_(0, 0, 0, 200).m_5752_();
        bb.m_252986_(matrix, x, y, 0.0f).m_6122_(0, 0, 0, 200).m_5752_();
        tesselator.m_85914_();
        matrix.translate(0.0f, 0.0f, -0.01f);
        if (fillPct > 0.0f) {
            float fillW = w * fillPct;
            float fillSlant = slant * fillPct;
            bb.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
            bb.m_252986_(matrix, x, y + h, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
            bb.m_252986_(matrix, x + fillW + fillSlant, y + h, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
            bb.m_252986_(matrix, x + fillW, y, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
            bb.m_252986_(matrix, x, y, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
            tesselator.m_85914_();
        }
        matrix.translate(0.0f, 0.0f, 0.03f);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    public static void drawStraightBar(Matrix4f matrix, float x, float y, float w, float h, float fillPct, int color) {
        SAODrawingUtils.drawStraightBar(matrix, x, y, w, h, fillPct, color, true);
    }

    public static void drawStraightBar(Matrix4f matrix, float x, float y, float w, float h, float fillPct, int color, boolean disableDepthTest) {
        int a_fill = color >> 24 & 0xFF;
        int r_fill = color >> 16 & 0xFF;
        int g_fill = color >> 8 & 0xFF;
        int b_fill = color & 0xFF;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (disableDepthTest) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::m_172811_);
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder bb = tesselator.m_85915_();
        float b = 1.0f;
        bb.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        bb.m_252986_(matrix, x - b, y - b, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
        bb.m_252986_(matrix, x - b, y + h + b, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
        bb.m_252986_(matrix, x + w + b, y + h + b, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
        bb.m_252986_(matrix, x + w + b, y - b, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
        tesselator.m_85914_();
        bb.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        bb.m_252986_(matrix, x, y, 0.0f).m_6122_(0, 0, 0, 255).m_5752_();
        bb.m_252986_(matrix, x, y + h, 0.0f).m_6122_(0, 0, 0, 255).m_5752_();
        bb.m_252986_(matrix, x + w, y + h, 0.0f).m_6122_(0, 0, 0, 255).m_5752_();
        bb.m_252986_(matrix, x + w, y, 0.0f).m_6122_(0, 0, 0, 255).m_5752_();
        tesselator.m_85914_();
        if (fillPct > 0.0f) {
            float curW = w * fillPct;
            bb.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
            bb.m_252986_(matrix, x, y, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
            bb.m_252986_(matrix, x, y + h, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
            bb.m_252986_(matrix, x + curW, y + h, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
            bb.m_252986_(matrix, x + curW, y, 0.0f).m_6122_(r_fill, g_fill, b_fill, a_fill).m_5752_();
            tesselator.m_85914_();
        }
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    public static void drawSAOEntityCursor(Matrix4f matrix, float x, float y, float size, int color, boolean disableDepthTest) {
        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (disableDepthTest) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::m_172811_);
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder bb = tesselator.m_85915_();
        float bPad = 0.5f;
        bb.m_166779_(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.f_85815_);
        bb.m_252986_(matrix, x, y + size + bPad * 1.5f, 0.0f).m_6122_(0, 0, 0, 255).m_5752_();
        bb.m_252986_(matrix, x - size / 2.0f - bPad, y - bPad, 0.0f).m_6122_(0, 0, 0, 255).m_5752_();
        bb.m_252986_(matrix, x + size / 2.0f + bPad, y - bPad, 0.0f).m_6122_(0, 0, 0, 255).m_5752_();
        tesselator.m_85914_();
        bb.m_166779_(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.f_85815_);
        bb.m_252986_(matrix, x, y + size, 0.0f).m_6122_(r, g, b, a).m_5752_();
        bb.m_252986_(matrix, x - size / 2.0f, y, 0.0f).m_6122_(r, g, b, a).m_5752_();
        bb.m_252986_(matrix, x + size / 2.0f, y, 0.0f).m_6122_(r, g, b, a).m_5752_();
        tesselator.m_85914_();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    public static void drawTexture(Matrix4f matrix, ResourceLocation texture, float x, float y, float w, float h) {
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)texture);
        RenderSystem.setShader(GameRenderer::m_172817_);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder bb = tesselator.m_85915_();
        bb.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85817_);
        bb.m_252986_(matrix, x, y, 0.0f).m_7421_(0.0f, 0.0f).m_5752_();
        bb.m_252986_(matrix, x, y + h, 0.0f).m_7421_(0.0f, 1.0f).m_5752_();
        bb.m_252986_(matrix, x + w, y + h, 0.0f).m_7421_(1.0f, 1.0f).m_5752_();
        bb.m_252986_(matrix, x + w, y, 0.0f).m_7421_(1.0f, 0.0f).m_5752_();
        tesselator.m_85914_();
    }
}

