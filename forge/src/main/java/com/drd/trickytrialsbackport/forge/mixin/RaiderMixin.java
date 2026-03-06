package com.drd.trickytrialsbackport.forge.mixin;

import com.drd.trickytrialsbackport.registry.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.raid.Raider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raider.class)
public abstract class RaiderMixin {
    @Inject(
            method = "die",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
            ),
            cancellable = true,
            remap = false
    )
    private void replaceBadOmenWithBottle(DamageSource source, CallbackInfo ci) {
        Raider raider = (Raider)(Object)this;

        if (raider.isPatrolLeader()) {
            raider.spawnAtLocation(ModItems.OMINOUS_BOTTLE.get());

            ci.cancel();
        }
    }
}
