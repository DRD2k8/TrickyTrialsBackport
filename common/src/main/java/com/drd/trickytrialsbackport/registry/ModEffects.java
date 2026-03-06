package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.effect.InfestedEffect;
import com.drd.trickytrialsbackport.mixin.MobEffectInvoker;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.function.Supplier;

public class ModEffects {
    public static Supplier<MobEffect> INFESTED;
    public static Supplier<MobEffect> RAID_OMEN;
    public static Supplier<MobEffect> TRIAL_OMEN;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        INFESTED = helper.registerAuto(Registries.MOB_EFFECT, "infested", () -> new InfestedEffect(MobEffectCategory.HARMFUL, 9214860));
        RAID_OMEN = helper.registerAuto(Registries.MOB_EFFECT, "raid_omen", () -> MobEffectInvoker.create(MobEffectCategory.NEUTRAL, 14565464));
        TRIAL_OMEN = helper.registerAuto(Registries.MOB_EFFECT, "trial_omen", () -> MobEffectInvoker.create(MobEffectCategory.NEUTRAL, 1484454));
    }
}
