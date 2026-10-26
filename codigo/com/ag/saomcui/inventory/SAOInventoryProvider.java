/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.ICapabilitySerializable
 *  net.minecraftforge.common.util.LazyOptional
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.ag.saomcui.inventory;

import com.ag.saomcui.inventory.SAOInventory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SAOInventoryProvider
implements ICapabilitySerializable<CompoundTag> {
    private final SAOInventory inventory = new SAOInventory();
    private final LazyOptional<SAOInventory> optional = LazyOptional.of(() -> this.inventory);

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == SAOInventory.CAPABILITY) {
            return this.optional.cast();
        }
        return LazyOptional.empty();
    }

    public CompoundTag serializeNBT() {
        return this.inventory.serializeNBT();
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.inventory.deserializeNBT(nbt);
    }
}

