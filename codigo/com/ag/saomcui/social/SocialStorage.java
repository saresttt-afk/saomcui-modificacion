/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.level.storage.LevelResource
 */
package com.ag.saomcui.social;

import com.ag.saomcui.social.SocialManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

public class SocialStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static MinecraftServer activeServer = null;

    public static void setServer(MinecraftServer server) {
        activeServer = server;
    }

    private static Path getSaveFile() {
        if (activeServer == null) {
            return null;
        }
        return activeServer.m_129843_(LevelResource.f_78182_).resolve("saomcui_social.json");
    }

    public static void load(MinecraftServer server) {
        SocialStorage.setServer(server);
        Path savePath = SocialStorage.getSaveFile();
        if (savePath != null && savePath.toFile().exists()) {
            try (FileReader reader = new FileReader(savePath.toFile());){
                SocialData data = (SocialData)GSON.fromJson((Reader)reader, SocialData.class);
                if (data != null) {
                    SocialManager.loadData(data.guilds, data.friends);
                }
            }
            catch (Exception e) {
                System.out.println("[SAOMCUI] Failed to load social data: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        Path savePath = SocialStorage.getSaveFile();
        if (savePath != null) {
            SocialData data = new SocialData();
            data.guilds = SocialManager.getAllGuilds();
            data.friends = SocialManager.getAllFriends();
            try (FileWriter writer = new FileWriter(savePath.toFile());){
                GSON.toJson((Object)data, (Appendable)writer);
            }
            catch (Exception e) {
                System.out.println("[SAOMCUI] Failed to save social data: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static class SocialData {
        public List<SocialManager.Guild> guilds = new ArrayList<SocialManager.Guild>();
        public Map<UUID, Set<UUID>> friends = new HashMap<UUID, Set<UUID>>();
    }
}

