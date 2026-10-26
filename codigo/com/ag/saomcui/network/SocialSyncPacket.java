/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.ag.saomcui.network;

import com.ag.saomcui.social.SocialManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SocialSyncPacket {
    private final String guildName;
    private final String emblemUrl;
    private final String roleName;
    private final boolean inParty;
    private final List<String> partyMembers;
    private final List<String> guildMembers;
    private final List<String> friends;

    public SocialSyncPacket(String guildName, String emblemUrl, String roleName, boolean inParty, List<String> partyMembers, List<String> guildMembers, List<String> friends) {
        this.guildName = guildName == null ? "" : guildName;
        this.emblemUrl = emblemUrl == null ? "" : emblemUrl;
        this.roleName = roleName == null ? "" : roleName;
        this.inParty = inParty;
        this.partyMembers = partyMembers == null ? new ArrayList() : partyMembers;
        this.guildMembers = guildMembers == null ? new ArrayList() : guildMembers;
        this.friends = friends == null ? new ArrayList() : friends;
    }

    public SocialSyncPacket(FriendlyByteBuf buf) {
        this.guildName = buf.m_130277_();
        this.emblemUrl = buf.m_130277_();
        this.roleName = buf.m_130277_();
        this.inParty = buf.readBoolean();
        int pSize = buf.readInt();
        this.partyMembers = new ArrayList<String>();
        for (int i = 0; i < pSize; ++i) {
            this.partyMembers.add(buf.m_130277_());
        }
        int gSize = buf.readInt();
        this.guildMembers = new ArrayList<String>();
        for (int i = 0; i < gSize; ++i) {
            this.guildMembers.add(buf.m_130277_());
        }
        int fSize = buf.readInt();
        this.friends = new ArrayList<String>();
        for (int i = 0; i < fSize; ++i) {
            this.friends.add(buf.m_130277_());
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.guildName != null ? this.guildName : "");
        buf.m_130070_(this.emblemUrl != null ? this.emblemUrl : "");
        buf.m_130070_(this.roleName != null ? this.roleName : "");
        buf.writeBoolean(this.inParty);
        buf.writeInt(this.partyMembers.size());
        for (String m : this.partyMembers) {
            buf.m_130070_(m != null ? m : "");
        }
        buf.writeInt(this.guildMembers.size());
        for (String m : this.guildMembers) {
            buf.m_130070_(m != null ? m : "");
        }
        buf.writeInt(this.friends.size());
        for (String f : this.friends) {
            buf.m_130070_(f != null ? f : "");
        }
    }

    public static void handle(SocialSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> SocialManager.updateClientState(msg.guildName, msg.emblemUrl, msg.roleName, msg.inParty, msg.partyMembers, msg.guildMembers, msg.friends));
        ctx.get().setPacketHandled(true);
    }
}

