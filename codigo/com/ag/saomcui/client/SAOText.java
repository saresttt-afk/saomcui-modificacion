/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 */
package com.ag.saomcui.client;

import java.util.Optional;
import java.util.function.ToIntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public final class SAOText {
    private SAOText() {
    }

    public static String fit(String text, int width, ToIntFunction<String> measure) {
        if (measure.applyAsInt(text) <= width) {
            return text;
        }
        String suffix = "...";
        if (measure.applyAsInt(suffix) > width) {
            return "";
        }
        int end = text.length();
        while (end > 0 && measure.applyAsInt(text.substring(0, end) + suffix) > width) {
            end = text.offsetByCodePoints(end, -1);
        }
        return text.substring(0, end) + suffix;
    }

    public static Component partyName(Component original, String account) {
        original = SAOText.normalizeLegacy(original);
        String plain = ChatFormatting.m_126649_((String)original.getString());
        int start = plain.indexOf(account);
        if (account.isEmpty() || start < 0) {
            return original;
        }
        int end = start + account.length();
        int[] offset = new int[]{0};
        MutableComponent result = Component.m_237119_();
        original.m_7451_((style, text) -> {
            String clean = ChatFormatting.m_126649_((String)text);
            int i = 0;
            while (i < clean.length()) {
                boolean green;
                int absolute = offset[0] + i;
                boolean bl = green = absolute >= start && absolute < end;
                int boundary = absolute < start ? start : (absolute < end ? end : Integer.MAX_VALUE);
                int next = (int)Math.min((long)clean.length(), (long)boundary - (long)offset[0]);
                result.m_7220_((Component)Component.m_237113_((String)clean.substring(i, next)).m_6270_(green ? style.m_131140_(ChatFormatting.GREEN) : style));
                i = next;
            }
            offset[0] = offset[0] + clean.length();
            return Optional.empty();
        }, Style.f_131099_);
        return result;
    }

    private static Component normalizeLegacy(Component original) {
        MutableComponent result = Component.m_237119_();
        original.m_7451_((baseStyle, text) -> {
            Style style = baseStyle;
            int start = 0;
            int i = 0;
            while (i + 1 < text.length()) {
                ChatFormatting format;
                if (text.charAt(i) == '\u00a7' && (format = ChatFormatting.m_126645_((char)text.charAt(i + 1))) != null) {
                    if (i > start) {
                        result.m_7220_((Component)Component.m_237113_((String)text.substring(start, i)).m_6270_(style));
                    }
                    style = format == ChatFormatting.RESET ? baseStyle : style.m_131164_(format);
                    start = i + 2;
                    ++i;
                }
                ++i;
            }
            if (start < text.length()) {
                result.m_7220_((Component)Component.m_237113_((String)text.substring(start)).m_6270_(style));
            }
            return Optional.empty();
        }, Style.f_131099_);
        return result;
    }

    public static float healthFraction(float current, float max) {
        return Float.isFinite(current) && Float.isFinite(max) && max > 0.0f ? Math.max(0.0f, Math.min(1.0f, current / max)) : 0.0f;
    }
}

