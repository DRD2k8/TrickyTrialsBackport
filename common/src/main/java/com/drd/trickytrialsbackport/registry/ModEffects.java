package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.mixin.MobEffectInvoker;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.function.Supplier;

public class ModEffects {
    public static Supplier<MobEffect> TRIAL_OMEN;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        TRIAL_OMEN = helper.registerAuto(Registries.MOB_EFFECT, "trial_omen", () -> MobEffectInvoker.create(MobEffectCategory.NEUTRAL, 1484454));
    }
}
