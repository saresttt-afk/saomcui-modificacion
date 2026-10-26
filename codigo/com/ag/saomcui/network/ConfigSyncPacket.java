/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.ag.saomcui.network;

import com.ag.saomcui.config.SAOConfig;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class ConfigSyncPacket {
    public final boolean enableCrafting;

    public ConfigSyncPacket(boolean enableCrafting) {
        this.enableCrafting = enableCrafting;
    }

    public ConfigSyncPacket(FriendlyByteBuf buf) {
        this.enableCrafting = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.enableCrafting);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> {
            SAOConfig.enableCrafting = this.enableCrafting;
        }));
        ctx.get().setPacketHandled(true);
    }
}

