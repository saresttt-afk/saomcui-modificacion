/*
 * Decompiled with CFR 0.152.
 */
package com.ag.saomcui.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class SkillEditBridge {
    private SkillEditBridge() {
    }

    public static void run(String string, Runnable runnable) {
        if ("Skills".equals(string)) {
            try {
                SkillEditBridge.openSkillEditScreen();
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return;
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    /*
     * WARNING - void declaration
     */
    private static void openSkillEditScreen() throws Exception {
        void var9_35;
        void var7_23;
        void var10_38;
        void var6_17;
        void var9_31;
        void var3_5;
        Class<?> clazz;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> clazz2 = Class.forName("net.minecraft.client.Minecraft", true, classLoader);
        Object object2 = null;
        for (Method method : clazz2.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) || method.getParameterCount() != 0 || !clazz2.isAssignableFrom(method.getReturnType()) || !method.getName().equals("getInstance") && !method.getName().equals("m_91087_")) continue;
            method.setAccessible(true);
            object2 = method.invoke(null, new Object[0]);
            break;
        }
        if (object2 == null) {
            throw new IllegalStateException("Cannot find Minecraft client instance");
        }
        Object var3_4 = null;
        block1: for (clazz = clazz2; clazz != null && var3_5 == null; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                String string = field.getType().getName();
                if (!string.endsWith("LocalPlayer") && !string.equals("net.minecraft.client.player.LocalPlayer")) continue;
                field.setAccessible(true);
                Object object = field.get(object2);
                if (object != null) continue block1;
            }
        }
        if (var3_5 == null) {
            throw new IllegalStateException("Cannot find local player");
        }
        clazz = Class.forName("yesman.epicfight.world.capabilities.EpicFightCapabilities", true, classLoader);
        Object object = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !field.getName().equals("CAPABILITY_SKILL")) continue;
            field.setAccessible(true);
            object = field.get(null);
            break;
        }
        if (object == null) {
            throw new IllegalStateException("Cannot find Epic Fight CAPABILITY_SKILL");
        }
        Object var6_15 = null;
        Method[] methodArray = var3_5.getClass().getMethods();
        int n = methodArray.length;
        boolean bl = false;
        while (var9_31 < n) {
            Method method = methodArray[var9_31];
            if ((method.getName().equals("getCapability") || method.getName().equals("m_21441_")) && method.getParameterCount() == 1 && method.getParameterTypes()[0].isInstance(object)) {
                Object object3 = method.invoke(var3_5, object);
                break;
            }
            ++var9_31;
        }
        if (var6_17 == null) {
            throw new IllegalStateException("Cannot obtain Epic Fight skill capability");
        }
        Object var7_21 = null;
        Object object4 = var6_17.getClass().getMethods();
        int n2 = ((Method[])object4).length;
        boolean bl2 = false;
        while (var10_38 < n2) {
            Method method = object4[var10_38];
            if (method.getName().equals("orElse") && method.getParameterCount() == 1) {
                Object object5 = method.invoke(var6_17, new Object[]{null});
                break;
            }
            ++var10_38;
        }
        if (var7_23 == null) {
            throw new IllegalStateException("Epic Fight skill capability is not available for the player yet");
        }
        object4 = Class.forName("yesman.epicfight.client.gui.screen.SkillEditScreen", true, classLoader);
        Object var9_33 = null;
        for (Constructor<?> constructor : ((Class)object4).getConstructors()) {
            Class<?>[] classArray = constructor.getParameterTypes();
            if (classArray.length != 2 || !classArray[0].isInstance(var3_5) || !classArray[1].isInstance(var7_23)) continue;
            Object obj = constructor.newInstance(var3_5, var7_23);
            break;
        }
        if (var9_35 == null) {
            throw new IllegalStateException("Cannot construct Epic Fight SkillEditScreen");
        }
        Class<?> clazz3 = Class.forName("net.minecraft.client.gui.screens.Screen", true, classLoader);
        for (Method method : clazz2.getMethods()) {
            if (!method.getName().equals("setScreen") && !method.getName().equals("m_91152_") || method.getParameterCount() != 1 || !method.getParameterTypes()[0].isAssignableFrom(clazz3)) continue;
            method.invoke(object2, var9_35);
            return;
        }
        throw new IllegalStateException("Cannot find Minecraft setScreen method");
    }
}

