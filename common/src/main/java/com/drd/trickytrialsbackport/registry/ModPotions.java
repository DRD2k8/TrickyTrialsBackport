package com.drd.trickytrialsbackport.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

import java.util.function.Supplier;

public class ModPotions {
    public static Supplier<Potion> INFESTED;
    public static Supplier<Potion> OOZING;
    public static Supplier<Potion> WEAVING;
    public static Supplier<Potion> WIND_CHARGED;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        INFESTED = helper.registerAuto(Registries.POTION, "infested", () -> new Potion(new MobEffectInstance(ModEffects.INFESTED.get(), 3600)));
        OOZING = helper.registerAuto(Registries.POTION, "oozing", () -> new Potion(new MobEffectInstance(ModEffects.OOZING.get(), 3600)));
        WEAVING = helper.registerAuto(Registries.POTION, "weaving", () -> new Potion(new MobEffectInstance(ModEffects.WEAVING.get(), 3600)));
        WIND_CHARGED = helper.registerAuto(Registries.POTION, "wind_charged", () -> new Potion(new MobEffectInstance(ModEffects.WIND_CHARGED.get(), 3600)));
    }
}
