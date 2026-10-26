/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.logging.LogUtils
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ag.saomcui.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class SAOHealthModel {
    private static List<Face> faces;
    private static boolean attempted;

    private static void load() {
        attempted = true;
        try (InputStream in = Minecraft.m_91087_().m_91098_().m_215595_(new ResourceLocation("saomcui", "models/effects/health_bar.glb"));){
            faces = SAOHealthModel.decode(in.readAllBytes());
        }
        catch (Exception e) {
            LogUtils.getLogger().error("Cannot load SAO healthbar model", (Throwable)e);
            faces = List.of();
        }
    }

    public static int validateModel(byte[] bytes) {
        return SAOHealthModel.decode(bytes).size();
    }

    private static List<Face> decode(byte[] bytes) {
        ByteBuffer file = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        if (file.getInt() != 1179937895 || file.getInt() != 2) {
            throw new IllegalArgumentException("GLB header");
        }
        file.getInt();
        int jsonSize = file.getInt();
        file.getInt();
        byte[] jsonBytes = new byte[jsonSize];
        file.get(jsonBytes);
        JsonObject json = JsonParser.parseString((String)new String(jsonBytes, StandardCharsets.UTF_8)).getAsJsonObject();
        int binSize = file.getInt();
        file.getInt();
        byte[] binary = new byte[binSize];
        file.get(binary);
        ByteBuffer bin = ByteBuffer.wrap(binary).order(ByteOrder.LITTLE_ENDIAN);
        ArrayList<Face> raw = new ArrayList<Face>();
        for (JsonElement node : json.getAsJsonArray("scenes").get(0).getAsJsonObject().getAsJsonArray("nodes")) {
            SAOHealthModel.walk(json, bin, node.getAsInt(), new Matrix4f(), raw);
        }
        float minX = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float minY = minX;
        float maxY = maxX;
        float minZ = minX;
        float maxZ = maxX;
        for (Face f : raw) {
            for (Vector3f v : new Vector3f[]{f.a, f.b, f.c}) {
                minX = Math.min(minX, v.x);
                maxX = Math.max(maxX, v.x);
                minY = Math.min(minY, v.y);
                maxY = Math.max(maxY, v.y);
                minZ = Math.min(minZ, v.z);
                maxZ = Math.max(maxZ, v.z);
            }
        }
        float width = maxX - minX;
        float cx = (minX + maxX) / 2.0f;
        float cy = (minY + maxY) / 2.0f;
        float cz = (minZ + maxZ) / 2.0f;
        if (!Float.isFinite(width) || width <= 0.0f) {
            throw new IllegalArgumentException("Empty model");
        }
        ArrayList<Face> result = new ArrayList<Face>();
        for (Face f : raw) {
            Vector3f a = new Vector3f((Vector3fc)f.a).sub(cx, cy, cz).div(width);
            Vector3f b = new Vector3f((Vector3fc)f.b).sub(cx, cy, cz).div(width);
            Vector3f c = new Vector3f((Vector3fc)f.c).sub(cx, cy, cz).div(width);
            Vector3f n = new Vector3f((Vector3fc)b).sub((Vector3fc)a).cross((Vector3fc)new Vector3f((Vector3fc)c).sub((Vector3fc)a));
            float shade = n.lengthSquared() > 0.0f ? 0.55f + 0.45f * Math.abs(n.normalize().dot((Vector3fc)new Vector3f(0.2f, 0.8f, 0.55f).normalize())) : 1.0f;
            result.add(new Face(a, b, c, f.fill, shade));
        }
        return result;
    }

    private static int integer(JsonObject obj, String name, int fallback) {
        return obj.has(name) ? obj.get(name).getAsInt() : fallback;
    }

    private static int offset(JsonObject json, int accessor) {
        JsonObject a = json.getAsJsonArray("accessors").get(accessor).getAsJsonObject();
        JsonObject v = json.getAsJsonArray("bufferViews").get(a.get("bufferView").getAsInt()).getAsJsonObject();
        return SAOHealthModel.integer(a, "byteOffset", 0) + SAOHealthModel.integer(v, "byteOffset", 0);
    }

    private static Vector3f position(JsonObject json, ByteBuffer bin, int accessor, int index, Matrix4f transform) {
        JsonObject a = json.getAsJsonArray("accessors").get(accessor).getAsJsonObject();
        JsonObject view = json.getAsJsonArray("bufferViews").get(a.get("bufferView").getAsInt()).getAsJsonObject();
        int start = SAOHealthModel.offset(json, accessor) + index * SAOHealthModel.integer(view, "byteStride", 12);
        return transform.transformPosition(new Vector3f(bin.getFloat(start), bin.getFloat(start + 4), bin.getFloat(start + 8)));
    }

    private static void walk(JsonObject json, ByteBuffer bin, int index, Matrix4f parent, List<Face> out) {
        JsonObject node = json.getAsJsonArray("nodes").get(index).getAsJsonObject();
        Matrix4f transform = new Matrix4f((Matrix4fc)parent);
        if (node.has("matrix")) {
            float[] values = new float[16];
            for (int i = 0; i < 16; ++i) {
                values[i] = node.getAsJsonArray("matrix").get(i).getAsFloat();
            }
            transform.mul((Matrix4fc)new Matrix4f().set(values));
        }
        if (node.has("mesh")) {
            for (JsonElement entry : json.getAsJsonArray("meshes").get(node.get("mesh").getAsInt()).getAsJsonObject().getAsJsonArray("primitives")) {
                JsonObject p = entry.getAsJsonObject();
                int positions = p.getAsJsonObject("attributes").get("POSITION").getAsInt();
                int indices = p.get("indices").getAsInt();
                JsonObject a = json.getAsJsonArray("accessors").get(indices).getAsJsonObject();
                int base = SAOHealthModel.offset(json, indices);
                int size = a.get("componentType").getAsInt() == 5125 ? 4 : 2;
                for (int i = 0; i < a.get("count").getAsInt(); i += 3) {
                    Vector3f[] vs = new Vector3f[3];
                    for (int j = 0; j < 3; ++j) {
                        int id = size == 4 ? bin.getInt(base + (i + j) * 4) : Short.toUnsignedInt(bin.getShort(base + (i + j) * 2));
                        vs[j] = SAOHealthModel.position(json, bin, positions, id, transform);
                    }
                    out.add(new Face(vs[0], vs[1], vs[2], SAOHealthModel.integer(p, "material", 0) == 1, 1.0f));
                }
            }
        }
        if (node.has("children")) {
            for (JsonElement child : node.getAsJsonArray("children")) {
                SAOHealthModel.walk(json, bin, child.getAsInt(), transform, out);
            }
        }
    }

    public static void render(Matrix4f matrix, float fraction) {
        if (!attempted) {
            SAOHealthModel.load();
        }
        if (faces.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::m_172811_);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        BufferBuilder builder = Tesselator.m_85913_().m_85915_();
        builder.m_166779_(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.f_85815_);
        for (Face f : faces) {
            if (!f.fill) {
                SAOHealthModel.emit(builder, matrix, f.a, f.b, f.c, 15266293, f.shade, 0.0f);
                continue;
            }
            float cutoff = 0.5f - Math.max(0.0f, Math.min(1.0f, fraction));
            List<Vector3f> triangle = List.of(f.a, f.b, f.c);
            List<Vector3f> empty = SAOHealthModel.clip(triangle, cutoff);
            List<Vector3f> filled = SAOHealthModel.clipSide(triangle, cutoff, false);
            int i = 1;
            while (i + 1 < empty.size()) {
                SAOHealthModel.emit(builder, matrix, empty.get(0), empty.get(i), empty.get(i + 1), 2503226, f.shade, 0.0f);
                ++i;
            }
            i = 1;
            while (i + 1 < filled.size()) {
                SAOHealthModel.emit(builder, matrix, filled.get(0), filled.get(i), filled.get(i + 1), SAOHealthModel.healthColor(fraction), 0.85f + 0.15f * f.shade, 0.0f);
                ++i;
            }
        }
        Tesselator.m_85913_().m_85914_();
        RenderSystem.enableCull();
    }

    public static Matrix4f shoulderTransform(Matrix4f origin, Quaternionf camera, float width, float height) {
        float shoulder = height - Math.min(0.25f, height * 0.18f);
        float offset = width * 0.5f + 0.18f - 0.425f;
        return new Matrix4f((Matrix4fc)origin).translate(0.0f, shoulder, 0.0f).rotate((Quaternionfc)camera).scale(-1.0f, 1.0f, -1.0f).translate(offset, 0.0f, 0.0f);
    }

    public static Vector3f ribbonPoint(float u, float y, float radius) {
        float angle = (float)Math.toRadians(-40.0f + 80.0f * u + y * 45.0f);
        return new Vector3f(radius * (0.95f + 0.65f * (float)Math.sin(angle)), y, radius * (0.15f + 0.65f * (float)Math.cos(angle)));
    }

    public static void renderRibbon(Matrix4f matrix, float fraction, float radius) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::m_172811_);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        BufferBuilder b = Tesselator.m_85913_().m_85915_();
        b.m_166779_(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.f_85815_);
        float hp = Math.max(0.0f, Math.min(1.0f, fraction));
        for (int i = 0; i < 64; ++i) {
            float a = (float)i / 64.0f;
            float c = (float)(i + 1) / 64.0f;
            SAOHealthModel.ribbonQuad(b, matrix, a, c, 0.067f, 0.08f, radius, 15595002);
            SAOHealthModel.ribbonQuad(b, matrix, a, c, -0.08f, -0.067f, radius, 15595002);
            if (i == 0 || i == 63) {
                SAOHealthModel.ribbonQuad(b, matrix, a, c, -0.067f, 0.067f, radius, 15595002);
                continue;
            }
            float split = Math.max(a, Math.min(c, 0.015625f + hp * 62.0f / 64.0f));
            if (split > a) {
                SAOHealthModel.ribbonQuad(b, matrix, a, split, -0.067f, 0.067f, radius, SAOHealthModel.healthColor(hp));
            }
            if (!(split < c)) continue;
            SAOHealthModel.ribbonQuad(b, matrix, split, c, -0.067f, 0.067f, radius, 2569277);
        }
        Tesselator.m_85913_().m_85914_();
        RenderSystem.enableCull();
    }

    private static void ribbonQuad(BufferBuilder b, Matrix4f m, float a, float c, float low, float high, float radius, int color) {
        Vector3f p = SAOHealthModel.ribbonPoint(a, low, radius);
        Vector3f q = SAOHealthModel.ribbonPoint(c, low, radius);
        Vector3f r = SAOHealthModel.ribbonPoint(c, high, radius);
        Vector3f s = SAOHealthModel.ribbonPoint(a, high, radius);
        SAOHealthModel.emit(b, m, p, q, r, color, 1.0f, 0.0f);
        SAOHealthModel.emit(b, m, p, r, s, color, 1.0f, 0.0f);
    }

    public static int healthColor(float fraction) {
        return fraction <= 0.25f ? 15672892 : (fraction <= 0.5f ? 15325236 : 9889067);
    }

    public static List<Vector3f> clip(List<Vector3f> input, float cutoff) {
        return SAOHealthModel.clipSide(input, cutoff, true);
    }

    private static List<Vector3f> clipSide(List<Vector3f> input, float cutoff, boolean left) {
        ArrayList<Vector3f> result = new ArrayList<Vector3f>();
        if (input.isEmpty()) {
            return result;
        }
        Vector3f previous = input.get(input.size() - 1);
        for (Vector3f current : input) {
            boolean b;
            boolean a;
            boolean bl = left ? previous.x <= cutoff : (a = previous.x >= cutoff);
            boolean bl2 = left ? current.x <= cutoff : (b = current.x >= cutoff);
            if (a != b) {
                result.add(new Vector3f((Vector3fc)previous).lerp((Vector3fc)current, (cutoff - previous.x) / (current.x - previous.x)));
            }
            if (b) {
                result.add(current);
            }
            previous = current;
        }
        return result;
    }

    private static void emit(BufferBuilder b, Matrix4f m, Vector3f a, Vector3f c, Vector3f d, int rgb, float shade, float offset) {
        for (Vector3f v : new Vector3f[]{a, c, d}) {
            b.m_252986_(m, v.x, v.y, v.z + offset).m_6122_((int)((float)(rgb >> 16 & 0xFF) * shade), (int)((float)(rgb >> 8 & 0xFF) * shade), (int)((float)(rgb & 0xFF) * shade), 255).m_5752_();
        }
    }

    private record Face(Vector3f a, Vector3f b, Vector3f c, boolean fill, float shade) {
    }
}

