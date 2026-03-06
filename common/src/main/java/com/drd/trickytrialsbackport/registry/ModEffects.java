package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.effect.InfestedEffect;
import com.drd.trickytrialsbackport.effect.OozingEffect;
import com.drd.trickytrialsbackport.effect.WeavingEffect;
import com.drd.trickytrialsbackport.effect.WindChargedEffect;
import com.drd.trickytrialsbackport.mixin.MobEffectInvoker;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.function.Supplier;

public class ModEffects {
    public static Supplier<MobEffect> INFESTED;
    public static Supplier<MobEffect> OOZING;
    public static Supplier<MobEffect> RAID_OMEN;
    public static Supplier<MobEffect> TRIAL_OMEN;
    public static Supplier<MobEffect> WEAVING;
    public static Supplier<MobEffect> WIND_CHARGED;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        INFESTED = helper.registerAuto(Registries.MOB_EFFECT, "infested", () -> new InfestedEffect(MobEffectCategory.HARMFUL, 9214860));
        OOZING = helper.registerAuto(Registries.MOB_EFFECT, "oozing", () -> new OozingEffect(MobEffectCategory.HARMFUL, 10092451, random -> 2));
        RAID_OMEN = helper.registerAuto(Registries.MOB_EFFECT, "raid_omen", () -> MobEffectInvoker.create(MobEffectCategory.NEUTRAL, 14565464));
        TRIAL_OMEN = helper.registerAuto(Registries.MOB_EFFECT, "trial_omen", () -> MobEffectInvoker.create(MobEffectCategory.NEUTRAL, 1484454));
        WEAVING = helper.registerAuto(Registries.MOB_EFFECT, "weaving", () -> new WeavingEffect(MobEffectCategory.HARMFUL, 7891290, random -> Mth.randomBetweenInclusive(random, 2, 3)));
        WIND_CHARGED = helper.registerAuto(Registries.MOB_EFFECT, "wind_charged", () -> new WindChargedEffect(MobEffectCategory.HARMFUL, 12438015));
    }
}
