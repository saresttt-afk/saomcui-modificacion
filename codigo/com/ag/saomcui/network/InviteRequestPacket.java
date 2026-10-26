/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.ag.saomcui.network;

import com.ag.saomcui.client.SAOMainMenuScreen;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class InviteRequestPacket {
    private final String inviterName;
    private final Type type;
    private final String extraData;

    public InviteRequestPacket(String inviterName, Type type, String extraData) {
        this.inviterName = inviterName;
        this.type = type;
        this.extraData = extraData;
    }

    public InviteRequestPacket(FriendlyByteBuf buf) {
        this.inviterName = buf.m_130277_();
        this.type = (Type)buf.m_130066_(Type.class);
        this.extraData = buf.m_130277_();
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.inviterName);
        buf.m_130068_((Enum)this.type);
        buf.m_130070_(this.extraData);
    }

    public static void handle(InviteRequestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> SAOMainMenuScreen.showExternalInvite(msg.inviterName, msg.type, msg.extraData));
        ctx.get().setPacketHandled(true);
    }

    public static enum Type {
        PARTY,
        GUILD,
        FRIEND;

    }
}

