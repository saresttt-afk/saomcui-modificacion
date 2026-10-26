/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 */
package com.ag.saomcui.client.map;

import com.ag.saomcui.client.SAOUISounds;
import com.ag.saomcui.client.map.MapClient;
import com.ag.saomcui.client.map.MapStore;
import com.ag.saomcui.client.map.MapUi;
import com.ag.saomcui.client.map.WaypointScreen;
import com.ag.saomcui.map.MapProjection;
import com.ag.saomcui.map.Waypoint;
import com.ag.saomcui.network.WaypointPacket;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class SAOMapScreen
extends Screen {
    static final int ORANGE = -1336576;
    static final int INK = -13619152;
    static final int PAPER = -1;
    static final int HEADER = -1381654;
    static final int MUTED = -11184811;
    private int panelX;
    private int panelY;
    private float panelScale = 1.0f;
    private final Screen parent;
    private double centerX;
    private double centerZ;
    private double zoom = 1.0;
    private String dimension;
    private int mapRight;
    private int mapBottom;
    private int page;
    private EditBox search;
    private Waypoint selected;
    private DynamicTexture texture;
    private ResourceLocation textureId;
    private long rasterTime;
    private long lastClick;
    private double clickX;
    private double clickY;
    private boolean dragging;
    private List<Waypoint> shown = List.of();
    private Button inboxButton;

    public void setPanelTransform(int x, int y, float scale) {
        this.panelX = x;
        this.panelY = y;
        this.panelScale = scale;
    }

    public SAOMapScreen(Screen parent) {
        super((Component)Component.m_237113_((String)"SAO Field Map"));
        this.parent = parent;
        Minecraft mc = Minecraft.m_91087_();
        this.dimension = MapClient.dimension();
        this.centerX = mc.f_91074_.m_20185_();
        this.centerZ = mc.f_91074_.m_20189_();
    }

    public static Button button(int x, int y, int w, final String label, Runnable action) {
        return new Button(x, y, w, 20, (Component)Component.m_237113_((String)label), b -> action.run(), narration -> (MutableComponent)narration.get()){
            private boolean wasHovered;

            public void m_7435_(SoundManager manager) {
                SAOUISounds.play(label.equals("Back") || label.equals("Done") || label.equals("Cancel") ? "ui.back" : "ui.click");
            }

            public void m_87963_(GuiGraphics g, int mx, int my, float dt) {
                if (this.m_198029_() && !this.wasHovered && this.f_93623_) {
                    SAOUISounds.move();
                }
                this.wasHovered = this.m_198029_();
                g.m_280509_(this.m_252754_(), this.m_252907_(), this.m_252754_() + this.m_5711_(), this.m_252907_() + this.m_93694_(), this.m_198029_() ? -10366 : -1381654);
                g.m_280509_(this.m_252754_(), this.m_252907_(), this.m_252754_() + 2, this.m_252907_() + this.m_93694_(), -1336576);
                Font font = Minecraft.m_91087_().f_91062_;
                String label2 = font.m_92834_(this.m_6035_().getString(), this.m_5711_() - 10);
                g.m_280056_(font, label2, this.m_252754_() + (this.m_5711_() - font.m_92895_(label2)) / 2, this.m_252907_() + 6, this.f_93623_ ? -13619152 : -8947849, false);
            }
        };
    }

    protected void m_7856_() {
        if (this.selected != null) {
            this.selected = MapStore.data.waypoints.stream().filter(w -> w.id.equals(this.selected.id)).findFirst().orElse(null);
        }
        this.release();
        this.mapRight = this.f_96543_ - Math.min(160, this.f_96543_ / 3);
        this.mapBottom = this.f_96544_ - 34;
        String query = this.search == null ? "" : this.search.m_94155_();
        this.search = new EditBox(this.f_96547_, this.mapRight + 8, 31, this.f_96543_ - this.mapRight - 16, 18, (Component)Component.m_237113_((String)"Search waypoints"));
        this.search.m_257771_((Component)Component.m_237113_((String)"Search..."));
        this.search.m_94202_(-1);
        this.search.m_94205_(-2037010);
        this.search.m_94144_(query);
        this.search.m_94151_(s -> {
            this.page = 0;
        });
        this.m_142416_((GuiEventListener)this.search);
        this.m_142416_((GuiEventListener)SAOMapScreen.button(8, 5, 44, "Back", this::m_7379_));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(58, 5, 50, "Center", this::center));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(114, 5, 28, "+", () -> this.zoomAt((double)this.mapRight / 2.0, (double)(30 + this.mapBottom) / 2.0, 1.5)));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(146, 5, 28, "-", () -> this.zoomAt((double)this.mapRight / 2.0, (double)(30 + this.mapBottom) / 2.0, 0.6666666666666666)));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(180, 5, Math.max(60, this.f_96543_ - 188), this.shortDimension(), this::cycleDimension));
        int px = this.mapRight + 8;
        int pw = this.f_96543_ - px - 8;
        this.m_142416_((GuiEventListener)SAOMapScreen.button(px, this.f_96544_ - 128, pw, "+ Waypoint", () -> this.edit(null, (int)this.centerX, (int)this.centerZ)));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(px, this.f_96544_ - 104, pw, "Edit / Share", () -> {
            if (this.selected != null) {
                this.edit(this.selected, this.selected.x, this.selected.z);
            }
        }));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(px, this.f_96544_ - 80, pw, "Track target", () -> {
            if (this.selected != null) {
                MapStore.trackedId = this.selected.id.equals(MapStore.trackedId) ? null : this.selected.id;
            }
        }));
        this.inboxButton = (Button)this.m_142416_((GuiEventListener)SAOMapScreen.button(px, this.f_96544_ - 56, pw, "Inbox (" + MapStore.inbox.size() + ")", this::inbox));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(px, this.f_96544_ - 30, pw / 2 - 2, "<", () -> {
            this.page = Math.max(0, this.page - 1);
        }));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(px + pw / 2 + 2, this.f_96544_ - 30, pw / 2 - 2, ">", () -> {
            if ((this.page + 1) * this.rows() < this.filtered().size()) {
                ++this.page;
            }
        }));
        this.texture = new DynamicTexture(new NativeImage(256, 256, false));
        this.textureId = this.f_96541_.m_91097_().m_118490_("sao_field_map", this.texture);
        this.rasterTime = 0L;
    }

    private String shortDimension() {
        return this.dimension.substring(this.dimension.indexOf(58) + 1) + " >";
    }

    private int rows() {
        return Math.max(1, (this.f_96544_ - 188) / 22);
    }

    private List<Waypoint> filtered() {
        String q = this.search.m_94155_().toLowerCase(Locale.ROOT);
        return MapStore.data.waypoints.stream().filter(w -> w.dimension.equals(this.dimension) && w.name.toLowerCase(Locale.ROOT).contains(q)).sorted(Comparator.comparing(w -> w.name.toLowerCase(Locale.ROOT))).toList();
    }

    private void center() {
        boolean changed = !this.dimension.equals(MapClient.dimension());
        this.dimension = MapClient.dimension();
        this.centerX = this.f_96541_.f_91074_.m_20185_();
        this.centerZ = this.f_96541_.f_91074_.m_20189_();
        this.rasterTime = 0L;
        if (changed) {
            this.rebuild();
        }
    }

    private void cycleDimension() {
        TreeSet<String> dimensions = new TreeSet<String>();
        dimensions.add(MapClient.dimension());
        dimensions.addAll(MapStore.data.dimensions);
        MapStore.data.tiles.values().forEach(t -> dimensions.add(t.dimension));
        MapStore.data.waypoints.forEach(w -> dimensions.add(w.dimension));
        ArrayList values = new ArrayList(dimensions);
        this.dimension = (String)values.get((values.indexOf(this.dimension) + 1) % values.size());
        this.page = 0;
        this.selected = null;
        this.rasterTime = 0L;
        this.rebuild();
    }

    private void rebuild() {
        this.release();
        this.m_169413_();
        this.m_7856_();
    }

    private double worldX(double sx) {
        return MapProjection.world(sx, (double)this.mapRight / 2.0, this.centerX, this.zoom);
    }

    private double worldZ(double sy) {
        return MapProjection.world(sy, (double)(30 + this.mapBottom) / 2.0, this.centerZ, this.zoom);
    }

    private int screenX(double x) {
        return (int)MapProjection.screen(x, (double)this.mapRight / 2.0, this.centerX, this.zoom);
    }

    private int screenZ(double z) {
        return (int)MapProjection.screen(z, (double)(30 + this.mapBottom) / 2.0, this.centerZ, this.zoom);
    }

    private boolean inside(double x, double y) {
        return x >= 8.0 && x < (double)(this.mapRight - 6) && y >= 30.0 && y < (double)this.mapBottom;
    }

    private void zoomAt(double x, double y, double factor) {
        double wx = this.worldX(x);
        double wz = this.worldZ(y);
        this.zoom = MapProjection.clampZoom(this.zoom * factor);
        this.centerX += wx - this.worldX(x);
        this.centerZ += wz - this.worldZ(y);
        this.rasterTime = 0L;
    }

    private void raster() {
        NativeImage image = this.texture.m_117991_();
        for (int py = 0; py < 256; ++py) {
            for (int px = 0; px < 256; ++px) {
                int z;
                int x = (int)Math.floor(this.worldX(8.0 + (double)(px * (this.mapRight - 14)) / 256.0));
                MapStore.Tile t = MapStore.tile(this.dimension, x >> 4, (z = (int)Math.floor(this.worldZ(30.0 + (double)(py * (this.mapBottom - 30)) / 256.0))) >> 4);
                int color = t == null ? (((x >> 4) + (z >> 4)) % 2 == 0 ? -2829100 : -2171170) : t.colors[(z & 0xF) * 16 + (x & 0xF)];
                image.m_84988_(px, py, color & 0xFF00FF00 | (color & 0xFF) << 16 | color >> 16 & 0xFF);
            }
        }
        this.texture.m_117985_();
        this.rasterTime = System.currentTimeMillis();
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float dt) {
        int y;
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.inboxButton.m_93666_((Component)Component.m_237113_((String)("Inbox (" + MapStore.inbox.size() + ")")));
        g.m_280509_(0, 0, this.f_96543_, this.f_96544_, -1);
        g.m_280509_(1, 1, this.f_96543_ - 1, 28, -1381654);
        g.m_280509_(1, this.mapBottom, this.mapRight, this.f_96544_ - 1, -3092272);
        g.m_280509_(this.mapRight, 28, this.mapRight + 1, this.f_96544_ - 1, -3092272);
        if (MapStore.ready() && System.currentTimeMillis() - this.rasterTime > 200L) {
            this.raster();
        }
        g.m_280411_(this.textureId, 8, 30, this.mapRight - 14, this.mapBottom - 30, 0.0f, 0.0f, 256, 256, 256, 256);
        g.m_280588_((int)((float)this.panelX + 8.0f * this.panelScale), (int)((float)this.panelY + 30.0f * this.panelScale), (int)((float)this.panelX + (float)(this.mapRight - 6) * this.panelScale), (int)((float)this.panelY + (float)this.mapBottom * this.panelScale));
        for (Waypoint w : MapStore.data.waypoints) {
            if (!w.visible || !w.dimension.equals(this.dimension)) continue;
            int x = this.screenX(w.x);
            y = this.screenZ(w.z);
            int color = 0xFF000000 | w.color;
            int beamHeight = 16 + (int)(2.0 * Math.sin((double)System.currentTimeMillis() / 350.0));
            g.m_280509_(x - 4, y - beamHeight, x + 5, y, 0x44000000 | w.color);
            g.m_280509_(x - 1, y - beamHeight, x + 2, y, color);
            g.m_280509_(x, y - beamHeight, x + 1, y, -1);
            SAOMapScreen.ring(g, x, y, 6, color);
            g.m_280509_(x - 2, y - beamHeight - 2, x + 3, y - beamHeight + 2, -1);
            if (!this.inside(x, y) || w != this.selected && (Math.abs(mx - x) >= 8 || Math.abs(my - y) >= 8)) continue;
            String label = this.f_96547_.m_92834_(w.icon + " " + w.name, this.mapRight - 30);
            int lx = Math.max(11, Math.min(x + 10, this.mapRight - 11 - this.f_96547_.m_92895_(label)));
            int ly = Math.max(34, y - beamHeight - 12);
            g.m_280509_(lx - 3, ly - 3, lx + this.f_96547_.m_92895_(label) + 3, ly + 11, -11513776);
            g.m_280509_(lx - 2, ly - 2, lx + this.f_96547_.m_92895_(label) + 2, ly + 10, -1);
            g.m_280509_(lx - 2, ly + 9, lx + this.f_96547_.m_92895_(label) + 2, ly + 11, color);
            g.m_280056_(this.f_96547_, label, lx, ly, -13619152, false);
        }
        if (this.dimension.equals(MapClient.dimension())) {
            int x = this.screenX(this.f_96541_.f_91074_.m_20185_());
            int y2 = this.screenZ(this.f_96541_.f_91074_.m_20189_());
            SAOMapScreen.ring(g, x, y2, 6, -1336576);
            g.m_280509_(x - 1, y2 - 1, x + 2, y2 + 2, -1);
            double angle = Math.toRadians(this.f_96541_.f_91074_.m_146908_());
            g.m_280509_(x - (int)(Math.sin(angle) * 9.0) - 1, y2 + (int)(Math.cos(angle) * 9.0) - 1, x - (int)(Math.sin(angle) * 9.0) + 2, y2 + (int)(Math.cos(angle) * 9.0) + 2, -1336576);
            g.m_280509_(x - 12, y2 + 12, x + 13, y2 + 25, -11513776);
            g.m_280509_(x - 11, y2 + 13, x + 12, y2 + 24, -1);
            g.m_280056_(this.f_96547_, "YOU", x - 9, y2 + 14, -13619152, false);
        }
        g.m_280618_();
        g.m_280509_(8, this.mapBottom, this.mapRight - 6, this.mapBottom + 1, -1336576);
        g.m_280056_(this.f_96547_, this.f_96547_.m_92834_("N ^  " + (int)this.worldX(mx) + ", " + (int)this.worldZ(my) + "  " + String.format(Locale.ROOT, "%.2fx", this.zoom), this.mapRight - 16), 10, this.f_96544_ - 28, -13619152, false);
        g.m_280056_(this.f_96547_, "Drag: move / Wheel: zoom", 10, this.f_96544_ - 15, -13619152, false);
        List<Waypoint> all = this.filtered();
        this.page = Math.min(this.page, Math.max(0, (all.size() - 1) / this.rows()));
        this.shown = all.stream().skip((long)this.page * (long)this.rows()).limit(this.rows()).toList();
        for (int i = 0; i < this.shown.size(); ++i) {
            Waypoint w = this.shown.get(i);
            y = 55 + i * 22;
            g.m_280509_(this.mapRight + 6, y, this.f_96543_ - 6, y + 20, w == this.selected ? -7258 : -1381654);
            g.m_280509_(this.mapRight + 6, y, this.mapRight + 9, y + 20, 0xFF000000 | w.color);
            g.m_280056_(this.f_96547_, this.f_96547_.m_92834_((w.visible ? w.icon : "-") + " " + w.name, this.f_96543_ - this.mapRight - 22), this.mapRight + 13, y + 6, -13619152, false);
        }
        super.m_88315_(g, mx, my, dt);
        if (!MapStore.status.isEmpty()) {
            g.m_280509_(8, 30, this.mapRight - 6, 44, -1);
            g.m_280056_(this.f_96547_, this.f_96547_.m_92834_(MapStore.status, this.mapRight - 20), 12, 33, -13619152, false);
        }
    }

    public boolean m_6375_(double x, double y, int b) {
        long now;
        if (super.m_6375_(x, y, b)) {
            return true;
        }
        if (x >= (double)this.mapRight && y >= 55.0 && y < (double)(55 + this.shown.size() * 22)) {
            this.selected = this.shown.get((int)(y - 55.0) / 22);
            this.centerX = this.selected.x;
            this.centerZ = this.selected.z;
            this.rasterTime = 0L;
            return true;
        }
        if (!this.inside(x, y)) {
            return false;
        }
        this.search.m_93692_(false);
        if (b == 1) {
            Waypoint hit = MapStore.data.waypoints.stream().filter(w -> w.visible && w.dimension.equals(this.dimension) && Math.abs((double)this.screenX(w.x) - x) < 8.0 && Math.abs((double)this.screenZ(w.z) - y) < 8.0).findFirst().orElse(null);
            this.edit(hit, (int)Math.floor(this.worldX(x)), (int)Math.floor(this.worldZ(y)));
            return true;
        }
        if (b != 0) {
            return false;
        }
        for (Waypoint w2 : MapStore.data.waypoints) {
            if (!w2.visible || !w2.dimension.equals(this.dimension) || !(Math.abs((double)this.screenX(w2.x) - x) < 7.0) || !(Math.abs((double)this.screenZ(w2.z) - y) < 7.0)) continue;
            this.selected = w2;
            break;
        }
        if ((now = System.currentTimeMillis()) - this.lastClick < 300L && Math.hypot(x - this.clickX, y - this.clickY) < 5.0) {
            this.lastClick = 0L;
            this.edit(this.selected != null && Math.abs((double)this.screenX(this.selected.x) - x) < 7.0 && Math.abs((double)this.screenZ(this.selected.z) - y) < 7.0 ? this.selected : null, (int)Math.floor(this.worldX(x)), (int)Math.floor(this.worldZ(y)));
            return true;
        }
        this.lastClick = now;
        this.clickX = x;
        this.clickY = y;
        this.dragging = true;
        return true;
    }

    public boolean m_7979_(double x, double y, int b, double dx, double dy) {
        if (this.dragging && b == 0) {
            this.centerX -= dx / this.zoom;
            this.centerZ -= dy / this.zoom;
            this.lastClick = 0L;
            this.rasterTime = 0L;
            return true;
        }
        return super.m_7979_(x, y, b, dx, dy);
    }

    public boolean m_6348_(double x, double y, int b) {
        this.dragging = false;
        return super.m_6348_(x, y, b);
    }

    public boolean m_6050_(double x, double y, double delta) {
        if (this.inside(x, y)) {
            this.zoomAt(x, y, Math.pow(1.25, delta));
            return true;
        }
        if (x >= (double)this.mapRight && y >= 55.0 && y < (double)(this.f_96544_ - 128)) {
            this.page = Math.max(0, Math.min(Math.max(0, (this.filtered().size() - 1) / this.rows()), this.page - (int)Math.signum(delta)));
            return true;
        }
        return super.m_6050_(x, y, delta);
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (!this.search.m_93696_() && key == 32) {
            this.center();
            return true;
        }
        if (!this.search.m_93696_() && this.OPEN_KEY(key, scan)) {
            this.m_7379_();
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    private boolean OPEN_KEY(int key, int scan) {
        return MapClient.OPEN.m_90832_(key, scan);
    }

    private static void ring(GuiGraphics g, int x, int y, int radius, int color) {
        for (int i = 0; i < 24; ++i) {
            double a = (double)i * Math.PI / 12.0;
            int px = x + (int)Math.round(Math.cos(a) * (double)radius);
            int py = y + (int)Math.round(Math.sin(a) * (double)radius);
            g.m_280509_(px, py, px + 1, py + 1, color);
        }
    }

    private void edit(Waypoint w, int x, int z) {
        if (!MapStore.ready()) {
            return;
        }
        Waypoint draft = w == null ? new Waypoint("Waypoint", this.dimension, x, this.f_96541_.f_91074_.m_146904_(), z) : w.copy();
        MapUi.show(new WaypointScreen(this, draft, w, false));
    }

    private void inbox() {
        Waypoint w = MapStore.inbox.peekFirst();
        if (w != null) {
            MapUi.show(new WaypointScreen(this, w.copy(), null, true));
        }
    }

    public static void receive(WaypointPacket.Delivery msg) {
        if (!msg.waypoint.valid()) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        if (MapStore.inbox.size() >= 32) {
            mc.f_91074_.m_213846_((Component)Component.m_237113_((String)"Waypoint inbox full (32). Open Map to review invitations."));
            return;
        }
        MapStore.inbox.addLast(msg.waypoint);
        mc.f_91074_.m_213846_((Component)Component.m_237113_((String)(msg.target + " shared '" + msg.waypoint.name + "'. Open Map > Inbox to accept or decline.")));
    }

    private void release() {
        if (this.textureId != null) {
            this.f_96541_.m_91097_().m_118513_(this.textureId);
            this.textureId = null;
            this.texture = null;
        }
    }

    public void m_7861_() {
        this.release();
        MapStore.save();
    }

    public void m_7379_() {
        MapUi.show(this.parent);
    }

    public boolean m_7043_() {
        return false;
    }
}

