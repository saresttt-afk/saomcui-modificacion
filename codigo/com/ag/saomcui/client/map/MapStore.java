/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.level.storage.LevelResource
 */
package com.ag.saomcui.client.map;

import com.ag.saomcui.map.Waypoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelResource;

public final class MapStore {
    private static final Gson JSON = new GsonBuilder().create();
    private static final ExecutorService IO = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "SAO map storage");
        t.setDaemon(true);
        return t;
    });
    public static Data data;
    public static String status;
    public static String trackedId;
    public static final Deque<Waypoint> inbox;
    private static Path file;
    private static boolean dirty;
    private static CompletableFuture<Data> loading;
    private static final Set<String> missing;
    private static final Set<String> pending;
    private static final Map<String, Tile> dirtyTiles;
    private static long generation;

    private static Path tilePath(Path root, String dimension, int x, int z) {
        String folder = UUID.nameUUIDFromBytes(dimension.getBytes(StandardCharsets.UTF_8)).toString();
        return root.getParent().resolve("terrain").resolve(folder).resolve(x + "_" + z + ".json");
    }

    public static Tile tile(String dimension, int x, int z) {
        String key = dimension + ":" + x + ":" + z;
        Tile tile = MapStore.data.tiles.get(key);
        if (tile != null || file == null || missing.contains(key) || pending.contains(key) || pending.size() >= 32) {
            return tile;
        }
        pending.add(key);
        Path source = MapStore.tilePath(file, dimension, x, z);
        long session = generation;
        IO.execute(() -> {
            Tile found = null;
            if (Files.exists(source, new LinkOption[0])) {
                try (BufferedReader reader2 = Files.newBufferedReader(source);){
                    Tile value = (Tile)JSON.fromJson((Reader)reader2, Tile.class);
                    if (value != null && key.equals(value.key()) && value.colors != null && value.colors.length == 256) {
                        found = value;
                    }
                }
                catch (Exception reader2) {
                    // empty catch block
                }
            }
            Tile loaded = found;
            Minecraft.m_91087_().execute(() -> {
                if (session != generation) {
                    return;
                }
                pending.remove(key);
                if (loaded != null) {
                    MapStore.data.tiles.putIfAbsent(key, loaded);
                    MapStore.trim();
                } else {
                    if (missing.size() >= 8192) {
                        missing.clear();
                    }
                    missing.add(key);
                }
            });
        });
        return null;
    }

    public static void putTile(Tile tile) {
        MapStore.data.tiles.put(tile.key(), tile);
        dirtyTiles.put(tile.key(), tile);
        missing.remove(tile.key());
        MapStore.data.dimensions.add(tile.dimension);
        dirty = true;
        MapStore.trim();
    }

    private static void trim() {
        while (MapStore.data.tiles.size() > 4096) {
            MapStore.data.tiles.remove(MapStore.data.tiles.keySet().iterator().next());
        }
        if (dirtyTiles.size() > 512) {
            MapStore.save();
        }
    }

    public static void connect() {
        ++generation;
        Minecraft mc = Minecraft.m_91087_();
        String identity = mc.m_91092_() != null ? "local:" + String.valueOf(mc.m_91092_().m_129843_(LevelResource.f_78182_).toAbsolutePath().normalize()) : "server:" + (mc.m_91089_() == null ? "unknown" : mc.m_91089_().f_105363_.toLowerCase(Locale.ROOT));
        try {
            String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(identity.getBytes(StandardCharsets.UTF_8)));
            Path source = file = mc.f_91069_.toPath().resolve("saomcui/maps").resolve(hash).resolve("map.json");
            status = "Loading map...";
            loading = CompletableFuture.supplyAsync(() -> {
                Data data;
                block11: {
                    if (!Files.exists(source, new LinkOption[0])) {
                        return new Data();
                    }
                    BufferedReader reader = Files.newBufferedReader(source);
                    try {
                        Data d = (Data)JSON.fromJson((Reader)reader, Data.class);
                        if (d == null || d.waypoints == null) {
                            throw new IllegalStateException("Invalid map file");
                        }
                        d.tiles = new LinkedHashMap();
                        if (d.dimensions == null) {
                            d.dimensions = new TreeSet<String>();
                        }
                        d.waypoints.removeIf(w -> w == null || !w.valid());
                        data = d;
                        if (reader == null) break block11;
                    }
                    catch (Throwable throwable) {
                        try {
                            if (reader != null) {
                                try {
                                    reader.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (Exception e) {
                            throw new CompletionException(e);
                        }
                    }
                    reader.close();
                }
                return data;
            }, IO);
        }
        catch (Exception e) {
            status = "Map storage unavailable";
            file = null;
        }
    }

    public static boolean ready() {
        if (loading != null) {
            if (!loading.isDone()) {
                return false;
            }
            try {
                data = loading.join();
                status = "";
            }
            catch (Exception e) {
                status = "Map could not load; original file preserved";
                file = null;
            }
            loading = null;
        }
        return true;
    }

    public static void changed() {
        dirty = true;
    }

    public static boolean add(Waypoint w) {
        if (!w.valid() || MapStore.data.waypoints.size() >= 512) {
            status = "Waypoint limit: 512";
            return false;
        }
        MapStore.data.waypoints.add(w);
        MapStore.changed();
        MapStore.save();
        return true;
    }

    public static void save() {
        if (!dirty || file == null || loading != null) {
            return;
        }
        String snapshot = JSON.toJson((Object)data);
        Path destination = file;
        ArrayList<Tile> terrain = new ArrayList<Tile>(dirtyTiles.values());
        dirtyTiles.clear();
        dirty = false;
        IO.execute(() -> {
            try {
                for (Tile tile : terrain) {
                    MapStore.atomicWrite(MapStore.tilePath(destination, tile.dimension, tile.x, tile.z), JSON.toJson((Object)tile));
                }
                Files.createDirectories(destination.getParent(), new FileAttribute[0]);
                Path temp = destination.resolveSibling("map.json.tmp");
                Files.writeString(temp, (CharSequence)snapshot, StandardCharsets.UTF_8, new OpenOption[0]);
                try {
                    Files.move(temp, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                }
                catch (AtomicMoveNotSupportedException e) {
                    Files.move(temp, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            catch (Exception e) {
                Minecraft.m_91087_().execute(() -> {
                    status = "Map save failed: check disk space / permissions";
                    if (destination.equals(file)) {
                        dirty = true;
                        terrain.forEach(t -> dirtyTiles.putIfAbsent(t.key(), (Tile)t));
                    }
                });
            }
        });
    }

    public static void disconnect() {
        MapStore.save();
        ++generation;
        file = null;
        loading = null;
        dirty = false;
        data = new Data();
        inbox.clear();
        trackedId = null;
        dirtyTiles.clear();
        missing.clear();
        pending.clear();
        status = "";
    }

    private static void atomicWrite(Path destination, String json) throws IOException {
        Files.createDirectories(destination.getParent(), new FileAttribute[0]);
        Path temp = destination.resolveSibling(String.valueOf(destination.getFileName()) + ".tmp");
        Files.writeString(temp, (CharSequence)json, StandardCharsets.UTF_8, new OpenOption[0]);
        try {
            Files.move(temp, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            IO.shutdown();
            try {
                IO.awaitTermination(5L, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "SAO map save shutdown"));
        data = new Data();
        status = "";
        inbox = new ArrayDeque<Waypoint>();
        missing = new HashSet<String>();
        pending = new HashSet<String>();
        dirtyTiles = new LinkedHashMap<String, Tile>();
    }

    public static class Data {
        public List<Waypoint> waypoints = new ArrayList<Waypoint>();
        public transient LinkedHashMap<String, Tile> tiles = new LinkedHashMap();
        public Set<String> dimensions = new TreeSet<String>();
    }

    public static class Tile {
        public String dimension;
        public int x;
        public int z;
        public int[] colors = new int[256];

        public Tile(String d, int x, int z) {
            this.dimension = d;
            this.x = x;
            this.z = z;
        }

        public String key() {
            return this.dimension + ":" + this.x + ":" + this.z;
        }
    }
}

