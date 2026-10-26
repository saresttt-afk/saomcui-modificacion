/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 */
package com.ag.saomcui.network;

import com.ag.saomcui.inventory.SAOInventory;
import com.ag.saomcui.network.MenuActivityPacket;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record PublicMenuView(int root, int sub, int selected, int action, int cursorX, int cursorY, boolean pressed) {
    public static final PublicMenuView EMPTY = new PublicMenuView(-1, -1, -1, -1, 0, 0, false);

    public PublicMenuView {
        root = PublicMenuView.clamp(root, -1, 4);
        sub = PublicMenuView.clamp(sub, -1, 15);
        selected = PublicMenuView.clamp(selected, -1, 63);
        action = PublicMenuView.clamp(action, -1, 7);
        cursorX = PublicMenuView.clamp(cursorX, -300, 650);
        cursorY = PublicMenuView.clamp(cursorY, -350, 350);
    }

    public PublicMenuView(FriendlyByteBuf b) {
        this(b.readByte(), b.readByte(), b.readByte(), b.readByte(), b.readShort(), b.readShort(), b.readBoolean());
    }

    private static int clamp(int n, int lo, int hi) {
        return Math.max(lo, Math.min(hi, n));
    }

    public void write(FriendlyByteBuf b) {
        b.writeByte(this.root);
        b.writeByte(this.sub);
        b.writeByte(this.selected);
        b.writeByte(this.action);
        b.writeShort(this.cursorX);
        b.writeShort(this.cursorY);
        b.writeBoolean(this.pressed);
    }

    public PublicMenuView publicOnly(MenuActivityPacket.Section section) {
        return section == MenuActivityPacket.Section.INVENTORY || section == MenuActivityPacket.Section.CRAFTING ? this : new PublicMenuView(this.root, -1, -1, -1, 0, 0, false);
    }

    private static void add(List<Item> items, ItemStack stack, int count, int group, int slot) {
        if (stack.m_41619_() || count <= 0) {
            return;
        }
        String name = ChatFormatting.m_126649_((String)stack.m_41786_().getString()).replaceAll("[\\p{Cntrl}]", "");
        if (name.length() > 80) {
            name = name.substring(0, 80);
        }
        items.add(new Item(BuiltInRegistries.f_257033_.m_7981_((Object)stack.m_41720_()), Math.min(9999, count), name, group, slot));
    }

    public static List<Item> inventory(ServerPlayer player, MenuActivityPacket.Section section) {
        int i;
        if (section != MenuActivityPacket.Section.INVENTORY && section != MenuActivityPacket.Section.CRAFTING) {
            return List.of();
        }
        ArrayList<Item> items = new ArrayList<Item>();
        for (i = 0; i < 9; ++i) {
            ItemStack stack = player.m_150109_().m_8020_(i);
            PublicMenuView.add(items, stack, stack.m_41613_(), 0, i);
        }
        player.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
            for (int i = 0; i < 27; ++i) {
                SAOInventory.SAOStack stack = inv.getStackInSlot(i);
                PublicMenuView.add(items, stack.item, stack.count, 1, i);
            }
        });
        for (i = 0; i < 4; ++i) {
            PublicMenuView.add(items, (ItemStack)player.m_150109_().f_35975_.get(i), 1, 2, i);
        }
        PublicMenuView.add(items, player.m_21206_(), player.m_21206_().m_41613_(), 3, 0);
        return List.copyOf(items);
    }

    public record Item(ResourceLocation type, int count, String name, int group, int slot) {
        public Item(FriendlyByteBuf b) {
            this(b.m_130281_(), b.m_130242_(), b.m_130136_(80), b.readByte(), b.readByte());
        }

        public void write(FriendlyByteBuf b) {
            b.m_130085_(this.type);
            b.m_130130_(this.count);
            b.m_130072_(this.name, 80);
            b.writeByte(this.group);
            b.writeByte(this.slot);
        }
    }
}

