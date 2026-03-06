package com.drd.trickytrialsbackport.mixin;

import com.drd.trickytrialsbackport.registry.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Inject(method = "absorbBadOmen", at = @At("HEAD"), cancellable = true)
    private void useRaidOmen(Player player, CallbackInfo ci) {
        if (player.hasEffect(MobEffects.BAD_OMEN)) {
            MobEffectInstance inst = player.getEffect(MobEffects.BAD_OMEN);

            player.removeEffect(MobEffects.BAD_OMEN);

            player.addEffect(new MobEffectInstance(
                    ModEffects.RAID_OMEN.get(),
                    inst.getDuration(),
                    inst.getAmplifier(),
                    inst.isAmbient(),
                    inst.isVisible(),
                    inst.showIcon()
            ));
        }

        ci.cancel();
    }
}
