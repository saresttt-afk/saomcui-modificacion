/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.ag.saomcui.network;

import com.ag.saomcui.inventory.SAOCraftingHandler;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class CraftActionPacket {
    private final ItemStack[] gridStacks;
    private final boolean is3x3;
    private final boolean isShift;

    public CraftActionPacket(ItemStack[] gridStacks, boolean is3x3, boolean isShift) {
        this.gridStacks = gridStacks;
        this.is3x3 = is3x3;
        this.isShift = isShift;
    }

    public CraftActionPacket(FriendlyByteBuf buf) {
        this.gridStacks = new ItemStack[9];
        for (int i = 0; i < 9; ++i) {
            this.gridStacks[i] = buf.m_130267_();
        }
        this.is3x3 = buf.readBoolean();
        this.isShift = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        for (int i = 0; i < 9; ++i) {
            buf.m_130055_(this.gridStacks[i] == null ? ItemStack.f_41583_ : this.gridStacks[i]);
        }
        buf.writeBoolean(this.is3x3);
        buf.writeBoolean(this.isShift);
    }

    public ItemStack[] getGridStacks() {
        return this.gridStacks;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player != null) {
                SAOCraftingHandler.handleCraft(player, this.gridStacks, this.is3x3, this.isShift);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

