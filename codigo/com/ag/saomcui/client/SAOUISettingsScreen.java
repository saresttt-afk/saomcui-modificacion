/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSliderButton
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 */
package com.ag.saomcui.client;

import com.ag.saomcui.client.SAOClientPreferences;
import com.ag.saomcui.client.SAOMainMenuScreen;
import com.ag.saomcui.client.SAOUISounds;
import com.ag.saomcui.client.map.SAOMapScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public final class SAOUISettingsScreen
extends Screen {
    private final SAOMainMenuScreen parent;

    public SAOUISettingsScreen(SAOMainMenuScreen parent) {
        super((Component)Component.m_237113_((String)"UI Settings"));
        this.parent = parent;
    }

    protected void m_7856_() {
        this.m_142416_((GuiEventListener)new AbstractSliderButton(24, 83, 302, 24, (Component)Component.m_237119_(), ((double)SAOClientPreferences.scale() - 0.75) / 0.75){
            {
                this.m_5695_();
            }

            public void m_7435_(SoundManager manager) {
                SAOUISounds.play("ui.click");
            }

            protected void m_5695_() {
                this.m_93666_((Component)Component.m_237113_((String)("Menu size: " + Math.round(SAOClientPreferences.scale() * 100.0f) + "%")));
            }

            protected void m_5697_() {
                float old = SAOClientPreferences.scale();
                SAOClientPreferences.setScale((float)Math.round((0.75 + this.f_93577_ * 0.75) * 100.0) / 100.0f);
                if (old != SAOClientPreferences.scale()) {
                    SAOUISounds.move();
                }
                this.m_5695_();
            }

            public void m_87963_(GuiGraphics g, int mx, int my, float dt) {
                g.m_280509_(this.m_252754_(), this.m_252907_(), this.m_252754_() + this.f_93618_, this.m_252907_() + this.f_93619_, -1381654);
                g.m_280509_(this.m_252754_(), this.m_252907_() + this.f_93619_ - 4, this.m_252754_() + this.f_93618_, this.m_252907_() + this.f_93619_, -3355444);
                int knob = this.m_252754_() + (int)(this.f_93577_ * (double)(this.f_93618_ - 8));
                g.m_280509_(this.m_252754_(), this.m_252907_() + this.f_93619_ - 4, knob + 8, this.m_252907_() + this.f_93619_, -1336576);
                g.m_280509_(knob, this.m_252907_() + this.f_93619_ - 7, knob + 8, this.m_252907_() + this.f_93619_, this.m_198029_() ? -4687872 : -1336576);
                g.m_280614_(SAOUISettingsScreen.this.f_96547_, this.m_6035_(), this.m_252754_() + (this.f_93618_ - SAOUISettingsScreen.this.f_96547_.m_92852_((FormattedText)this.m_6035_())) / 2, this.m_252907_() + 4, -13619152, false);
            }
        });
        this.m_142416_((GuiEventListener)SAOMapScreen.button(24, 174, 145, "Reset: 100%", () -> {
            SAOClientPreferences.setScale(1.0f);
            SAOClientPreferences.save();
            this.m_232761_();
        }));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(181, 174, 145, "Done", this::m_7379_));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(24, 200, 302, "HUD: " + (SAOClientPreferences.healthBars() ? "SAO" : "Minecraft"), () -> {
            SAOClientPreferences.setHealthBars(!SAOClientPreferences.healthBars());
            SAOClientPreferences.save();
            this.m_232761_();
        }));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(24, 225, 302, "Floating menu: " + (SAOClientPreferences.floatingMenu() ? "ON" : "OFF"), () -> {
            SAOClientPreferences.setFloatingMenu(!SAOClientPreferences.floatingMenu());
            SAOClientPreferences.save();
            this.m_232761_();
        }));
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float dt) {
        g.m_280509_(0, 0, this.f_96543_, this.f_96544_, -1);
        g.m_280509_(1, 1, this.f_96543_ - 1, 30, -1381654);
        g.m_280614_(this.f_96547_, this.f_96539_, 18, 11, -8367360, false);
        g.m_280509_(18, 25, 85, 27, -1336576);
        g.m_280056_(this.f_96547_, "SAO menu scale", 24, 55, -13619152, false);
        g.m_280056_(this.f_96547_, "75%", 24, 114, -11184811, false);
        g.m_280056_(this.f_96547_, "150%", 302, 114, -11184811, false);
        g.m_280056_(this.f_96547_, "Live preview. Saved on this device.", 24, 139, -11184811, false);
        g.m_280056_(this.f_96547_, "Limited to fit your window.", 24, 152, -11184811, false);
        super.m_88315_(g, mx, my, dt);
    }

    public void m_7861_() {
        SAOClientPreferences.save();
    }

    public void m_7379_() {
        this.parent.setMapContent(null);
    }

    public boolean m_7043_() {
        return false;
    }
}

