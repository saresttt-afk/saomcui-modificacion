/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.ConfirmScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 */
package com.ag.saomcui.client.map;

import com.ag.saomcui.client.map.MapStore;
import com.ag.saomcui.client.map.MapUi;
import com.ag.saomcui.client.map.SAOMapScreen;
import com.ag.saomcui.map.Waypoint;
import com.ag.saomcui.network.SAOPacketHandler;
import com.ag.saomcui.network.WaypointPacket;
import com.ag.saomcui.social.SocialManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class WaypointScreen
extends Screen {
    private final Screen parent;
    private final Waypoint draft;
    private final Waypoint original;
    private final boolean incoming;
    private EditBox name;
    private EditBox x;
    private EditBox y;
    private EditBox z;
    private EditBox description;
    private String error = "";
    private int left;
    private int top;
    private int colorIndex;
    private static final int[] COLORS = new int[]{4774366, 15440640, 15756653, 8702824, 11308512, 0xFFFFFF};
    private static final String[] ICONS = new String[]{"+", "X", "!", "H", "*"};

    public WaypointScreen(Screen parent, Waypoint draft, Waypoint original, boolean incoming) {
        super((Component)Component.m_237113_((String)(incoming ? "RECEIVED WAYPOINT" : "WAYPOINT")));
        this.parent = parent;
        this.draft = draft;
        this.original = original;
        this.incoming = incoming;
    }

    private EditBox field(int xx, int yy, int width, String value, int length, String label) {
        EditBox box = new EditBox(this.f_96547_, xx, yy, width, 18, (Component)Component.m_237113_((String)label));
        box.m_94202_(-1);
        box.m_94205_(-2037010);
        box.m_94199_(length);
        box.m_94144_(value);
        this.m_142416_((GuiEventListener)box);
        return box;
    }

    protected void m_7856_() {
        this.left = this.f_96543_ / 2 - 145;
        this.top = Math.max(4, this.f_96544_ / 2 - 115);
        this.name = this.field(this.left + 10, this.top + 35, 270, this.name == null ? this.draft.name : this.name.m_94155_(), 48, "Name");
        this.x = this.field(this.left + 10, this.top + 71, 84, (String)(this.x == null ? "" + this.draft.x : this.x.m_94155_()), 10, "X");
        this.y = this.field(this.left + 103, this.top + 71, 84, (String)(this.y == null ? "" + this.draft.y : this.y.m_94155_()), 6, "Y");
        this.z = this.field(this.left + 196, this.top + 71, 84, (String)(this.z == null ? "" + this.draft.z : this.z.m_94155_()), 10, "Z");
        this.description = this.field(this.left + 10, this.top + 103, 270, this.description == null ? this.draft.description : this.description.m_94155_(), 160, "Description");
        this.m_142416_((GuiEventListener)SAOMapScreen.button(this.left + 10, this.top + 134, 84, "Color", () -> {
            this.draft.color = COLORS[++this.colorIndex % COLORS.length];
        }));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(this.left + 103, this.top + 134, 84, "Icon", () -> {
            this.draft.icon = ICONS[(Arrays.asList(ICONS).indexOf(this.draft.icon) + 1) % ICONS.length];
        }));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(this.left + 196, this.top + 134, 84, "Show / Hide", () -> {
            this.draft.visible = !this.draft.visible;
        }));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(this.left + 10, this.top + 162, 84, this.incoming ? "Accept" : "Save", this::save));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(this.left + 103, this.top + 162, 84, this.incoming ? "Decline" : "Cancel", () -> {
            if (this.incoming) {
                MapStore.inbox.pollFirst();
            }
            this.m_7379_();
        }));
        this.m_142416_((GuiEventListener)SAOMapScreen.button(this.left + 196, this.top + 162, 84, "Share", () -> {
            if (this.read()) {
                MapUi.show(new ShareScreen(this, this.draft.copy()));
            }
        }));
        if (this.original != null) {
            this.m_142416_((GuiEventListener)SAOMapScreen.button(this.left + 10, this.top + 190, 270, "Delete waypoint", () -> MapUi.show((Screen)new ConfirmScreen(yes -> {
                if (yes) {
                    MapStore.data.waypoints.remove(this.original);
                    MapStore.changed();
                    MapStore.save();
                    MapUi.show(this.parent);
                } else {
                    MapUi.show(this);
                }
            }, (Component)Component.m_237113_((String)("Delete " + this.original.name + "?")), (Component)Component.m_237113_((String)"This removes your saved waypoint.")){

                public void m_7379_() {
                    MapUi.show(WaypointScreen.this);
                }
            })));
        }
        this.m_264313_((GuiEventListener)this.name);
    }

    private boolean read() {
        try {
            this.draft.x = Integer.parseInt(this.x.m_94155_().trim());
            this.draft.y = Integer.parseInt(this.y.m_94155_().trim());
            this.draft.z = Integer.parseInt(this.z.m_94155_().trim());
        }
        catch (NumberFormatException e) {
            this.error = "Enter whole numbers for X, Y and Z.";
            return false;
        }
        this.draft.name = this.name.m_94155_().trim();
        this.draft.description = this.description.m_94155_().trim();
        if (!this.draft.valid()) {
            this.error = "Name required; coordinates must be inside world limits.";
            return false;
        }
        return true;
    }

    private void save() {
        if (!this.read()) {
            return;
        }
        if (this.original != null) {
            int index = MapStore.data.waypoints.indexOf(this.original);
            if (index < 0) {
                this.error = "Waypoint no longer exists.";
                return;
            }
            MapStore.data.waypoints.set(index, this.draft);
            MapStore.changed();
            MapStore.save();
        } else if (!MapStore.add(this.draft)) {
            this.error = MapStore.status;
            return;
        }
        if (this.incoming) {
            MapStore.inbox.pollFirst();
        }
        this.m_7379_();
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float dt) {
        g.m_280509_(0, 0, this.f_96543_, this.f_96544_, -1);
        g.m_280509_(this.left, this.top, this.left + 290, this.top + 224, -1381654);
        g.m_280509_(this.left, this.top, this.left + 290, this.top + 3, -1336576);
        g.m_280614_(this.f_96547_, this.f_96539_, this.left + 10, this.top + 10, -13619152, false);
        g.m_280056_(this.f_96547_, "Name", this.left + 10, this.top + 24, -13619152, false);
        g.m_280056_(this.f_96547_, "X                   Y                   Z", this.left + 10, this.top + 59, -13619152, false);
        g.m_280056_(this.f_96547_, "Description", this.left + 10, this.top + 92, -13619152, false);
        g.m_280509_(this.left + 10, this.top + 124, this.left + 19, this.top + 133, -11184811);
        g.m_280509_(this.left + 11, this.top + 125, this.left + 18, this.top + 132, 0xFF000000 | this.draft.color);
        g.m_280056_(this.f_96547_, this.f_96547_.m_92834_(this.draft.dimension + "  " + this.draft.icon + "  " + (this.draft.visible ? "Visible" : "Hidden"), 256), this.left + 24, this.top + 124, -13619152, false);
        super.m_88315_(g, mx, my, dt);
        if (!this.error.isEmpty()) {
            g.m_280056_(this.f_96547_, this.f_96547_.m_92834_(this.error, this.f_96543_ - 20), 10, this.f_96544_ - 12, -5627872, false);
        }
    }

    public void m_7379_() {
        MapUi.show(this.parent);
    }

    public boolean m_7043_() {
        return false;
    }

    private static class ShareScreen
    extends Screen {
        private final Screen parent;
        private final Waypoint waypoint;
        private final List<String> friends = new ArrayList<String>();
        private int friendIndex;
        private String status = "";

        ShareScreen(Screen parent, Waypoint w) {
            super((Component)Component.m_237113_((String)"SHARE WAYPOINT"));
            this.parent = parent;
            this.waypoint = w;
            for (String f : SocialManager.getClientFriends()) {
                String[] parts = f.split("\\|");
                if (parts.length != 3 || !parts[2].equals("true")) continue;
                this.friends.add(parts[1]);
            }
        }

        protected void m_7856_() {
            int x = this.f_96543_ / 2 - 120;
            int y = this.f_96544_ / 2 - 65;
            this.m_142416_((GuiEventListener)SAOMapScreen.button(x, y, 240, "Friend: " + (this.friends.isEmpty() ? "none online" : this.friends.get(this.friendIndex)), () -> {
                if (!this.friends.isEmpty()) {
                    this.friendIndex = (this.friendIndex + 1) % this.friends.size();
                    this.m_232761_();
                }
            }));
            this.m_142416_((GuiEventListener)SAOMapScreen.button(x, y + 24, 240, "Share with selected friend", () -> {
                if (!this.friends.isEmpty()) {
                    this.send(WaypointPacket.Audience.FRIEND, this.friends.get(this.friendIndex));
                }
            }));
            this.m_142416_((GuiEventListener)SAOMapScreen.button(x, y + 48, 116, "Share to party", () -> this.send(WaypointPacket.Audience.PARTY, "")));
            this.m_142416_((GuiEventListener)SAOMapScreen.button(x + 124, y + 48, 116, "Share to guild", () -> this.send(WaypointPacket.Audience.GUILD, "")));
            this.m_142416_((GuiEventListener)SAOMapScreen.button(x, y + 96, 240, "Back", this::m_7379_));
        }

        private void send(WaypointPacket.Audience a, String target) {
            SAOPacketHandler.sendToServer(new WaypointPacket(this.waypoint, a, target));
            this.status = "Requested. Server confirms in chat.";
        }

        public void m_88315_(GuiGraphics g, int mx, int my, float dt) {
            g.m_280509_(0, 0, this.f_96543_, this.f_96544_, -1);
            g.m_280614_(this.f_96547_, this.f_96539_, (this.f_96543_ - this.f_96547_.m_92852_((FormattedText)this.f_96539_)) / 2, this.f_96544_ / 2 - 88, -13619152, false);
            super.m_88315_(g, mx, my, dt);
            g.m_280056_(this.f_96547_, this.status, (this.f_96543_ - this.f_96547_.m_92895_(this.status)) / 2, this.f_96544_ / 2 + 12, -11184811, false);
        }

        public void m_7379_() {
            MapUi.show(this.parent);
        }

        public boolean m_7043_() {
            return false;
        }
    }
}

