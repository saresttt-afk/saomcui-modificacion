/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.TransientCraftingContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.Level
 */
package com.ag.saomcui.inventory;

import com.ag.saomcui.inventory.SAOForgeEventHandler;
import com.ag.saomcui.inventory.SAOInventory;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class SAOCraftingHandler {
    public static void handleCraft(ServerPlayer player, ItemStack[] grid, boolean is3x3, boolean shiftCraft) {
        player.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
            int size = is3x3 ? 3 : 2;
            TransientCraftingContainer container = new TransientCraftingContainer(new AbstractContainerMenu(null, -1){

                public ItemStack m_7648_(Player p, int slot) {
                    return ItemStack.f_41583_;
                }

                public boolean m_6875_(Player p) {
                    return true;
                }
            }, size, size);
            for (int i = 0; i < size * size; ++i) {
                container.m_6836_(i, grid[i] == null ? ItemStack.f_41583_ : grid[i]);
            }
            Optional recipeOpt = player.m_284548_().m_7465_().m_44015_(RecipeType.f_44107_, (Container)container, (Level)player.m_284548_());
            if (!recipeOpt.isPresent()) {
                return;
            }
            CraftingRecipe recipe = (CraftingRecipe)recipeOpt.get();
            ItemStack result = recipe.m_5874_((Container)container, player.m_284548_().m_9598_());
            if (result.m_41619_()) {
                return;
            }
            ArrayList<ItemStack> needed = new ArrayList<ItemStack>();
            for (int i = 0; i < size * size; ++i) {
                if (grid[i] == null || grid[i].m_41619_()) continue;
                boolean found = false;
                for (ItemStack n : needed) {
                    if (!ItemStack.m_150942_((ItemStack)n, (ItemStack)grid[i])) continue;
                    n.m_41769_(1);
                    found = true;
                    break;
                }
                if (found) continue;
                ItemStack copy = grid[i].m_41777_();
                copy.m_41764_(1);
                needed.add(copy);
            }
            int craftTimes = shiftCraft ? 999 : 1;
            boolean craftedAny = false;
            for (int loop = 0; loop < craftTimes; ++loop) {
                SAOInventory.SAOStack sao;
                int slot;
                boolean canCraft = true;
                for (ItemStack req : needed) {
                    int available = 0;
                    for (slot = 0; slot < inv.getSlots().size(); ++slot) {
                        sao = inv.getStackInSlot(slot);
                        if (!ItemStack.m_150942_((ItemStack)sao.item, (ItemStack)req)) continue;
                        available += sao.count;
                    }
                    if (available >= req.m_41613_()) continue;
                    canCraft = false;
                    break;
                }
                if (!canCraft) break;
                for (ItemStack req : needed) {
                    int remainingToDeduct = req.m_41613_();
                    for (slot = 0; slot < inv.getSlots().size() && remainingToDeduct > 0; ++slot) {
                        sao = inv.getStackInSlot(slot);
                        if (!ItemStack.m_150942_((ItemStack)sao.item, (ItemStack)req)) continue;
                        int take = Math.min(sao.count, remainingToDeduct);
                        sao.count -= take;
                        remainingToDeduct -= take;
                        if (sao.count > 0) continue;
                        inv.setStackInSlot(slot, ItemStack.f_41583_, 0);
                    }
                }
                inv.insertItem(result.m_41777_());
                craftedAny = true;
            }
            if (!craftedAny) {
                return;
            }
            player.m_9236_().m_5594_(null, player.m_20183_(), SoundEvents.f_12494_, SoundSource.PLAYERS, 1.0f, 1.0f);
            SAOForgeEventHandler.syncInventory(player);
        });
    }
}

