/*
 * Decompiled with CFR 0.152.
 */
package com.ag.saomcui.client;

import com.ag.saomcui.network.ChatPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SAOChatManager {
    private static final Map<ChatPacket.Channel, List<ChatMessage>> messages = new HashMap<ChatPacket.Channel, List<ChatMessage>>();
    public static boolean hasUnreadMessages;

    public static void receiveMessage(ChatPacket.Channel channel, String sender, String text, String target) {
        messages.get((Object)channel).add(new ChatMessage(sender, text, target));
        if (messages.get((Object)channel).size() > 100) {
            messages.get((Object)channel).remove(0);
        }
        if (channel != ChatPacket.Channel.SYSTEM) {
            hasUnreadMessages = true;
        }
    }

    public static List<ChatMessage> getMessages(ChatPacket.Channel channel) {
        return messages.get((Object)channel);
    }

    public static void clear() {
        for (ChatPacket.Channel c : ChatPacket.Channel.values()) {
            messages.get((Object)c).clear();
        }
        hasUnreadMessages = false;
    }

    static {
        for (ChatPacket.Channel c : ChatPacket.Channel.values()) {
            messages.put(c, new ArrayList());
        }
        hasUnreadMessages = false;
    }

    public static class ChatMessage {
        public final String sender;
        public final String message;
        public final String target;
        public final long timestamp;

        public ChatMessage(String sender, String message, String target) {
            this.sender = sender;
            this.message = message;
            this.target = target;
            this.timestamp = System.currentTimeMillis();
        }
    }
}

