/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.CapabilityManager
 *  net.minecraftforge.common.capabilities.CapabilityToken
 *  net.minecraftforge.common.util.INBTSerializable
 */
package com.ag.saomcui.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

public class SAOInventory
implements INBTSerializable<CompoundTag> {
    public static final Capability<SAOInventory> CAPABILITY = CapabilityManager.get((CapabilityToken)new CapabilityToken<SAOInventory>(){});
    public static final int MAX_SLOTS = 27;
    public static final int MAX_SLOT_SIZE = 9999;
    private final List<SAOStack> slots = new ArrayList<SAOStack>(27);

    public SAOInventory() {
        for (int i = 0; i < 27; ++i) {
            this.slots.add(new SAOStack(ItemStack.f_41583_, 0));
        }
    }

    public List<SAOStack> getSlots() {
        return this.slots;
    }

    public SAOStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= 27) {
            return new SAOStack(ItemStack.f_41583_, 0);
        }
        return this.slots.get(slot);
    }

    public void setStackInSlot(int slot, ItemStack stack, int count) {
        if (slot < 0 || slot >= 27) {
            return;
        }
        this.slots.set(slot, new SAOStack(stack, count));
    }

    public ItemStack insertItem(ItemStack stackIn) {
        SAOStack slotData;
        int i;
        if (stackIn.m_41619_()) {
            return ItemStack.f_41583_;
        }
        ItemStack stack = stackIn.m_41777_();
        int amountToInsert = stack.m_41613_();
        for (i = 0; i < 27; ++i) {
            int space;
            slotData = this.slots.get(i);
            if (slotData.item.m_41619_() || !ItemStack.m_150942_((ItemStack)slotData.item, (ItemStack)stack) || (space = 9999 - slotData.count) <= 0) continue;
            int toAdd = Math.min(amountToInsert, space);
            slotData.count += toAdd;
            if ((amountToInsert -= toAdd) > 0) continue;
            return ItemStack.f_41583_;
        }
        if (amountToInsert > 0) {
            for (i = 0; i < 27; ++i) {
                slotData = this.slots.get(i);
                if (!slotData.item.m_41619_() && slotData.count > 0) continue;
                int toAdd = Math.min(amountToInsert, 9999);
                ItemStack base = stack.m_41777_();
                base.m_41764_(1);
                this.slots.set(i, new SAOStack(base, toAdd));
                if ((amountToInsert -= toAdd) > 0) continue;
                return ItemStack.f_41583_;
            }
        }
        stack.m_41764_(amountToInsert);
        return stack;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (int i = 0; i < 27; ++i) {
            SAOStack slotData = this.slots.get(i);
            if (slotData.item.m_41619_() || slotData.count <= 0) continue;
            CompoundTag itemTag = new CompoundTag();
            itemTag.m_128405_("Slot", i);
            itemTag.m_128405_("CustomCount", slotData.count);
            ItemStack base = slotData.item.m_41777_();
            base.m_41764_(1);
            itemTag.m_128365_("ItemTag", (Tag)base.m_41739_(new CompoundTag()));
            listTag.add((Object)itemTag);
        }
        tag.m_128365_("SAOItems", (Tag)listTag);
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        for (int i = 0; i < 27; ++i) {
            this.slots.set(i, new SAOStack(ItemStack.f_41583_, 0));
        }
        ListTag listTag = nbt.m_128437_("SAOItems", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag itemTag = listTag.m_128728_(i);
            int slot = itemTag.m_128451_("Slot");
            int count = itemTag.m_128451_("CustomCount");
            ItemStack item = ItemStack.m_41712_((CompoundTag)itemTag.m_128469_("ItemTag"));
            if (slot < 0 || slot >= 27 || item.m_41619_() || count <= 0) continue;
            this.slots.set(slot, new SAOStack(item, count));
        }
    }

    public static class SAOStack {
        public ItemStack item;
        public int count;

        public SAOStack(ItemStack item, int count) {
            this.item = item;
            this.count = count;
        }
    }
}

