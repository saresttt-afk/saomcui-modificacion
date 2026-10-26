/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.ag.saomcui.social;

import com.ag.saomcui.network.ChatPacket;
import com.ag.saomcui.network.GlobalSocialSyncPacket;
import com.ag.saomcui.network.InviteRequestPacket;
import com.ag.saomcui.network.SAOPacketHandler;
import com.ag.saomcui.network.SocialPacket;
import com.ag.saomcui.network.SocialSyncPacket;
import com.ag.saomcui.social.SocialStorage;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class SocialManager {
    private static final Map<UUID, Guild> playerToGuild = new HashMap<UUID, Guild>();
    private static final Map<UUID, Party> playerToParty = new HashMap<UUID, Party>();
    private static final List<Guild> allGuilds = new ArrayList<Guild>();
    private static final Map<UUID, UUID> pendingPartyInvites = new HashMap<UUID, UUID>();
    private static final Map<UUID, Guild> pendingGuildInvites = new HashMap<UUID, Guild>();
    private static final Map<UUID, Set<UUID>> playerFriends = new HashMap<UUID, Set<UUID>>();
    private static String clientGuildName = null;
    private static String clientEmblemUrl = null;
    private static String clientRole = null;
    private static boolean clientInParty = false;
    private static List<String> clientPartyMembers = new ArrayList<String>();
    private static List<String> clientGuildMembers = new ArrayList<String>();
    private static List<String> clientFriends = new ArrayList<String>();
    private static Map<UUID, String> globalEmblems = new HashMap<UUID, String>();

    public static List<Guild> getAllGuilds() {
        return allGuilds;
    }

    public static Map<UUID, Set<UUID>> getAllFriends() {
        return playerFriends;
    }

    public static void loadData(List<Guild> guilds, Map<UUID, Set<UUID>> friends) {
        allGuilds.clear();
        playerToGuild.clear();
        if (guilds != null) {
            allGuilds.addAll(guilds);
            for (Guild g : allGuilds) {
                for (UUID member : g.members.keySet()) {
                    playerToGuild.put(member, g);
                }
            }
        }
        playerFriends.clear();
        if (friends != null) {
            playerFriends.putAll(friends);
        }
    }

    public static void updateClientState(String gName, String eUrl, String role, boolean inParty, List<String> pMembers, List<String> gMembers, List<String> friends) {
        clientGuildName = gName.isEmpty() ? null : gName;
        clientEmblemUrl = eUrl.isEmpty() ? null : eUrl;
        clientRole = role.isEmpty() ? null : role;
        clientInParty = inParty;
        clientPartyMembers = pMembers;
        clientGuildMembers = gMembers;
        clientFriends = friends;
    }

    public static void updateGlobalEmblems(Map<UUID, String> newEmblems) {
        globalEmblems = newEmblems;
    }

    public static String getGlobalEmblem(UUID uuid) {
        return globalEmblems.get(uuid);
    }

    public static void broadcastGlobalEmblems(MinecraftServer server) {
        HashMap<UUID, String> emblems = new HashMap<UUID, String>();
        for (Guild g : allGuilds) {
            String url = g.getEmblemUrl();
            if (url == null || url.isEmpty()) continue;
            for (UUID memberId : g.getMembers().keySet()) {
                emblems.put(memberId, url);
            }
        }
        GlobalSocialSyncPacket pkt = new GlobalSocialSyncPacket(emblems);
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            SAOPacketHandler.sendToPlayer(pkt, player);
        }
    }

    public static String getClientGuildName() {
        return clientGuildName;
    }

    public static String getClientEmblemUrl() {
        return clientEmblemUrl;
    }

    public static String getClientGuildRole() {
        return clientRole;
    }

    public static boolean isClientInParty() {
        return clientInParty;
    }

    public static List<String> getClientPartyMembers() {
        return clientPartyMembers;
    }

    public static List<String> getClientGuildMembers() {
        return clientGuildMembers;
    }

    public static List<String> getClientFriends() {
        return clientFriends;
    }

    public static void handleAction(ServerPlayer sender, SocialPacket.ActionType action, String target, String value) {
        if (sender == null) {
            return;
        }
        switch (action) {
            case PARTY_CREATE: {
                SocialManager.createParty(sender);
                break;
            }
            case PARTY_LEAVE: {
                SocialManager.leaveParty(sender);
                break;
            }
            case PARTY_INVITE: {
                SocialManager.inviteToParty(sender, target);
                break;
            }
            case PARTY_KICK: {
                SocialManager.kickFromParty(sender, target);
                break;
            }
            case PARTY_PROMOTE: {
                SocialManager.promoteInParty(sender, target);
                break;
            }
            case PARTY_ACCEPT: {
                SocialManager.acceptPartyInvite(sender);
                break;
            }
            case PARTY_DECLINE: {
                pendingPartyInvites.remove(sender.m_20148_());
                break;
            }
            case GUILD_CREATE: {
                SocialManager.createGuild(sender, target);
                break;
            }
            case GUILD_LEAVE: {
                SocialManager.leaveGuild(sender);
                break;
            }
            case GUILD_INVITE: {
                SocialManager.inviteToGuild(sender, target);
                break;
            }
            case GUILD_ACCEPT: {
                SocialManager.acceptGuildInvite(sender);
                break;
            }
            case GUILD_DECLINE: {
                pendingGuildInvites.remove(sender.m_20148_());
                break;
            }
            case GUILD_KICK: {
                ServerPlayer tp;
                Guild kG = playerToGuild.get(sender.m_20148_());
                if (kG == null || kG.getRole(sender.m_20148_()) != Role.LEADER && kG.getRole(sender.m_20148_()) != Role.OFFICER || (tp = sender.f_8924_.m_6846_().m_11255_(target)) == null) break;
                kG.removeMember(tp.m_20148_());
                playerToGuild.remove(tp.m_20148_());
                tp.m_213846_((Component)Component.m_237113_((String)("\u00a7cYou have been kicked from the guild '" + kG.getName() + "'.")));
                SocialManager.syncPlayer(tp);
                for (UUID uid : kG.getMembers().keySet()) {
                    ServerPlayer sp = sender.f_8924_.m_6846_().m_11259_(uid);
                    if (sp == null) continue;
                    SocialManager.syncPlayer(sp);
                }
                SocialManager.broadcastGlobalEmblems(sender.f_8924_);
                break;
            }
            case GUILD_SET_ROLE: {
                ServerPlayer tp;
                Guild sG = playerToGuild.get(sender.m_20148_());
                if (sG == null || sG.getRole(sender.m_20148_()) != Role.LEADER || (tp = sender.f_8924_.m_6846_().m_11255_(target)) == null) break;
                try {
                    Role nr = Role.valueOf(value.toUpperCase());
                    sG.addMember(tp.m_20148_(), tp.m_7755_().getString(), nr);
                    String roleName = nr == Role.OFFICER ? "OFFICER" : "MEMBER";
                    String color = nr == Role.OFFICER ? "\u00a7b" : "\u00a77";
                    tp.m_213846_((Component)Component.m_237113_((String)("\u00a7aYou have been " + (nr == Role.OFFICER ? "promoted" : "demoted") + " to " + color + roleName + "\u00a7a.")));
                    sender.m_213846_((Component)Component.m_237113_((String)("\u00a7a" + target + " set to " + roleName + ".")));
                    for (UUID uid : sG.getMembers().keySet()) {
                        ServerPlayer sp = sender.f_8924_.m_6846_().m_11259_(uid);
                        if (sp == null) continue;
                        SocialManager.syncPlayer(sp);
                    }
                    break;
                }
                catch (Exception nr) {
                    break;
                }
            }
            case GUILD_SET_EMBLEM: {
                Guild eG = playerToGuild.get(sender.m_20148_());
                if (eG == null || eG.getRole(sender.m_20148_()) != Role.LEADER) break;
                eG.setEmblemUrl(value);
                for (UUID uid : eG.getMembers().keySet()) {
                    ServerPlayer p = sender.f_8924_.m_6846_().m_11259_(uid);
                    if (p == null) continue;
                    SocialManager.syncPlayer(p);
                }
                SocialManager.broadcastGlobalEmblems(sender.f_8924_);
                break;
            }
            case FRIEND_ADD: {
                ServerPlayer fTarget = sender.f_8924_.m_6846_().m_11255_(target);
                if (fTarget == null || fTarget.m_20148_().equals(sender.m_20148_())) break;
                SAOPacketHandler.sendToPlayer(new InviteRequestPacket(sender.m_7755_().getString(), InviteRequestPacket.Type.FRIEND, ""), fTarget);
                sender.m_213846_((Component)Component.m_237113_((String)("\u00a7eFriend request sent to " + target + ".")));
                break;
            }
            case FRIEND_ACCEPT: {
                ServerPlayer aTarget = sender.f_8924_.m_6846_().m_11255_(target);
                if (aTarget == null) break;
                playerFriends.computeIfAbsent(sender.m_20148_(), k -> new HashSet()).add(aTarget.m_20148_());
                playerFriends.computeIfAbsent(aTarget.m_20148_(), k -> new HashSet()).add(sender.m_20148_());
                sender.m_213846_((Component)Component.m_237113_((String)("\u00a7aYou are now friends with " + target + "!")));
                aTarget.m_213846_((Component)Component.m_237113_((String)("\u00a7a" + sender.m_7755_().getString() + " accepted your friend request!")));
                SocialManager.syncPlayer(aTarget);
                break;
            }
            case FRIEND_DECLINE: {
                ServerPlayer dTarget = sender.f_8924_.m_6846_().m_11255_(target);
                if (dTarget == null) break;
                dTarget.m_213846_((Component)Component.m_237113_((String)("\u00a7c" + sender.m_7755_().getString() + " declined your friend request.")));
                break;
            }
            case FRIEND_REMOVE: {
                ServerPlayer rTarget = sender.f_8924_.m_6846_().m_11255_(target);
                UUID rUuid = null;
                if (rTarget != null) {
                    rUuid = rTarget.m_20148_();
                }
                if (rUuid == null) break;
                if (playerFriends.containsKey(sender.m_20148_())) {
                    playerFriends.get(sender.m_20148_()).remove(rUuid);
                }
                if (playerFriends.containsKey(rUuid)) {
                    playerFriends.get(rUuid).remove(sender.m_20148_());
                }
                sender.m_213846_((Component)Component.m_237113_((String)("\u00a7cRemoved " + target + " from friends.")));
                if (rTarget == null) break;
                SocialManager.syncPlayer(rTarget);
                break;
            }
            case TRADE_REQUEST: {
                sender.m_213846_((Component)Component.m_237113_((String)("Trade request sent to " + target)));
            }
        }
        if (action.name().startsWith("GUILD_") || action.name().startsWith("FRIEND_")) {
            SocialStorage.save();
        }
        SocialManager.syncPlayer(sender);
    }

    private static void createParty(ServerPlayer p) {
        if (playerToParty.containsKey(p.m_20148_())) {
            return;
        }
        Party pt = new Party(p);
        playerToParty.put(p.m_20148_(), pt);
        p.m_213846_((Component)Component.m_237113_((String)"\u00a76Party created!"));
        SocialManager.syncPlayer(p);
    }

    private static void leaveParty(ServerPlayer p) {
        Party pt = playerToParty.get(p.m_20148_());
        if (pt == null) {
            return;
        }
        pt.removeMember(p.m_20148_());
        playerToParty.remove(p.m_20148_());
        if (pt.isOwner(p.m_20148_()) && !pt.members.isEmpty()) {
            UUID next;
            pt.owner = next = pt.members.iterator().next();
            ServerPlayer np = p.f_8924_.m_6846_().m_11259_(next);
            if (np != null) {
                np.m_213846_((Component)Component.m_237113_((String)"\u00a7aYou are now the party leader!"));
                SocialManager.syncPlayer(np);
            }
        }
        p.m_213846_((Component)Component.m_237113_((String)"\u00a7eYou left the party."));
        SocialManager.syncPlayer(p);
        String pName = p.m_7755_().getString();
        for (UUID uid : pt.members) {
            ServerPlayer sp = p.f_8924_.m_6846_().m_11259_(uid);
            if (sp == null) continue;
            sp.m_213846_((Component)Component.m_237113_((String)("\u00a7e" + pName + " has left the party.")));
            SocialManager.syncPlayer(sp);
        }
    }

    private static void inviteToParty(ServerPlayer s, String t) {
        ServerPlayer tp = s.f_8924_.m_6846_().m_11255_(t);
        if (tp == null) {
            return;
        }
        Party pt = playerToParty.get(s.m_20148_());
        if (pt == null) {
            pt = new Party(s);
            playerToParty.put(s.m_20148_(), pt);
        }
        if (pt.isOwner(s.m_20148_())) {
            if (playerToParty.containsKey(tp.m_20148_())) {
                s.m_213846_((Component)Component.m_237113_((String)("\u00a7c" + t + " is already in a party.")));
                return;
            }
            pendingPartyInvites.put(tp.m_20148_(), s.m_20148_());
            SAOPacketHandler.sendToPlayer(new InviteRequestPacket(s.m_7755_().getString(), InviteRequestPacket.Type.PARTY, ""), tp);
            s.m_213846_((Component)Component.m_237113_((String)("\u00a7aInvited " + t + " to party!")));
        }
    }

    private static void acceptPartyInvite(ServerPlayer p) {
        UUID inviterId = pendingPartyInvites.remove(p.m_20148_());
        if (inviterId == null) {
            return;
        }
        Party pt = playerToParty.get(inviterId);
        if (pt != null) {
            pt.addMember(p);
            playerToParty.put(p.m_20148_(), pt);
            p.m_213846_((Component)Component.m_237113_((String)"\u00a7aYou joined the party!"));
            for (UUID uid : pt.members) {
                ServerPlayer sp = p.f_8924_.m_6846_().m_11259_(uid);
                if (sp == null) continue;
                SocialManager.syncPlayer(sp);
            }
        }
    }

    private static void kickFromParty(ServerPlayer s, String t) {
        ServerPlayer tp;
        Party pt = playerToParty.get(s.m_20148_());
        if (pt != null && pt.isOwner(s.m_20148_()) && (tp = s.f_8924_.m_6846_().m_11255_(t)) != null && pt.members.contains(tp.m_20148_()) && !tp.m_20148_().equals(s.m_20148_())) {
            pt.removeMember(tp.m_20148_());
            playerToParty.remove(tp.m_20148_());
            tp.m_213846_((Component)Component.m_237113_((String)"\u00a7cYou were kicked from the party."));
            s.m_213846_((Component)Component.m_237113_((String)("\u00a7aKicked " + t + " from the party.")));
            SocialManager.syncPlayer(tp);
            for (UUID uid : pt.members) {
                ServerPlayer sp = s.f_8924_.m_6846_().m_11259_(uid);
                if (sp == null) continue;
                SocialManager.syncPlayer(sp);
            }
        }
    }

    private static void promoteInParty(ServerPlayer s, String t) {
        ServerPlayer tp;
        Party pt = playerToParty.get(s.m_20148_());
        if (pt != null && pt.isOwner(s.m_20148_()) && (tp = s.f_8924_.m_6846_().m_11255_(t)) != null && pt.members.contains(tp.m_20148_()) && !tp.m_20148_().equals(s.m_20148_())) {
            pt.owner = tp.m_20148_();
            tp.m_213846_((Component)Component.m_237113_((String)"\u00a7aYou are now the party leader!"));
            s.m_213846_((Component)Component.m_237113_((String)("\u00a7aTransfered party leadership to " + t + ".")));
            for (UUID uid : pt.members) {
                ServerPlayer sp = s.f_8924_.m_6846_().m_11259_(uid);
                if (sp == null) continue;
                SocialManager.syncPlayer(sp);
            }
        }
    }

    public static void syncPlayer(ServerPlayer p) {
        String gn = "";
        String eu = "";
        String r = "";
        Guild g = playerToGuild.get(p.m_20148_());
        if (g != null) {
            gn = g.getName();
            eu = g.getEmblemUrl();
            r = g.getRole(p.m_20148_()).name();
        }
        boolean ip = playerToParty.containsKey(p.m_20148_());
        ArrayList<String> pm = new ArrayList<String>();
        if (ip) {
            Party pt = playerToParty.get(p.m_20148_());
            String leaderName = pt.memberNames.get(pt.owner);
            if (leaderName != null) {
                pm.add("[L] " + (String)leaderName);
            }
            for (Map.Entry<UUID, String> entry : pt.memberNames.entrySet()) {
                if (entry.getKey().equals(pt.owner)) continue;
                pm.add(entry.getValue());
            }
        }
        ArrayList<String> gm = new ArrayList<String>();
        if (g != null) {
            for (Map.Entry entry : g.members.entrySet()) {
                String name = g.memberNames.get(entry.getKey());
                if (entry.getValue() == Role.LEADER) {
                    gm.add("[L] " + name);
                    continue;
                }
                if (entry.getValue() == Role.OFFICER) {
                    gm.add("[O] " + name);
                    continue;
                }
                gm.add(name);
            }
        }
        ArrayList<String> fl = new ArrayList<String>();
        Set set = playerFriends.getOrDefault(p.m_20148_(), new HashSet());
        for (UUID fid : set) {
            ServerPlayer fp = p.f_8924_.m_6846_().m_11259_(fid);
            if (fp != null && !fp.m_9232_()) {
                fl.add(fid.toString() + "|" + fp.m_7755_().getString() + "|true");
                continue;
            }
            String offName = p.f_8924_.m_129927_().m_11002_(fid).map(GameProfile::getName).orElse("Unknown");
            fl.add(fid.toString() + "|" + offName + "|false");
        }
        SAOPacketHandler.sendToPlayer(new SocialSyncPacket(gn, eu, r, ip, pm, gm, fl), p);
    }

    public static void handleChatMessage(ServerPlayer sender, ChatPacket msg) {
        String senderName = sender.m_7755_().getString();
        ChatPacket out = new ChatPacket(msg.getChannel(), senderName, msg.getMessage(), msg.getTargetTag());
        switch (msg.getChannel()) {
            case PARTY: {
                Party pt = playerToParty.get(sender.m_20148_());
                if (pt == null) break;
                for (UUID uid : pt.members) {
                    ServerPlayer p = sender.f_8924_.m_6846_().m_11259_(uid);
                    if (p == null) continue;
                    SAOPacketHandler.sendToPlayer(out, p);
                }
                break;
            }
            case GUILD: {
                Guild gl = playerToGuild.get(sender.m_20148_());
                if (gl == null) break;
                for (UUID uid : gl.getMembers().keySet()) {
                    ServerPlayer p = sender.f_8924_.m_6846_().m_11259_(uid);
                    if (p == null) continue;
                    SAOPacketHandler.sendToPlayer(out, p);
                }
                break;
            }
            case PRIVATE: {
                ServerPlayer tp = sender.f_8924_.m_6846_().m_11255_(msg.getTargetTag());
                if (tp == null) break;
                SAOPacketHandler.sendToPlayer(out, tp);
                SAOPacketHandler.sendToPlayer(out, sender);
                break;
            }
            case SYSTEM: {
                SAOPacketHandler.sendToPlayer(out, sender);
            }
        }
    }

    private static void createGuild(ServerPlayer p, String name) {
        if (playerToGuild.containsKey(p.m_20148_())) {
            return;
        }
        Guild g = new Guild(name, p);
        allGuilds.add(g);
        playerToGuild.put(p.m_20148_(), g);
        p.m_213846_((Component)Component.m_237113_((String)("\u00a76Guild '" + name + "' created!")));
        SocialManager.syncPlayer(p);
        SocialManager.broadcastGlobalEmblems(p.f_8924_);
    }

    private static void leaveGuild(ServerPlayer p) {
        Guild g = playerToGuild.get(p.m_20148_());
        if (g == null) {
            return;
        }
        Role rl = g.getRole(p.m_20148_());
        g.removeMember(p.m_20148_());
        playerToGuild.remove(p.m_20148_());
        if (rl == Role.LEADER && !g.members.isEmpty()) {
            UUID next = g.members.keySet().iterator().next();
            g.members.put(next, Role.LEADER);
            ServerPlayer nl = p.f_8924_.m_6846_().m_11259_(next);
            if (nl != null) {
                SocialManager.syncPlayer(nl);
            }
        } else if (rl == Role.LEADER && g.members.isEmpty()) {
            allGuilds.remove(g);
        }
        p.m_213846_((Component)Component.m_237113_((String)"\u00a7eLeft guild."));
        SocialManager.syncPlayer(p);
        String pName = p.m_7755_().getString();
        for (UUID uid : g.getMembers().keySet()) {
            ServerPlayer sp = p.f_8924_.m_6846_().m_11259_(uid);
            if (sp == null) continue;
            sp.m_213846_((Component)Component.m_237113_((String)("\u00a7e" + pName + " has left the guild.")));
            SocialManager.syncPlayer(sp);
        }
        SocialManager.broadcastGlobalEmblems(p.f_8924_);
    }

    private static void inviteToGuild(ServerPlayer s, String t) {
        ServerPlayer tp;
        Guild g = playerToGuild.get(s.m_20148_());
        if (g == null) {
            return;
        }
        Role rl = g.getRole(s.m_20148_());
        if (!(rl != Role.LEADER && rl != Role.OFFICER || (tp = s.f_8924_.m_6846_().m_11255_(t)) == null || playerToGuild.containsKey(tp.m_20148_()))) {
            pendingGuildInvites.put(tp.m_20148_(), g);
            SAOPacketHandler.sendToPlayer(new InviteRequestPacket(s.m_7755_().getString(), InviteRequestPacket.Type.GUILD, g.getName()), tp);
            s.m_213846_((Component)Component.m_237113_((String)("\u00a7aInvited " + t + " to guild!")));
        }
    }

    private static void acceptGuildInvite(ServerPlayer p) {
        Guild g = pendingGuildInvites.remove(p.m_20148_());
        if (g == null) {
            return;
        }
        if (!playerToGuild.containsKey(p.m_20148_())) {
            g.addMember(p.m_20148_(), p.m_7755_().getString(), Role.MEMBER);
            playerToGuild.put(p.m_20148_(), g);
            p.m_213846_((Component)Component.m_237113_((String)("\u00a7aYou joined the guild '" + g.getName() + "'!")));
            for (UUID uid : g.getMembers().keySet()) {
                ServerPlayer sp = p.f_8924_.m_6846_().m_11259_(uid);
                if (sp == null) continue;
                SocialManager.syncPlayer(sp);
            }
            SocialManager.broadcastGlobalEmblems(p.f_8924_);
        }
    }

    public static Guild getPlayerGuild(UUID uuid) {
        return playerToGuild.get(uuid);
    }

    public static Party getPlayerParty(UUID uuid) {
        return playerToParty.get(uuid);
    }

    public static class Guild {
        public String name;
        public String emblemUrl = "";
        public Map<UUID, Role> members = new HashMap<UUID, Role>();
        public Map<UUID, String> memberNames = new HashMap<UUID, String>();

        public Guild() {
        }

        public Guild(String name, ServerPlayer creator) {
            this.name = name;
            this.addMember(creator.m_20148_(), creator.m_7755_().getString(), Role.LEADER);
        }

        public void addMember(UUID uuid, String name, Role role) {
            this.members.put(uuid, role);
            this.memberNames.put(uuid, name);
        }

        public void removeMember(UUID uuid) {
            this.members.remove(uuid);
            this.memberNames.remove(uuid);
        }

        public Role getRole(UUID uuid) {
            return this.members.getOrDefault(uuid, Role.MEMBER);
        }

        public String getName() {
            return this.name;
        }

        public String getEmblemUrl() {
            return this.emblemUrl;
        }

        public void setEmblemUrl(String url) {
            this.emblemUrl = url;
        }

        public Map<UUID, Role> getMembers() {
            return this.members;
        }
    }

    public static enum Role {
        LEADER,
        OFFICER,
        MEMBER;

    }

    public static class Party {
        public UUID owner;
        public Set<UUID> members = new HashSet<UUID>();
        public Map<UUID, String> memberNames = new HashMap<UUID, String>();

        public Party(ServerPlayer owner) {
            this.owner = owner.m_20148_();
            this.addMember(owner);
        }

        public void addMember(ServerPlayer player) {
            this.members.add(player.m_20148_());
            this.memberNames.put(player.m_20148_(), player.m_7755_().getString());
        }

        public void removeMember(UUID uuid) {
            this.members.remove(uuid);
            this.memberNames.remove(uuid);
        }

        public boolean isOwner(UUID uuid) {
            return this.owner.equals(uuid);
        }
    }

    @Mod.EventBusSubscriber(modid="saomcui", bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class SocialEvents {
        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            Player player = event.getEntity();
            if (player instanceof ServerPlayer) {
                ServerPlayer sp = (ServerPlayer)player;
                SocialManager.syncPlayer(sp);
                SocialManager.broadcastGlobalEmblems(sp.f_8924_);
                for (Map.Entry entry : playerFriends.entrySet()) {
                    ServerPlayer fp;
                    if (!((Set)entry.getValue()).contains(sp.m_20148_()) || (fp = sp.f_8924_.m_6846_().m_11259_((UUID)entry.getKey())) == null) continue;
                    SocialManager.syncPlayer(fp);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
            Player player = event.getEntity();
            if (player instanceof ServerPlayer) {
                ServerPlayer sp = (ServerPlayer)player;
                for (Map.Entry entry : playerFriends.entrySet()) {
                    ServerPlayer fp;
                    if (!((Set)entry.getValue()).contains(sp.m_20148_()) || (fp = sp.f_8924_.m_6846_().m_11259_((UUID)entry.getKey())) == null || fp.m_20148_().equals(sp.m_20148_())) continue;
                    SocialManager.syncPlayer(fp);
                }
            }
        }
    }
}

