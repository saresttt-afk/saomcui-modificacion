/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  org.joml.Matrix4f
 */
package com.ag.saomcui.client;

import com.ag.saomcui.client.SAOFloatMotion;
import com.ag.saomcui.client.SAOMenuPresence;
import com.ag.saomcui.client.SAOText;
import com.ag.saomcui.network.MenuActivityPacket;
import com.ag.saomcui.network.PublicMenuView;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

final class SAOHologramRenderer {
    private static ResourceLocation white;
    private static final MultiBufferSource.BufferSource BUFFER;
    private static final int INK = -13619152;
    private static final int PAPER = -285212673;
    private static final int ORANGE = -1336576;
    private static final String[] ROOT;
    private static final String[] PROFILE;
    private final PoseStack pose;
    private final Minecraft mc = Minecraft.m_91087_();
    private final float opacity;

    private SAOHologramRenderer(PoseStack pose, float opacity) {
        this.pose = pose;
        this.opacity = opacity;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void render(RenderLevelStageEvent event, Player player, SAOMenuPresence.Presence presence, long now) {
        if (white == null) {
            NativeImage pixels = new NativeImage(1, 1, false);
            pixels.m_84988_(0, 0, -1);
            white = Minecraft.m_91087_().m_91097_().m_118490_("sao_hologram_white", new DynamicTexture(pixels));
        }
        PoseStack pose = event.getPoseStack();
        float dt = event.getPartialTick();
        Vec3 camera = event.getCamera().m_90583_();
        float yaw = Mth.m_14189_((float)dt, (float)player.f_20884_, (float)player.f_20883_);
        double angle = Math.toRadians(yaw);
        double forwardX = -Math.sin(angle);
        double forwardZ = Math.cos(angle);
        double x = Mth.m_14139_((double)dt, (double)player.f_19854_, (double)player.m_20185_()) + forwardX * 1.05;
        double z = Mth.m_14139_((double)dt, (double)player.f_19856_, (double)player.m_20189_()) + forwardZ * 1.05;
        float open = SAOFloatMotion.ease((float)(now - presence.opened) / 300.0f);
        float close = presence.closed == 0L ? 0.0f : SAOFloatMotion.ease((float)(now - presence.closed) / 300.0f);
        pose.m_85836_();
        try {
            pose.m_85837_(x - camera.f_82479_, Mth.m_14139_((double)dt, (double)player.f_19855_, (double)player.m_20186_()) + 1.35 - camera.f_82480_ + (double)close * 0.9, z - camera.f_82481_);
            pose.m_252781_(Axis.f_252436_.m_252977_(180.0f - yaw));
            if ((camera.f_82479_ - x) * forwardX + (camera.f_82481_ - z) * forwardZ > 0.0) {
                pose.m_252781_(Axis.f_252436_.m_252977_(180.0f));
            }
            pose.m_85841_(0.0027f, -0.0027f, 0.0027f);
            pose.m_252880_(-100.0f, (1.0f - open) * 22.0f, 0.0f);
            new SAOHologramRenderer(pose, open * (1.0f - close)).draw(player, presence, now);
            BUFFER.m_109911_();
        }
        finally {
            pose.m_85849_();
        }
    }

    private int alpha(int color) {
        return Math.round((float)(color >>> 24) * this.opacity) << 24 | color & 0xFFFFFF;
    }

    private void quad(ResourceLocation tex, float x, float y, float w, float h, float z, int color) {
        VertexConsumer b = BUFFER.m_6299_(RenderType.m_110497_((ResourceLocation)tex));
        Matrix4f m = this.pose.m_85850_().m_252922_();
        int c = this.alpha(color);
        b.m_252986_(m, x, y + h, z).m_193479_(c).m_7421_(0.0f, 1.0f).m_85969_(0xF000F0).m_5752_();
        b.m_252986_(m, x + w, y + h, z).m_193479_(c).m_7421_(1.0f, 1.0f).m_85969_(0xF000F0).m_5752_();
        b.m_252986_(m, x + w, y, z).m_193479_(c).m_7421_(1.0f, 0.0f).m_85969_(0xF000F0).m_5752_();
        b.m_252986_(m, x, y, z).m_193479_(c).m_7421_(0.0f, 0.0f).m_85969_(0xF000F0).m_5752_();
    }

    private void rect(float x, float y, float w, float h, int color) {
        this.quad(white, x, y, w, h, 0.0f, color);
    }

    private void text(String text, float x, float y, int color, int width) {
        String fitted = SAOText.fit(text, width, arg_0 -> ((Font)this.mc.f_91062_).m_92895_(arg_0));
        this.pose.m_85836_();
        this.pose.m_85837_(0.0, 0.0, 0.5);
        this.mc.f_91062_.m_271703_(fitted, x, y, this.alpha(color), false, this.pose.m_85850_().m_252922_(), (MultiBufferSource)BUFFER, Font.DisplayMode.NORMAL, 0, 0xF000F0);
        this.pose.m_85849_();
    }

    private void row(String label, float x, float y, int width, boolean selected) {
        this.rect(x, y, width, 34.0f, selected ? -1336576 : -285212673);
        this.text(label, x + 10.0f, y + 12.0f, selected ? -1 : -13619152, width - 20);
    }

    private void icon(PublicMenuView.Item item, float x, float y) {
        this.pose.m_85836_();
        this.pose.m_252880_(x + 8.0f, y + 8.0f, 3.0f);
        this.pose.m_85841_(16.0f, -16.0f, 16.0f);
        this.mc.m_91291_().m_269128_(new ItemStack((ItemLike)BuiltInRegistries.f_257033_.m_7745_(item.type())), ItemDisplayContext.GUI, 0xF000F0, OverlayTexture.f_118083_, this.pose, (MultiBufferSource)BUFFER, (Level)this.mc.f_91073_, 0);
        this.pose.m_85849_();
    }

    private void draw(Player player, SAOMenuPresence.Presence p, long now) {
        MenuActivityPacket.Delivery data = p.data;
        PublicMenuView view = data.view;
        for (int i2 = 0; i2 < 5; ++i2) {
            Iterator<PublicMenuView.Item> key = ROOT[i2] + (view.root() == i2 ? "_on" : "");
            this.quad(new ResourceLocation("saomcui", "textures/gui/icons/" + (String)((Object)key) + ".png"), -22.0f, -159.5f + (float)(i2 * 55), 44.0f, 44.0f, 0.2f, -1);
            if (i2 >= 4) continue;
            this.rect(-1.0f, -115.5f + (float)(i2 * 55), 2.0f, 11.0f, -2130706433);
        }
        if (data.section == MenuActivityPacket.Section.INVENTORY || data.section == MenuActivityPacket.Section.CRAFTING) {
            this.rect(-250.0f, -150.0f, 200.0f, 300.0f, -285212673);
            this.rect(-249.0f, -149.0f, 198.0f, 28.0f, -1381654);
            this.text(player.m_36316_().getName(), -235.0f, -139.0f, -13619152, 170);
            this.text("Equipment", -235.0f, -100.0f, -13619152, 170);
            int armorRow = 0;
            for (PublicMenuView.Item item : data.items) {
                if (item.group() != 2 && item.group() != 3) continue;
                this.icon(item, -235.0f, -78 + armorRow * 28);
                this.text(item.name(), -212.0f, -74 + armorRow * 28, -13619152, 151);
                ++armorRow;
            }
            this.text("Hotbar", -235.0f, 78.0f, -13619152, 170);
            for (PublicMenuView.Item item : data.items) {
                if (item.group() != 0) continue;
                this.icon(item, -240 + item.slot() * 20, 100.0f);
            }
            for (int i3 = 0; i3 < PROFILE.length; ++i3) {
                float y = -208.5f + (float)(i3 * 36);
                boolean selected = view.sub() == i3;
                this.row("", 37.0f, y, 160, selected);
                this.quad(new ResourceLocation("saomcui", "textures/gui/under_icons/friend" + (selected ? "_on" : "") + ".png"), 52.0f, y + 5.0f, 24.0f, 24.0f, 0.2f, -1);
                this.text(PROFILE[i3], 87.0f, y + 13.0f, selected ? -1 : -13619152, 102);
            }
            if (view.sub() == 0 || view.sub() == 1) {
                int group = view.sub();
                List<PublicMenuView.Item> items = data.items.stream().filter(i -> i.group() == group).toList();
                int first = 0;
                int visible = items.size();
                float top = -208.5f + (float)(view.sub() * 36) + 18.0f - (float)(visible * 18);
                for (int i4 = 0; i4 < visible; ++i4) {
                    PublicMenuView.Item item = items.get(first + i4);
                    float y = top + (float)(i4 * 36);
                    if (y < -350.0f || y > 316.0f) continue;
                    this.row("", 217.0f, y, 160, first + i4 == view.selected());
                    this.icon(item, 223.0f, y + 8.0f);
                    this.text(item.name() + " x" + item.count(), 245.0f, y + 11.0f, first + i4 == view.selected() ? -1 : -13619152, 123);
                }
                if (view.selected() >= 0 && view.selected() < items.size()) {
                    String[] stringArray;
                    float y = top + (float)((view.selected() - first) * 36);
                    if (group == 0) {
                        String[] stringArray2 = new String[1];
                        stringArray = stringArray2;
                        stringArray2[0] = "Stow in Items";
                    } else {
                        String[] stringArray3 = new String[2];
                        stringArray3[0] = "Equip";
                        stringArray = stringArray3;
                        stringArray3[1] = "Drop";
                    }
                    String[] actions = stringArray;
                    for (int i5 = 0; i5 < actions.length; ++i5) {
                        if (!(y + (float)(i5 * 34) >= -350.0f) || !(y + (float)(i5 * 34) <= 316.0f)) continue;
                        this.row(actions[i5], 397.0f, y + (float)(i5 * 34), 140, view.action() == i5);
                    }
                }
                if (items.isEmpty()) {
                    this.row("Empty", 217.0f, -120.0f, 160, false);
                }
            } else if (data.section == MenuActivityPacket.Section.CRAFTING) {
                this.rect(217.0f, -125.0f, 250.0f, 250.0f, -285212673);
                this.text("Crafting", 230.0f, -110.0f, -13619152, 210);
                this.text("Preparing a recipe", 230.0f, -85.0f, -13619152, 210);
                for (int y = 0; y < 3; ++y) {
                    for (int x = 0; x < 3; ++x) {
                        this.rect(235 + x * 40, -50 + y * 40, 34.0f, 34.0f, -3092272);
                    }
                }
                this.text("Recipe contents not shared", 230.0f, 95.0f, -13619152, 220);
            }
            float cx = p.cursorX(now);
            float cy = p.cursorY(now);
            this.quad(white, cx - 2.0f, cy - 6.0f, 4.0f, 12.0f, 6.0f, view.pressed() ? -1336576 : -11184811);
            this.quad(white, cx - 6.0f, cy - 2.0f, 12.0f, 4.0f, 6.0f, view.pressed() ? -1336576 : -11184811);
        } else if (data.section != MenuActivityPacket.Section.MENU) {
            this.rect(50.0f, -125.0f, 350.0f, 250.0f, -285212673);
            this.rect(51.0f, -124.0f, 348.0f, 30.0f, -1381654);
            this.text(data.section.label, 65.0f, -113.0f, -13619152, 320);
            this.rect(65.0f, -96.0f, 80.0f, 2.0f, -1336576);
            String message = switch (data.section) {
                case MenuActivityPacket.Section.MESSAGES -> "Private messages hidden";
                case MenuActivityPacket.Section.MAP -> "Map and waypoint details hidden";
                case MenuActivityPacket.Section.SOCIAL -> "Social details hidden";
                case MenuActivityPacket.Section.SETTINGS -> "Personal settings hidden";
                default -> "Private content hidden";
            };
            this.text(message, 70.0f, -10.0f, -13619152, 310);
        }
    }

    static {
        BUFFER = MultiBufferSource.m_109898_((BufferBuilder)new BufferBuilder(65536));
        ROOT = new String[]{"ringed_man", "ringed_men", "ringed_message", "ringed_location", "ringed_config"};
        PROFILE = new String[]{"Equipment", "Items", "Crafting", "Skills"};
    }
}

