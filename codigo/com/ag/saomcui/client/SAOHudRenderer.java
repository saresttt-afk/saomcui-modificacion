/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  org.joml.Matrix4f
 */
package com.ag.saomcui.client;

import com.ag.saomcui.client.SAOClientPreferences;
import com.ag.saomcui.client.SAODrawingUtils;
import com.ag.saomcui.client.SAOText;
import com.ag.saomcui.client.WebTextureHelper;
import com.ag.saomcui.social.SocialManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class SAOHudRenderer {
    private static final ResourceLocation HOTBAR_OFF = new ResourceLocation("saomcui", "textures/gui/hotbar/in_hand_off.png");
    private static final ResourceLocation HOTBAR_ON = new ResourceLocation("saomcui", "textures/gui/hotbar/in_hand_on.png");

    public static void renderSAOHud(GuiGraphics graphics, float partialTicks) {
        ResourceLocation emblem;
        String emblemUrl;
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91066_.f_92062_ || mc.f_91074_ == null || !SAOClientPreferences.healthBars()) {
            return;
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        float actualGuiScale = (float)mc.m_91268_().m_85449_();
        float scale = 1.8f / actualGuiScale;
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_85841_(scale, scale, 1.0f);
        float mainStartX = 10.0f;
        float mainStartY = 10.0f;
        float mainW = 300.0f;
        float mainH = 62.0f;
        if (SAOClientPreferences.healthBars()) {
            SAOHudRenderer.drawSAOBar(graphics, mc.f_91074_.m_36316_().getName(), mc.f_91074_.m_21223_(), mc.f_91074_.m_21233_(), mc.f_91074_.f_36078_, mainStartX, mainStartY, mainW, mainH, false, -1);
        }
        float nextY = mainStartY + mainH + 10.0f;
        if (SAOClientPreferences.healthBars() && SocialManager.isClientInParty()) {
            float partyX = mainStartX + 20.0f;
            float partyY = nextY;
            float partyW = mainW * 0.7f;
            float partyH = mainH * 0.8f;
            for (String n : SocialManager.getClientPartyMembers()) {
                String memberName;
                String string = n.startsWith("[L] ") ? n.substring(4) : (memberName = n.startsWith("[O] ") ? n.substring(4) : n);
                if (memberName.equals(mc.f_91074_.m_7755_().getString())) continue;
                Player memberEntity = null;
                for (Player p : mc.f_91073_.m_6907_()) {
                    if (!p.m_36316_().getName().equalsIgnoreCase(memberName)) continue;
                    memberEntity = p;
                    break;
                }
                if (memberEntity == null) continue;
                SAOHudRenderer.drawSAOBar(graphics, memberName, memberEntity.m_21223_(), memberEntity.m_21233_(), memberEntity.f_36078_, partyX, partyY, partyW, partyH, true, -1);
                partyY += partyH + 8.0f;
            }
        }
        if ((emblemUrl = SocialManager.getClientEmblemUrl()) != null && !emblemUrl.isEmpty() && (emblem = WebTextureHelper.getOrCreateWebTexture(emblemUrl)) != null) {
            float emblemSize = 48.0f;
            float screenW = (float)mc.m_91268_().m_85445_() / scale;
            float emblemX = screenW - emblemSize - 10.0f;
            float emblemY = 10.0f;
            graphics.m_280163_(emblem, (int)emblemX, (int)emblemY, 0.0f, 0.0f, (int)emblemSize, (int)emblemSize, (int)emblemSize, (int)emblemSize);
        }
        graphics.m_280168_().m_85849_();
        SAOHudRenderer.renderBottomBars(graphics, mc);
        SAOHudRenderer.renderHotbar(graphics, mc, partialTicks);
    }

    private static void renderHotbar(GuiGraphics graphics, Minecraft mc, float partialTicks) {
        if (mc.f_91074_ == null) {
            return;
        }
        int screenW = mc.m_91268_().m_85445_();
        int screenH = mc.m_91268_().m_85446_();
        int slotSize = 22;
        int totalW = 9 * slotSize;
        int startX = (screenW - totalW) / 2;
        int startY = screenH - slotSize - 2;
        int selectedSlot = mc.f_91074_.m_150109_().f_35977_;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        for (int i = 0; i < 9; ++i) {
            int x = startX + i * slotSize;
            int y = startY;
            ResourceLocation tex = i == selectedSlot ? HOTBAR_ON : HOTBAR_OFF;
            graphics.m_280163_(tex, x, y, 0.0f, 0.0f, slotSize, slotSize, slotSize, slotSize);
            ItemStack stack = mc.f_91074_.m_150109_().m_8020_(i);
            if (stack.m_41619_()) continue;
            int itemX = x + (slotSize - 16) / 2;
            int itemY = y + (slotSize - 16) / 2;
            graphics.m_280480_(stack, itemX, itemY);
            graphics.m_280370_(mc.f_91062_, stack, itemX, itemY);
        }
        RenderSystem.disableBlend();
    }

    private static void renderBottomBars(GuiGraphics graphics, Minecraft mc) {
        if (mc.f_91074_ == null) {
            return;
        }
        int screenW = mc.m_91268_().m_85445_();
        int screenH = mc.m_91268_().m_85446_();
        int centerX = screenW / 2;
        int bottomY = screenH - (SAOClientPreferences.healthBars() ? 35 : 52);
        float armor = mc.f_91074_.m_21230_();
        float food = mc.f_91074_.m_36324_().m_38702_();
        int colPlate = Integer.MIN_VALUE;
        int colArmorFill = -14117176;
        int colFoodFill = -2987746;
        int colWhite = -1;
        graphics.m_280168_().m_85836_();
        if (armor > 0.0f) {
            float aX = centerX - 80;
            float aY = bottomY;
            float aW = 70.0f;
            float aH = 5.0f;
            float fillPct = Math.min(armor / 20.0f, 1.0f);
            SAODrawingUtils.fillQuad(graphics.m_280168_().m_85850_().m_252922_(), aX, aY, aX + aW, aY, aX + aW + 2.0f, aY + aH, aX - 2.0f, aY + aH, colPlate);
            if (fillPct > 0.0f) {
                float fW = aW * fillPct;
                SAODrawingUtils.fillQuad(graphics.m_280168_().m_85850_().m_252922_(), aX, aY, aX + fW, aY, aX + fW + 2.0f * fillPct, aY + aH, aX - 2.0f, aY + aH, colArmorFill);
            }
            SAODrawingUtils.fillQuad(graphics.m_280168_().m_85850_().m_252922_(), aX, aY, aX + aW, aY, aX + aW, aY + 1.0f, aX, aY + 1.0f, colWhite);
            graphics.m_280168_().m_85836_();
            graphics.m_280168_().m_85841_(0.5f, 0.5f, 1.0f);
            graphics.m_280056_(mc.f_91062_, "ARM", (int)((aX - 15.0f) / 0.5f), (int)((aY + 1.0f) / 0.5f), colWhite, true);
            graphics.m_280168_().m_85849_();
        }
        float fX = centerX + 10;
        float fY = bottomY;
        float fW = 70.0f;
        float fH = 5.0f;
        float foodPct = Math.min(food / 20.0f, 1.0f);
        SAODrawingUtils.fillQuad(graphics.m_280168_().m_85850_().m_252922_(), fX, fY, fX + fW, fY, fX + fW + 2.0f, fY + fH, fX - 2.0f, fY + fH, colPlate);
        if (foodPct > 0.0f) {
            float fWFill = fW * foodPct;
            SAODrawingUtils.fillQuad(graphics.m_280168_().m_85850_().m_252922_(), fX, fY, fX + fWFill, fY, fX + fWFill + 2.0f * foodPct, fY + fH, fX - 2.0f, fY + fH, colFoodFill);
        }
        SAODrawingUtils.fillQuad(graphics.m_280168_().m_85850_().m_252922_(), fX, fY, fX + fW, fY, fX + fW, fY + 1.0f, fX, fY + 1.0f, colWhite);
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_85841_(0.5f, 0.5f, 1.0f);
        graphics.m_280056_(mc.f_91062_, "HUN", (int)((fX + fW + 8.0f) / 0.5f), (int)((fY + 1.0f) / 0.5f), colWhite, true);
        graphics.m_280168_().m_85849_();
        graphics.m_280168_().m_85849_();
    }

    private static void drawSAOBar(GuiGraphics graphics, String displayName, float current, float max, int level, float x, float y, float w, float h, boolean shrunken, int colFillOverride) {
        float nameDrawY;
        float fillPct = SAOText.healthFraction(current, max);
        graphics.m_280262_();
        Matrix4f matrix = graphics.m_280168_().m_85850_().m_252922_();
        int colPlate = 0x4DFFFFFF;
        int colWhite = -1;
        int colBlack = -16777216;
        int colGreen = -9326821;
        int colDrawer = 0x4DFFFFFF;
        int colText = -1;
        float pW = shrunken ? 250.0f : 350.0f;
        float pH_L = shrunken ? 24.0f : 32.0f;
        float pH_R = shrunken ? 16.0f : 20.0f;
        float pKinkX = shrunken ? 130.0f : 187.0f;
        float pRightX = shrunken ? 235.0f : 330.0f;
        SAODrawingUtils.fillQuad(matrix, x, y, x + pKinkX, y, x + pKinkX, y + pH_L, x, y + pH_L, colPlate);
        SAODrawingUtils.fillQuad(matrix, x + pKinkX, y, x + pW, y, x + pRightX, y + pH_R, x + pKinkX, y + pH_R, colPlate);
        float nameW = shrunken ? 55.0f : 75.0f;
        float nameY = shrunken ? y + 6.0f : y + 12.0f;
        graphics.m_280168_().m_85836_();
        if (shrunken) {
            graphics.m_280168_().m_85841_(0.8f, 0.8f, 1.0f);
        }
        float nameDrawX = shrunken ? (x + 10.0f) / 0.8f : x + 12.0f;
        float f = nameDrawY = shrunken ? nameY / 0.8f : nameY;
        String fittedName = SAOText.fit(displayName, (int)((nameW - 18.0f) / (shrunken ? 0.8f : 1.0f)), arg_0 -> ((Font)Minecraft.m_91087_().f_91062_).m_92895_(arg_0));
        graphics.m_280056_(Minecraft.m_91087_().f_91062_, fittedName, (int)nameDrawX, (int)nameDrawY, colText, true);
        graphics.m_280168_().m_85849_();
        float bX = x + nameW;
        float bY = y + (float)(shrunken ? 3 : 4);
        float bW = shrunken ? 180.0f : 265.0f;
        float bH_L = shrunken ? 18.0f : 24.0f;
        float bH_R = shrunken ? 10.0f : 12.0f;
        float bK1 = shrunken ? 75.0f : 110.5f;
        float bK2 = shrunken ? 85.0f : 120.5f;
        float bR = shrunken ? 175.0f : 255.0f;
        SAODrawingUtils.fillQuad(matrix, bX, bY, bX + bK1, bY, bX + bK1, bY + bH_L, bX, bY + bH_L, colWhite);
        SAODrawingUtils.fillQuad(matrix, bX + bK1, bY, bX + bK2, bY, bX + bK2, bY + bH_R, bX + bK1, bY + bH_L, colWhite);
        SAODrawingUtils.fillQuad(matrix, bX + bK2, bY, bX + bW, bY, bX + bR, bY + bH_R, bX + bK2, bY + bH_R, colWhite);
        SAODrawingUtils.fillTriangle(matrix, bX + bW, bY, bX + bW, bY + 4.0f, bX + bR, bY + bH_R, colWhite);
        float iX = bX + 1.0f;
        float iY = bY + 1.0f;
        float iW = bW - 1.0f;
        float iH_L = bH_L - 2.0f;
        float iH_R = bH_R - 2.0f;
        float rK1 = bK1 - 0.5f;
        float rK2 = bK2 - 0.5f;
        float rR = bR - 1.0f;
        SAODrawingUtils.fillQuad(matrix, iX, iY, iX + rK1, iY, iX + rK1, iY + iH_L, iX, iY + iH_L, colBlack);
        SAODrawingUtils.fillQuad(matrix, iX + rK1, iY, iX + rK2, iY, iX + rK2, iY + iH_R, iX + rK1, iY + iH_L, colBlack);
        SAODrawingUtils.fillQuad(matrix, iX + rK2, iY, iX + iW, iY, iX + rR, iY + iH_R, iX + rK2, iY + iH_R, colBlack);
        float gX = iX + 1.0f;
        float gY = iY + 1.0f;
        float gW = iW - 2.0f;
        float gH_L = iH_L - 2.0f;
        float gH_R = iH_R - 2.0f;
        float gK1 = rK1 - 1.0f;
        float gK2 = rK2 - 1.0f;
        float gR = rR - 1.0f;
        SAODrawingUtils.fillQuad(matrix, gX, gY, gX + gK1, gY, gX + gK1, gY + gH_L, gX, gY + gH_L, colPlate);
        SAODrawingUtils.fillQuad(matrix, gX + gK1, gY, gX + gK2, gY, gX + gK2, gY + gH_R, gX + gK1, gY + gH_L, colPlate);
        SAODrawingUtils.fillQuad(matrix, gX + gK2, gY, gX + gW, gY, gX + gR, gY + gH_R, gX + gK2, gY + gH_R, colPlate);
        if (fillPct > 0.0f) {
            float hSlope;
            int colFill;
            int n = colFill = colFillOverride != -1 ? colFillOverride : colGreen;
            if (colFillOverride == -1) {
                if (fillPct <= 0.25f) {
                    colFill = -4974311;
                } else if (fillPct <= 0.5f) {
                    colFill = -1650151;
                }
            }
            float curW = gW * fillPct;
            float f2 = hSlope = shrunken ? 7.0f : 10.0f;
            if (curW > 0.0f) {
                float seg1W = Math.min(curW, gK1);
                float s1Slope = curW <= gK1 ? Math.min(hSlope, seg1W) : 0.0f;
                SAODrawingUtils.fillQuad(matrix, gX, gY, gX + seg1W, gY, gX + seg1W - s1Slope, gY + gH_L, gX, gY + gH_L, colFill);
                if (curW > gK1) {
                    float seg2W = Math.min(curW - gK1, gK2 - gK1);
                    float tPct = seg2W / (gK2 - gK1);
                    float curH = gH_L + (gH_R - gH_L) * tPct;
                    float s2Slope = curW <= gK2 ? Math.min(hSlope, seg2W) : 0.0f;
                    SAODrawingUtils.fillQuad(matrix, gX + gK1, gY, gX + gK1 + seg2W, gY, gX + gK1 + seg2W - s2Slope, gY + curH, gX + gK1, gY + gH_L, colFill);
                    if (curW > gK2) {
                        float seg3W = curW - gK2;
                        SAODrawingUtils.fillQuad(matrix, gX + gK2, gY, gX + gK2 + seg3W, gY, gX + gK2 + seg3W - Math.min(hSlope, seg3W), gY + gH_R, gX + gK2, gY + gH_R, colFill);
                    }
                }
            }
        }
        float indY = y + pH_R + 1.0f;
        float indH = shrunken ? 11.0f : 14.0f;
        float hpX = x + (float)(shrunken ? 135 : 192);
        float hpW = shrunken ? 55.0f : 80.0f;
        SAODrawingUtils.fillQuad(matrix, hpX, indY, hpX + hpW, indY, hpX + hpW, indY + indH, hpX, indY + indH, colDrawer);
        String hpStr = (int)current + " / " + (int)max;
        graphics.m_280168_().m_85836_();
        if (shrunken) {
            graphics.m_280168_().m_85841_(0.7f, 0.7f, 1.0f);
        }
        float hpTextX = shrunken ? (hpX + hpW / 2.0f - (float)Minecraft.m_91087_().f_91062_.m_92895_(hpStr) * 0.7f / 2.0f) / 0.7f : hpX + hpW / 2.0f - (float)(Minecraft.m_91087_().f_91062_.m_92895_(hpStr) / 2);
        float hpTextY = shrunken ? (indY + 2.0f) / 0.7f : indY + 3.0f;
        graphics.m_280056_(Minecraft.m_91087_().f_91062_, hpStr, (int)hpTextX, (int)hpTextY, colText, true);
        graphics.m_280168_().m_85849_();
        if (level >= 0) {
            float lvlX = hpX + hpW + 2.0f;
            float lvlW = shrunken ? 40.0f : 55.0f;
            SAODrawingUtils.fillQuad(matrix, lvlX, indY, lvlX + lvlW, indY, lvlX + lvlW, indY + indH, lvlX, indY + indH, colDrawer);
            String lvlStr = "LV: " + level;
            graphics.m_280168_().m_85836_();
            if (shrunken) {
                graphics.m_280168_().m_85841_(0.7f, 0.7f, 1.0f);
            }
            float lvlTextX = shrunken ? (lvlX + lvlW / 2.0f - (float)Minecraft.m_91087_().f_91062_.m_92895_(lvlStr) * 0.7f / 2.0f) / 0.7f : lvlX + lvlW / 2.0f - (float)(Minecraft.m_91087_().f_91062_.m_92895_(lvlStr) / 2);
            float lvlTextY = shrunken ? (indY + 2.0f) / 0.7f : indY + 3.0f;
            graphics.m_280056_(Minecraft.m_91087_().f_91062_, lvlStr, (int)lvlTextX, (int)lvlTextY, colText, true);
            graphics.m_280168_().m_85849_();
        }
        SAODrawingUtils.fillQuad(matrix, x - 2.0f, y, x, y, x, y + pH_L, x - 2.0f, y + pH_L, colWhite);
        SAODrawingUtils.fillQuad(matrix, x - 6.0f, y - 2.0f, x - 2.0f, y - 2.0f, x - 2.0f, y + 6.0f, x - 6.0f, y + 6.0f, colWhite);
        SAODrawingUtils.fillQuad(matrix, x - 6.0f, y + pH_L - 6.0f, x - 2.0f, y + pH_L - 6.0f, x - 2.0f, y + pH_L + 2.0f, x - 6.0f, y + pH_L + 2.0f, colWhite);
    }
}

