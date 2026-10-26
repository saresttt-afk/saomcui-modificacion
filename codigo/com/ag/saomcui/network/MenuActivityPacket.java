/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.ag.saomcui.network;

import com.ag.saomcui.client.SAOMenuPresence;
import com.ag.saomcui.network.PublicMenuView;
import com.ag.saomcui.network.SAOPacketHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class MenuActivityPacket {
    public final Section section;
    public final PublicMenuView view;

    public MenuActivityPacket(Section section) {
        this(section, PublicMenuView.EMPTY);
    }

    public MenuActivityPacket(Section section, PublicMenuView view) {
        this.section = section;
        this.view = view.publicOnly(section);
    }

    public MenuActivityPacket(FriendlyByteBuf buf) {
        this((Section)buf.m_130066_(Section.class), new PublicMenuView(buf));
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130068_((Enum)this.section);
        this.view.write(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender == null) {
                return;
            }
            long tick = sender.m_284548_().m_46467_();
            long last = sender.getPersistentData().m_128454_("saomcui.activityTick");
            if (this.section == Section.CLOSED && sender.getPersistentData().m_128471_("saomcui.activityClosed")) {
                return;
            }
            if (this.section != Section.CLOSED && tick >= last && tick - last < 4L) {
                return;
            }
            sender.getPersistentData().m_128356_("saomcui.activityTick", tick);
            sender.getPersistentData().m_128379_("saomcui.activityClosed", this.section == Section.CLOSED);
            Delivery delivery = new Delivery(sender.m_20148_(), this.section, this.view, PublicMenuView.inventory(sender, this.section));
            for (ServerPlayer observer : sender.m_284548_().m_6907_()) {
                if (observer == sender || !(observer.m_20280_((Entity)sender) <= 256.0) || !observer.m_142582_((Entity)sender) || sender.m_20177_((Player)observer) || sender.m_6144_()) continue;
                SAOPacketHandler.sendToPlayer(delivery, observer);
            }
        });
        ctx.setPacketHandled(true);
    }

    public static enum Section {
        CLOSED(""),
        MENU("Menu"),
        INVENTORY("Inventory / Equipment"),
        CRAFTING("Crafting"),
        SOCIAL("Friends / Party / Guild"),
        MESSAGES("Messages"),
        MAP("Map / Waypoints"),
        SETTINGS("UI Settings");

        public final String label;

        private Section(String label) {
            this.label = label;
        }
    }

    public static final class Delivery {
        public final UUID player;
        public final Section section;
        public final PublicMenuView view;
        public final List<PublicMenuView.Item> items;

        public Delivery(UUID player, Section section) {
            this(player, section, PublicMenuView.EMPTY, List.of());
        }

        public Delivery(UUID player, Section section, PublicMenuView view, List<PublicMenuView.Item> items) {
            this.player = player;
            this.section = section;
            this.view = view.publicOnly(section);
            this.items = section == Section.INVENTORY || section == Section.CRAFTING ? List.copyOf(items.subList(0, Math.min(41, items.size()))) : List.of();
        }

        public Delivery(FriendlyByteBuf buf) {
            this.player = buf.m_130259_();
            this.section = (Section)buf.m_130066_(Section.class);
            this.view = new PublicMenuView(buf).publicOnly(this.section);
            int count = buf.m_130242_();
            if (count < 0 || count > 41) {
                throw new IllegalArgumentException("Menu item limit");
            }
            ArrayList<PublicMenuView.Item> read = new ArrayList<PublicMenuView.Item>();
            for (int i = 0; i < count; ++i) {
                read.add(new PublicMenuView.Item(buf));
            }
            this.items = this.section == Section.INVENTORY || this.section == Section.CRAFTING ? List.copyOf(read) : List.of();
        }

        public void write(FriendlyByteBuf buf) {
            buf.m_130077_(this.player);
            buf.m_130068_((Enum)this.section);
            this.view.write(buf);
            buf.m_130130_(this.items.size());
            this.items.forEach(i -> i.write(buf));
        }

        public void handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();
            ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> SAOMenuPresence.receive(this)));
            ctx.setPacketHandled(true);
        }
    }
}

