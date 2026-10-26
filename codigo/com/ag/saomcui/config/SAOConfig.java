/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package com.ag.saomcui.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.file.Path;
import net.minecraftforge.fml.loading.FMLPaths;

public class SAOConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("saomcui.json");
    public static boolean enableCrafting = true;

    public static void load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (FileReader reader = new FileReader(CONFIG_PATH.toFile());){
                ConfigData data = (ConfigData)GSON.fromJson((Reader)reader, ConfigData.class);
                if (data != null) {
                    enableCrafting = data.enableCrafting;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            SAOConfig.save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile());){
            ConfigData data = new ConfigData();
            data.enableCrafting = enableCrafting;
            GSON.toJson((Object)data, (Appendable)writer);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ConfigData {
        public boolean enableCrafting = true;

        private ConfigData() {
        }
    }
}

