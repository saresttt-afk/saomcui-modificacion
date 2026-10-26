/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.ag.saomcui.network;

import com.ag.saomcui.client.SAOChatManager;
import com.ag.saomcui.client.SAOClientOnly;
import com.ag.saomcui.social.SocialManager;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class ChatPacket {
    private final Channel channel;
    private final String senderName;
    private final String message;
    private final String targetTag;

    public ChatPacket(Channel channel, String senderName, String message, String targetTag) {
        this.channel = channel;
        this.senderName = senderName == null ? "" : senderName;
        this.message = message == null ? "" : message;
        this.targetTag = targetTag == null ? "" : targetTag;
    }

    public ChatPacket(FriendlyByteBuf buf) {
        this.channel = (Channel)buf.m_130066_(Channel.class);
        this.senderName = buf.m_130277_();
        this.message = buf.m_130277_();
        this.targetTag = buf.m_130277_();
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130068_((Enum)this.channel);
        buf.m_130070_(this.senderName != null ? this.senderName : "");
        buf.m_130070_(this.message != null ? this.message : "");
        buf.m_130070_(this.targetTag != null ? this.targetTag : "");
    }

    public Channel getChannel() {
        return this.channel;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public String getMessage() {
        return this.message;
    }

    public String getTargetTag() {
        return this.targetTag;
    }

    public static void handle(ChatPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isServer()) {
                ServerPlayer sender = context.getSender();
                System.out.println("[SERVER] Received CHAT PACKET from " + (sender != null ? sender.m_7755_().getString() : "Unknown") + " | Channel: " + msg.channel.name() + " | To: " + msg.targetTag + " | Msg: " + msg.message);
                if (sender != null) {
                    SocialManager.handleChatMessage(sender, msg);
                }
            } else {
                DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> {
                    System.out.println("[CLIENT] Received CHAT PACKET from " + msg.senderName + " | Channel: " + msg.channel.name() + " | To: " + msg.targetTag + " | Msg: " + msg.message);
                    SAOChatManager.receiveMessage(msg.channel, msg.senderName, msg.message, msg.targetTag);
                    if (msg.channel != Channel.SYSTEM) {
                        SAOClientOnly.handleChatMessage(msg.senderName, msg.message, msg.channel.name(), msg.targetTag);
                    }
                });
            }
        });
        context.setPacketHandled(true);
    }

    public static enum Channel {
        PRIVATE,
        PARTY,
        GUILD,
        SYSTEM;

    }
}

