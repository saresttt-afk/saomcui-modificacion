/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.ag.saomcui;

import com.ag.saomcui.client.SAOClientOnly;
import com.ag.saomcui.config.SAOConfig;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="saomcui", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class SAOBlockInteractionHandler {
    @SubscribeEvent
    public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
        if (!SAOConfig.enableCrafting) {
            return;
        }
        if (event.getLevel().m_8055_(event.getPos()).m_60713_(Blocks.f_50091_)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            if (event.getLevel().f_46443_) {
                DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> SAOClientOnly.openMainMenu(true));
            }
        }
    }
}

