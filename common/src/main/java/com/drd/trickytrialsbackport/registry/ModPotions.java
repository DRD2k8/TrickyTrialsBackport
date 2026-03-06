package com.drd.trickytrialsbackport.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

import java.util.function.Supplier;

public class ModPotions {
    public static Supplier<Potion> INFESTED;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        INFESTED = helper.registerAuto(Registries.POTION, "infested", () -> new Potion(new MobEffectInstance(ModEffects.INFESTED.get(), 3600)));
    }
}
