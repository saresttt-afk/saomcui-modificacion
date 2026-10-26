/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 */
package com.ag.saomcui.client;

import com.ag.saomcui.client.SAOMainMenuScreen;
import com.ag.saomcui.inventory.SAOInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class SAOClientOnly {
    public static void openMainMenu(boolean as3x3) {
        SAOMainMenuScreen.openAs3x3 = as3x3;
        Minecraft.m_91087_().m_91152_((Screen)new SAOMainMenuScreen());
    }

    public static void handleInventorySync(CompoundTag data) {
        if (Minecraft.m_91087_().f_91074_ != null) {
            Minecraft.m_91087_().f_91074_.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> inv.deserializeNBT(data));
        }
    }

    public static void handleChatMessage(String senderName, String message, String channelName, String targetTag) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ != null && !senderName.equals(mc.f_91074_.m_7755_().getString())) {
            String prefix = "";
            if (channelName.equals("PARTY")) {
                prefix = "[Party] ";
            }
            if (channelName.equals("GUILD")) {
                prefix = "[Guild] ";
            }
            if (channelName.equals("PRIVATE")) {
                prefix = "[Private] ";
            }
            mc.f_91074_.m_213846_((Component)Component.m_237113_((String)("\u00a7e" + prefix + "Message from " + senderName)));
        }
    }
}

