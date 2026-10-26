/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.chunk.ChunkStatus
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.client.event.RenderGuiEvent$Post
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.ag.saomcui.client.map;

import com.ag.saomcui.client.SAOMainMenuScreen;
import com.ag.saomcui.client.map.MapStore;
import com.ag.saomcui.map.Waypoint;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="saomcui", value={Dist.CLIENT})
public final class MapClient {
    public static final KeyMapping OPEN = new KeyMapping("key.saomcui.map", InputConstants.Type.KEYSYM, 77, "key.categories.saomcui");
    private static boolean connected;
    private static boolean wasDead;
    private static int tick;
    private static int scan;
    private static int scanRadius;
    private static final List<int[]> offsets;

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
        MapStore.disconnect();
        connected = false;
        wasDead = false;
        scan = 0;
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || mc.f_91073_ == null) {
            return;
        }
        if (!connected) {
            MapStore.connect();
            connected = true;
        }
        if (!MapStore.ready()) {
            return;
        }
        while (OPEN.m_90859_()) {
            if (mc.f_91080_ != null) continue;
            mc.m_91152_((Screen)SAOMainMenuScreen.forMap());
        }
        boolean dead = mc.f_91074_.m_21224_();
        if (dead && !wasDead) {
            MapStore.data.waypoints.removeIf(w -> w.deathMarker);
            Waypoint w2 = new Waypoint("Last death", MapClient.dimension(), mc.f_91074_.m_146903_(), mc.f_91074_.m_146904_(), mc.f_91074_.m_146907_());
            w2.color = 15756653;
            w2.icon = "X";
            w2.deathMarker = true;
            MapStore.add(w2);
        }
        wasDead = dead;
        if (++tick % 600 == 0) {
            MapStore.save();
        }
        if (mc.m_91104_()) {
            return;
        }
        int radius = Math.min(16, (Integer)mc.f_91066_.m_231984_().m_231551_());
        if (radius != scanRadius) {
            offsets.clear();
            scanRadius = radius;
            scan = 0;
            for (int z = -radius; z <= radius; ++z) {
                int x = -radius;
                while (x <= radius) {
                    offsets.add(new int[]{x++, z});
                }
            }
            offsets.sort(Comparator.comparingInt(p -> p[0] * p[0] + p[1] * p[1]));
        }
        long start = System.nanoTime();
        for (int i = 0; i < 2; ++i) {
            int[] offset = offsets.get(scan++ % offsets.size());
            MapClient.sample(mc, (mc.f_91074_.m_146903_() >> 4) + offset[0], (mc.f_91074_.m_146907_() >> 4) + offset[1]);
            if (System.nanoTime() - start > 2000000L) break;
        }
    }

    private static void sample(Minecraft mc, int cx, int cz) {
        LevelChunk chunk = mc.f_91073_.m_7726_().m_7587_(cx, cz, ChunkStatus.f_62326_, false);
        if (chunk == null) {
            return;
        }
        MapStore.Tile tile = new MapStore.Tile(MapClient.dimension(), cx, cz);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int z = 0; z < 16; ++z) {
            for (int x = 0; x < 16; ++x) {
                int y = chunk.m_5885_(Heightmap.Types.WORLD_SURFACE, x, z);
                pos.m_122178_(cx * 16 + x, y, cz * 16 + z);
                int rgb = mc.f_91073_.m_8055_((BlockPos)pos).m_284242_((BlockGetter)mc.f_91073_, (BlockPos)pos).f_283871_;
                float shade = 0.75f + 0.2f * Math.min(1.0f, Math.max(0.0f, ((float)y + 64.0f) / 384.0f));
                int r = (int)((float)(rgb >> 16 & 0xFF) * shade);
                int g = (int)((float)(rgb >> 8 & 0xFF) * shade);
                int b = (int)((float)(rgb & 0xFF) * shade);
                tile.colors[z * 16 + x] = 0xFF000000 | r << 16 | g << 8 | b;
            }
        }
        MapStore.Tile old = MapStore.data.tiles.get(tile.key());
        if (old != null && Arrays.equals(old.colors, tile.colors)) {
            return;
        }
        MapStore.putTile(tile);
    }

    public static String dimension() {
        return Minecraft.m_91087_().f_91073_.m_46472_().m_135782_().toString();
    }

    @SubscribeEvent
    public static void hud(RenderGuiEvent.Post e) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || mc.f_91066_.f_92062_ || mc.f_91080_ != null) {
            return;
        }
        Waypoint w = MapStore.data.waypoints.stream().filter(p -> p.id.equals(MapStore.trackedId) && p.visible && p.dimension.equals(MapClient.dimension())).findFirst().orElse(null);
        if (w == null) {
            return;
        }
        double dx = (double)w.x - mc.f_91074_.m_20185_();
        double dz = (double)w.z - mc.f_91074_.m_20189_();
        double angle = Math.toDegrees(Math.atan2(-dx, dz)) - (double)mc.f_91074_.m_146908_();
        String text = ((angle = Mth.m_14175_((double)angle)) < -15.0 ? "< " : (angle > 15.0 ? "> " : "^ ")) + w.name + "  " + (int)Math.hypot(dx, dz) + "m";
        int x = (mc.m_91268_().m_85445_() - mc.f_91062_.m_92895_(text)) / 2;
        e.getGuiGraphics().m_280509_(x - 8, 8, x + mc.f_91062_.m_92895_(text) + 8, 25, -1072159446);
        e.getGuiGraphics().m_280488_(mc.f_91062_, text, x, 13, 0xFF000000 | w.color);
    }

    static {
        scanRadius = -1;
        offsets = new ArrayList<int[]>();
    }

    @Mod.EventBusSubscriber(modid="saomcui", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class Keys {
        @SubscribeEvent
        public static void keys(RegisterKeyMappingsEvent e) {
            e.register(OPEN);
        }
    }
}

