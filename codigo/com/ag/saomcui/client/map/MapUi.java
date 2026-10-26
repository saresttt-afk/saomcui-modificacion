/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 */
package com.ag.saomcui.client.map;

import com.ag.saomcui.client.SAOMainMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class MapUi {
    private MapUi() {
    }

    public static void show(Screen page) {
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen instanceof SAOMainMenuScreen) {
            SAOMainMenuScreen menu;
            menu.setMapContent(page == (menu = (SAOMainMenuScreen)screen) ? null : page);
        } else {
            mc.m_91152_(page);
        }
    }
}

