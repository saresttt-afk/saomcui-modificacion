/*
 * Decompiled with CFR 0.152.
 */
package com.ag.saomcui.client;

public final class SAOFloatMotion {
    private double x;
    private double y;

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public static float ease(float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        return t * t * t * (t * (t * 6.0f - 15.0f) + 10.0f);
    }

    public void update(double seconds, double targetX, double targetY, boolean held, boolean enabled) {
        if (!enabled) {
            this.x = 0.0;
            this.y = 0.0;
            return;
        }
        if (held) {
            return;
        }
        double blend = 1.0 - Math.exp(-10.0 * Math.max(0.0, Math.min(0.1, seconds)));
        this.x += (targetX - this.x) * blend;
        this.y += (targetY - this.y) * blend;
    }
}

