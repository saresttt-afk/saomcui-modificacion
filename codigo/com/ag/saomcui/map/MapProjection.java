/*
 * Decompiled with CFR 0.152.
 */
package com.ag.saomcui.map;

public final class MapProjection {
    private MapProjection() {
    }

    public static double world(double screen, double midpoint, double center, double zoom) {
        return center + (screen - midpoint) / zoom;
    }

    public static double screen(double world, double midpoint, double center, double zoom) {
        return midpoint + (world - center) * zoom;
    }

    public static int block(double world) {
        return (int)Math.floor(world);
    }

    public static double clampZoom(double zoom) {
        return Math.max(0.125, Math.min(8.0, zoom));
    }
}

