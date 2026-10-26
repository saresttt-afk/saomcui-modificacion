/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.player.Player
 */
package com.ag.saomcui.client;

import com.ag.saomcui.client.SAOClientEventHandler;
import com.ag.saomcui.client.SAOMainMenuScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

public class CreativeChoiceModalScreen
extends Screen {
    private final ResourceLocation btnOn = new ResourceLocation("saomcui", "textures/gui/buttons/yes_on.png");
    private final ResourceLocation btnOff = new ResourceLocation("saomcui", "textures/gui/buttons/yes.png");

    public CreativeChoiceModalScreen() {
        super((Component)Component.m_237113_((String)"Creative Choice"));
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.m_280273_(graphics);
        float baseScale = 1.3f / (float)this.f_96541_.m_91268_().m_85449_();
        float w = (float)this.f_96543_ / baseScale;
        float h = (float)this.f_96544_ / baseScale;
        float winW = 350.0f;
        float winH = 250.0f;
        float winX = w / 2.0f - winW / 2.0f;
        float winY = h / 2.0f - winH / 2.0f;
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_85841_(baseScale, baseScale, 1.0f);
        graphics.m_280163_(new ResourceLocation("saomcui", "textures/gui/window_blank.png"), (int)winX, (int)winY, 0.0f, 0.0f, (int)winW, (int)winH, (int)winW, (int)winH);
        String title = "Open which Menu?";
        graphics.m_280168_().m_85836_();
        float titleScale = 1.6f;
        graphics.m_280168_().m_252880_(winX + winW / 2.0f, winY + 25.0f, 0.0f);
        graphics.m_280168_().m_85841_(titleScale, titleScale, 1.0f);
        graphics.m_280056_(this.f_96547_, title, -this.f_96547_.m_92895_(title) / 2, 0, -13619152, false);
        graphics.m_280168_().m_85849_();
        String context = "Choose your inventory mode.";
        graphics.m_280168_().m_85836_();
        float contextScale = 1.2f;
        graphics.m_280168_().m_252880_(winX + winW / 2.0f, winY + winH / 2.0f - 20.0f, 0.0f);
        graphics.m_280168_().m_85841_(contextScale, contextScale, 1.0f);
        graphics.m_280056_(this.f_96547_, context, -this.f_96547_.m_92895_(context) / 2, 0, -13619152, false);
        graphics.m_280168_().m_85849_();
        float btnSize = 50.0f;
        float saoX = winX + winW / 4.0f - btnSize / 2.0f;
        float saoY = winY + winH - 65.0f;
        float mcX = winX + 3.0f * winW / 4.0f - btnSize / 2.0f;
        float mcY = saoY;
        boolean hoverSao = (float)mouseX / baseScale >= saoX && (float)mouseX / baseScale <= saoX + btnSize && (float)mouseY / baseScale >= saoY && (float)mouseY / baseScale <= saoY + btnSize;
        boolean hoverMc = (float)mouseX / baseScale >= mcX && (float)mouseX / baseScale <= mcX + btnSize && (float)mouseY / baseScale >= mcY && (float)mouseY / baseScale <= mcY + btnSize;
        graphics.m_280411_(hoverSao ? this.btnOn : this.btnOff, (int)saoX, (int)saoY, (int)btnSize, (int)btnSize, 0.0f, 0.0f, 256, 256, 256, 256);
        graphics.m_280411_(hoverMc ? this.btnOn : this.btnOff, (int)mcX, (int)mcY, (int)btnSize, (int)btnSize, 0.0f, 0.0f, 256, 256, 256, 256);
        String saoText = "SAO";
        graphics.m_280056_(this.f_96547_, saoText, (int)(saoX + btnSize / 2.0f - (float)(this.f_96547_.m_92895_(saoText) / 2)), (int)(saoY - 15.0f), -13619152, false);
        String mcText = "Creative";
        graphics.m_280056_(this.f_96547_, mcText, (int)(mcX + btnSize / 2.0f - (float)(this.f_96547_.m_92895_(mcText) / 2)), (int)(mcY - 15.0f), -13619152, false);
        graphics.m_280168_().m_85849_();
        super.m_88315_(graphics, mouseX, mouseY, partialTicks);
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        float baseScale = 1.3f / (float)this.f_96541_.m_91268_().m_85449_();
        float w = (float)this.f_96543_ / baseScale;
        float h = (float)this.f_96544_ / baseScale;
        float winW = 350.0f;
        float winH = 250.0f;
        float winX = w / 2.0f - winW / 2.0f;
        float winY = h / 2.0f - winH / 2.0f;
        float btnSize = 50.0f;
        float saoX = winX + winW / 4.0f - btnSize / 2.0f;
        float saoY = winY + winH - 65.0f;
        float mcX = winX + 3.0f * winW / 4.0f - btnSize / 2.0f;
        float mcY = saoY;
        if (mouseX / (double)baseScale >= (double)saoX && mouseX / (double)baseScale <= (double)(saoX + btnSize) && mouseY / (double)baseScale >= (double)saoY && mouseY / (double)baseScale <= (double)(saoY + btnSize)) {
            this.f_96541_.m_91106_().m_120367_((SoundInstance)SimpleSoundInstance.m_119752_((SoundEvent)SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("saomcui", "ui.click")), (float)1.0f));
            this.f_96541_.m_91152_((Screen)new SAOMainMenuScreen());
            return true;
        }
        if (mouseX / (double)baseScale >= (double)mcX && mouseX / (double)baseScale <= (double)(mcX + btnSize) && mouseY / (double)baseScale >= (double)mcY && mouseY / (double)baseScale <= (double)(mcY + btnSize)) {
            this.f_96541_.m_91106_().m_120367_((SoundInstance)SimpleSoundInstance.m_119752_((SoundEvent)SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("saomcui", "ui.click")), (float)1.0f));
            SAOClientEventHandler.bypassCreativeInventory = true;
            this.f_96541_.m_91152_((Screen)new CreativeModeInventoryScreen((Player)this.f_96541_.f_91074_, this.f_96541_.f_91074_.f_108617_.m_247016_(), ((Boolean)this.f_96541_.f_91066_.m_257871_().m_231551_()).booleanValue()));
            return true;
        }
        return super.m_6375_(mouseX, mouseY, button);
    }

    public boolean m_7043_() {
        return false;
    }
}

