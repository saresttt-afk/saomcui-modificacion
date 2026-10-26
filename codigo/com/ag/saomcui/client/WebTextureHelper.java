/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.HttpTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.resources.ResourceLocation
 */
package com.ag.saomcui.client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class WebTextureHelper {
    private static final Map<String, ResourceLocation> urlToLocation = new HashMap<String, ResourceLocation>();

    public static ResourceLocation getOrCreateWebTexture(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        if (urlToLocation.containsKey(url)) {
            return urlToLocation.get(url);
        }
        String id = "web_" + Integer.toHexString(url.hashCode());
        ResourceLocation loc = new ResourceLocation("saomcui", "textures/web/" + id);
        TextureManager manager = Minecraft.m_91087_().m_91097_();
        File cacheFile = new File(Minecraft.m_91087_().f_91069_, "sao_cache/textures/" + id + ".png");
        HttpTexture texture = new HttpTexture(cacheFile, url, new ResourceLocation("saomcui:textures/gui/under_icons/guild.png"), false, null);
        manager.m_118495_(loc, (AbstractTexture)texture);
        urlToLocation.put(url, loc);
        return loc;
    }
}

