/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.ag.saomcui.network;

import com.ag.saomcui.client.map.SAOMapScreen;
import com.ag.saomcui.map.Waypoint;
import com.ag.saomcui.network.SAOPacketHandler;
import com.ag.saomcui.social.SocialManager;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class WaypointPacket {
    public final Waypoint waypoint;
    public final Audience audience;
    public final String target;

    public WaypointPacket(Waypoint w, Audience a, String target) {
        this.waypoint = w;
        this.audience = a;
        this.target = target;
    }

    public WaypointPacket(FriendlyByteBuf b) {
        this.waypoint = new Waypoint(b.m_130136_(48), b.m_130136_(128), b.readInt(), b.readInt(), b.readInt());
        this.waypoint.description = b.m_130136_(160);
        this.waypoint.icon = b.m_130136_(2);
        this.waypoint.color = b.readInt() & 0xFFFFFF;
        this.audience = (Audience)b.m_130066_(Audience.class);
        this.target = b.m_130136_(48);
    }

    public void write(FriendlyByteBuf b) {
        b.m_130072_(this.waypoint.name, 48);
        b.m_130072_(this.waypoint.dimension, 128);
        b.writeInt(this.waypoint.x);
        b.writeInt(this.waypoint.y);
        b.writeInt(this.waypoint.z);
        b.m_130072_(this.waypoint.description, 160);
        b.m_130072_(this.waypoint.icon, 2);
        b.writeInt(this.waypoint.color);
        b.m_130068_((Enum)this.audience);
        b.m_130072_(this.target, 48);
    }

    public static void handle(WaypointPacket msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender == null || !msg.waypoint.valid()) {
                return;
            }
            long now = sender.f_8924_.m_129783_().m_46467_();
            long last = sender.getPersistentData().m_128454_("saomcui_last_waypoint_share");
            if (last > 0L && now >= last && now - last < 60L) {
                sender.m_213846_((Component)Component.m_237113_((String)"Please wait 3 seconds between waypoint shares."));
                return;
            }
            sender.getPersistentData().m_128356_("saomcui_last_waypoint_share", Math.max(1L, now));
            if (sender.f_8924_.m_129880_(ResourceKey.m_135785_((ResourceKey)Registries.f_256858_, (ResourceLocation)new ResourceLocation(msg.waypoint.dimension))) == null) {
                return;
            }
            HashSet<UUID> recipients = new HashSet<UUID>();
            switch (msg.audience) {
                case FRIEND: {
                    ServerPlayer friend = sender.f_8924_.m_6846_().m_11255_(msg.target);
                    if (friend == null || !SocialManager.getAllFriends().getOrDefault(sender.m_20148_(), Set.of()).contains(friend.m_20148_())) break;
                    recipients.add(friend.m_20148_());
                    break;
                }
                case PARTY: {
                    SocialManager.Party party = SocialManager.getPlayerParty(sender.m_20148_());
                    if (party == null) break;
                    recipients.addAll(party.members);
                    break;
                }
                case GUILD: {
                    SocialManager.Guild guild = SocialManager.getPlayerGuild(sender.m_20148_());
                    if (guild == null) break;
                    recipients.addAll(guild.getMembers().keySet());
                }
            }
            recipients.remove(sender.m_20148_());
            int sent = 0;
            for (UUID id : recipients) {
                ServerPlayer recipient = sender.f_8924_.m_6846_().m_11259_(id);
                if (recipient == null) continue;
                SAOPacketHandler.sendToPlayer(new Delivery(msg.waypoint, msg.audience, sender.m_36316_().getName()), recipient);
                ++sent;
            }
            sender.m_213846_((Component)Component.m_237113_((String)("Waypoint shared with " + sent + " online player(s).")));
        });
        ctx.setPacketHandled(true);
    }

    public static enum Audience {
        FRIEND,
        PARTY,
        GUILD;

    }

    public static class Delivery
    extends WaypointPacket {
        public Delivery(Waypoint w, Audience a, String sender) {
            super(w, a, sender);
        }

        public Delivery(FriendlyByteBuf b) {
            super(b);
        }

        public static void receive(Delivery msg, Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();
            ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> SAOMapScreen.receive(msg)));
            ctx.setPacketHandled(true);
        }
    }
}

