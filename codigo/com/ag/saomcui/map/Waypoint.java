/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.ag.saomcui.map;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class Waypoint {
    public String id = UUID.randomUUID().toString();
    public String name = "Waypoint";
    public String dimension = "minecraft:overworld";
    public String description = "";
    public String icon = "+";
    public int x;
    public int y;
    public int z;
    public int color = 4774366;
    public boolean visible = true;
    public boolean deathMarker;

    public Waypoint() {
    }

    public Waypoint(String name, String dimension, int x, int y, int z) {
        this.name = name;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean valid() {
        return this.id != null && this.name != null && !this.name.isBlank() && this.name.length() <= 48 && this.dimension != null && this.dimension.length() <= 128 && ResourceLocation.m_135820_((String)this.dimension) != null && this.description != null && this.description.length() <= 160 && this.icon != null && this.icon.length() <= 2 && Math.abs((long)this.x) <= 30000000L && Math.abs((long)this.z) <= 30000000L && this.y >= -2048 && this.y <= 4096;
    }

    public Waypoint copy() {
        Waypoint w = new Waypoint(this.name, this.dimension, this.x, this.y, this.z);
        w.id = this.id;
        w.description = this.description;
        w.icon = this.icon;
        w.color = this.color;
        w.visible = this.visible;
        w.deathMarker = this.deathMarker;
        return w;
    }
}

