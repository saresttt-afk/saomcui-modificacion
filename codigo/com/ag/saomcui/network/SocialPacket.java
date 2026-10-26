/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.ag.saomcui.network;

import com.ag.saomcui.social.SocialManager;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SocialPacket(ActionType action, String targetName, String value) {
    public SocialPacket(FriendlyByteBuf buffer) {
        this((ActionType)buffer.m_130066_(ActionType.class), buffer.m_130277_(), buffer.m_130277_());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.m_130068_((Enum)this.action);
        buffer.m_130070_(this.targetName);
        buffer.m_130070_(this.value);
    }

    public static void handle(SocialPacket payload, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> SocialManager.handleAction(context.getSender(), payload.action(), payload.targetName(), payload.value()));
        context.setPacketHandled(true);
    }

    public static enum ActionType {
        PARTY_INVITE,
        PARTY_CREATE,
        PARTY_LEAVE,
        PARTY_KICK,
        PARTY_PROMOTE,
        PARTY_ACCEPT,
        PARTY_DECLINE,
        GUILD_INVITE,
        GUILD_CREATE,
        GUILD_LEAVE,
        GUILD_KICK,
        GUILD_SET_ROLE,
        GUILD_SET_EMBLEM,
        GUILD_ACCEPT,
        GUILD_DECLINE,
        FRIEND_ADD,
        FRIEND_REMOVE,
        FRIEND_ACCEPT,
        FRIEND_DECLINE,
        TRADE_REQUEST;

    }
}

