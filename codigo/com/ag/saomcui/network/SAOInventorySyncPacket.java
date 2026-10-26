/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.ag.saomcui.network;

import com.ag.saomcui.client.SAOClientOnly;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record SAOInventorySyncPacket(CompoundTag inventoryData) {
    public SAOInventorySyncPacket(FriendlyByteBuf buffer) {
        this(buffer.m_130260_());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.m_130079_(this.inventoryData);
    }

    public static void handle(SAOInventorySyncPacket payload, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> SAOClientOnly.handleInventorySync(payload.inventoryData())));
        context.setPacketHandled(true);
    }
}

