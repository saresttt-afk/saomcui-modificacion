/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.ag.saomcui.network;

import com.ag.saomcui.inventory.SAOForgeEventHandler;
import com.ag.saomcui.inventory.SAOInventory;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public record ItemActionPacket(int slot, ActionType action) {
    public ItemActionPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt(), (ActionType)buffer.m_130066_(ActionType.class));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.slot);
        buffer.m_130068_((Enum)this.action);
    }

    public static void handle(ItemActionPacket payload, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (!(player instanceof ServerPlayer)) {
                return;
            }
            ServerPlayer sp = player;
            int slot = payload.slot();
            if (slot < 0) {
                return;
            }
            if (payload.action() == ActionType.STOW_HOTBAR) {
                if (slot >= 9) {
                    return;
                }
                ItemStack stack = sp.m_150109_().m_8020_(slot);
                if (!stack.m_41619_()) {
                    sp.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
                        ItemStack remainder = inv.insertItem(stack.m_41777_());
                        sp.m_150109_().m_6836_(slot, remainder);
                        SAOForgeEventHandler.syncInventory(sp);
                        sp.m_150109_().m_6596_();
                        sp.f_36096_.m_38946_();
                    });
                }
                return;
            }
            if (payload.action() == ActionType.UNEQUIP_ARMOR) {
                if (slot < 0 || slot >= sp.m_150109_().f_35975_.size()) {
                    return;
                }
                ItemStack armorPiece = (ItemStack)sp.m_150109_().f_35975_.get(slot);
                if (!armorPiece.m_41619_()) {
                    sp.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
                        ItemStack remainder = inv.insertItem(armorPiece.m_41777_());
                        if (remainder.m_41619_()) {
                            sp.m_150109_().f_35975_.set(slot, (Object)ItemStack.f_41583_);
                        } else {
                            sp.m_7197_(remainder, false, true);
                            sp.m_150109_().f_35975_.set(slot, (Object)ItemStack.f_41583_);
                        }
                        SAOForgeEventHandler.syncInventory(sp);
                        sp.m_150109_().m_6596_();
                        sp.f_36096_.m_38946_();
                    });
                }
                return;
            }
            sp.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
                if (slot >= inv.getSlots().size()) {
                    return;
                }
                SAOInventory.SAOStack saoStack = inv.getStackInSlot(slot);
                if (saoStack.count <= 0 || saoStack.item.m_41619_()) {
                    return;
                }
                if (payload.action() == ActionType.EQUIP_SAO) {
                    int amountToExtract = Math.min(saoStack.item.m_41741_(), saoStack.count);
                    ItemStack extracted = saoStack.item.m_41777_();
                    extracted.m_41764_(amountToExtract);
                    EquipmentSlot eSlot = Mob.m_147233_((ItemStack)extracted);
                    if (eSlot == EquipmentSlot.HEAD || eSlot == EquipmentSlot.CHEST || eSlot == EquipmentSlot.LEGS || eSlot == EquipmentSlot.FEET) {
                        ItemStack currentArmor = sp.m_6844_(eSlot).m_41777_();
                        sp.m_8061_(eSlot, extracted);
                        saoStack.count -= amountToExtract;
                        if (!currentArmor.m_41619_()) {
                            inv.insertItem(currentArmor);
                        }
                    } else {
                        boolean equipped = false;
                        for (int i = 0; i < 9; ++i) {
                            ItemStack hStack = sp.m_150109_().m_8020_(i);
                            if (!hStack.m_41619_()) continue;
                            sp.m_150109_().m_6836_(i, extracted);
                            saoStack.count -= amountToExtract;
                            equipped = true;
                            break;
                        }
                        if (!equipped) {
                            int activeSlot = sp.m_150109_().f_35977_;
                            ItemStack oldHotbarItem = sp.m_150109_().m_8020_(activeSlot).m_41777_();
                            sp.m_150109_().m_6836_(activeSlot, extracted);
                            saoStack.count -= amountToExtract;
                            inv.insertItem(oldHotbarItem);
                        }
                    }
                } else if (payload.action() == ActionType.DROP_SAO) {
                    ItemStack toDrop = saoStack.item.m_41777_();
                    int amount = Math.min(toDrop.m_41741_(), saoStack.count);
                    toDrop.m_41764_(amount);
                    saoStack.count -= amount;
                    sp.m_7197_(toDrop, false, true);
                } else if (payload.action() == ActionType.DESTROY_SAO) {
                    saoStack.count = 0;
                    saoStack.item = ItemStack.f_41583_;
                }
                if (saoStack.count <= 0) {
                    inv.setStackInSlot(slot, ItemStack.f_41583_, 0);
                }
                SAOForgeEventHandler.syncInventory(sp);
                sp.m_150109_().m_6596_();
                sp.f_36096_.m_38946_();
            });
        });
        context.setPacketHandled(true);
    }

    public static enum ActionType {
        EQUIP_SAO,
        STOW_HOTBAR,
        DROP_SAO,
        DESTROY_SAO,
        UNEQUIP_ARMOR;

    }
}

