/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.mojang.logging.LogUtils
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package com.ag.saomcui.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.minecraftforge.fml.loading.FMLPaths;

public final class SAOClientPreferences {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Data data;

    private static Path file() {
        return FMLPaths.CONFIGDIR.get().resolve("saomcui-ui.json");
    }

    private SAOClientPreferences() {
    }

    public static float scale() {
        if (data == null) {
            data = new Data();
            if (Files.exists(SAOClientPreferences.file(), new LinkOption[0])) {
                try (BufferedReader reader = Files.newBufferedReader(SAOClientPreferences.file());){
                    Data loaded = (Data)GSON.fromJson((Reader)reader, Data.class);
                    if (loaded != null) {
                        data = loaded;
                    }
                }
                catch (Exception e) {
                    LogUtils.getLogger().warn("Could not load SAO UI preferences", (Throwable)e);
                }
            }
            SAOClientPreferences.setScale(SAOClientPreferences.data.menuScale);
        }
        return SAOClientPreferences.data.menuScale;
    }

    public static void setScale(float value) {
        if (data == null) {
            data = new Data();
        }
        SAOClientPreferences.data.menuScale = Float.isFinite(value) ? Math.max(0.75f, Math.min(1.5f, value)) : 1.0f;
    }

    public static void save() {
        SAOClientPreferences.scale();
        try {
            Files.createDirectories(SAOClientPreferences.file().getParent(), new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(SAOClientPreferences.file(), new OpenOption[0]);){
                GSON.toJson((Object)data, (Appendable)writer);
            }
        }
        catch (Exception e) {
            LogUtils.getLogger().warn("Could not save SAO UI preferences", (Throwable)e);
        }
    }

    public static boolean healthBars() {
        SAOClientPreferences.scale();
        return SAOClientPreferences.data.healthBars;
    }

    public static boolean floatingMenu() {
        SAOClientPreferences.scale();
        return SAOClientPreferences.data.floatingMenu;
    }

    public static void setFloatingMenu(boolean enabled) {
        SAOClientPreferences.scale();
        SAOClientPreferences.data.floatingMenu = enabled;
    }

    public static void setHealthBars(boolean enabled) {
        SAOClientPreferences.scale();
        SAOClientPreferences.data.healthBars = enabled;
    }

    private static class Data {
        float menuScale = 1.0f;
        boolean healthBars = true;
        boolean floatingMenu = true;

        private Data() {
        }
    }
}

