/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package com.ag.saomcui.network;

import com.ag.saomcui.SAOSounds;
import com.ag.saomcui.client.SAODeathEffects;
import com.ag.saomcui.network.SAOPacketHandler;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid="saomcui")
public record DeathEffectPacket(UUID entity, ResourceLocation dimension, double x, double y, double z, float width, float height) {
    public DeathEffectPacket(FriendlyByteBuf b) {
        this(b.m_130259_(), b.m_130281_(), b.readDouble(), b.readDouble(), b.readDouble(), b.readFloat(), b.readFloat());
    }

    public void write(FriendlyByteBuf b) {
        b.m_130077_(this.entity);
        b.m_130085_(this.dimension);
        b.writeDouble(this.x);
        b.writeDouble(this.y);
        b.writeDouble(this.z);
        b.writeFloat(this.width);
        b.writeFloat(this.height);
    }

    public boolean valid() {
        return Double.isFinite(this.x) && Double.isFinite(this.y) && Double.isFinite(this.z) && Math.abs(this.x) <= 3.0E7 && Math.abs(this.z) <= 3.0E7 && Math.abs(this.y) <= 3.0E7 && Float.isFinite(this.width) && Float.isFinite(this.height) && this.width > 0.0f && this.height > 0.0f;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            if (this.valid()) {
                DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> SAODeathEffects.receive(this));
            }
        });
        ctx.setPacketHandled(true);
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void death(LivingDeathEvent event) {
        LivingEntity e = event.getEntity();
        if (e.m_9236_().f_46443_ || event.isCanceled()) {
            return;
        }
        e.m_9236_().m_6263_(null, e.m_20185_(), e.m_20186_(), e.m_20189_(), (SoundEvent)SAOSounds.DEATH.get(), SoundSource.PLAYERS, 2.0f, 1.0f);
        SAOPacketHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e), (Object)new DeathEffectPacket(e.m_20148_(), e.m_9236_().m_46472_().m_135782_(), e.m_20185_(), e.m_20186_(), e.m_20189_(), e.m_20205_(), e.m_20206_()));
    }
}

