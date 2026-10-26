/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.ag.saomcui.network;

import com.ag.saomcui.social.SocialManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class GlobalSocialSyncPacket {
    private final Map<UUID, String> playerEmblems;

    public GlobalSocialSyncPacket(Map<UUID, String> playerEmblems) {
        this.playerEmblems = playerEmblems;
    }

    public GlobalSocialSyncPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        this.playerEmblems = new HashMap<UUID, String>();
        for (int i = 0; i < size; ++i) {
            UUID uuid = buf.m_130259_();
            String url = buf.m_130277_();
            this.playerEmblems.put(uuid, url);
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.playerEmblems.size());
        for (Map.Entry<UUID, String> entry : this.playerEmblems.entrySet()) {
            buf.m_130077_(entry.getKey());
            buf.m_130070_(entry.getValue() != null ? entry.getValue() : "");
        }
    }

    public static void handle(GlobalSocialSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> SocialManager.updateGlobalEmblems(msg.playerEmblems));
        ctx.get().setPacketHandled(true);
    }
}

