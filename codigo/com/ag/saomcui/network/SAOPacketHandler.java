/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package com.ag.saomcui.network;

import com.ag.saomcui.network.ChatPacket;
import com.ag.saomcui.network.ConfigSyncPacket;
import com.ag.saomcui.network.CraftActionPacket;
import com.ag.saomcui.network.DeathEffectPacket;
import com.ag.saomcui.network.GlobalSocialSyncPacket;
import com.ag.saomcui.network.InviteRequestPacket;
import com.ag.saomcui.network.ItemActionPacket;
import com.ag.saomcui.network.MenuActivityPacket;
import com.ag.saomcui.network.SAOInventorySyncPacket;
import com.ag.saomcui.network.SocialPacket;
import com.ag.saomcui.network.SocialSyncPacket;
import com.ag.saomcui.network.WaypointPacket;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class SAOPacketHandler {
    private static final String PROTOCOL_VERSION = "5";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation("saomcui:main"), () -> "5", "5"::equals, "5"::equals);
    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, DeathEffectPacket.class, DeathEffectPacket::write, DeathEffectPacket::new, DeathEffectPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(packetId++, MenuActivityPacket.class, MenuActivityPacket::write, MenuActivityPacket::new, MenuActivityPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(packetId++, MenuActivityPacket.Delivery.class, MenuActivityPacket.Delivery::write, MenuActivityPacket.Delivery::new, MenuActivityPacket.Delivery::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(packetId++, WaypointPacket.class, WaypointPacket::write, WaypointPacket::new, WaypointPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(packetId++, WaypointPacket.Delivery.class, WaypointPacket::write, WaypointPacket.Delivery::new, WaypointPacket.Delivery::receive, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(packetId++, ItemActionPacket.class, ItemActionPacket::write, ItemActionPacket::new, ItemActionPacket::handle);
        CHANNEL.registerMessage(packetId++, SocialPacket.class, SocialPacket::write, SocialPacket::new, SocialPacket::handle);
        CHANNEL.registerMessage(packetId++, SocialSyncPacket.class, SocialSyncPacket::write, SocialSyncPacket::new, SocialSyncPacket::handle);
        CHANNEL.registerMessage(packetId++, InviteRequestPacket.class, InviteRequestPacket::write, InviteRequestPacket::new, InviteRequestPacket::handle);
        CHANNEL.registerMessage(packetId++, GlobalSocialSyncPacket.class, GlobalSocialSyncPacket::write, GlobalSocialSyncPacket::new, GlobalSocialSyncPacket::handle);
        CHANNEL.registerMessage(packetId++, SAOInventorySyncPacket.class, SAOInventorySyncPacket::write, SAOInventorySyncPacket::new, SAOInventorySyncPacket::handle);
        CHANNEL.registerMessage(packetId++, ChatPacket.class, ChatPacket::write, ChatPacket::new, ChatPacket::handle);
        CHANNEL.registerMessage(packetId++, ConfigSyncPacket.class, ConfigSyncPacket::toBytes, ConfigSyncPacket::new, ConfigSyncPacket::handle);
        CHANNEL.registerMessage(packetId++, CraftActionPacket.class, CraftActionPacket::toBytes, CraftActionPacket::new, CraftActionPacket::handle);
    }

    public static <MSG> void sendToServer(MSG message) {
        CHANNEL.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}

