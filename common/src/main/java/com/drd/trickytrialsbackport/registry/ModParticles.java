package com.drd.trickytrialsbackport.registry;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class ModParticles {
    public static Supplier<SimpleParticleType> GUST;
    public static Supplier<SimpleParticleType> GUST_EMITTER_LARGE;
    public static Supplier<SimpleParticleType> GUST_EMITTER_SMALL;
    public static Supplier<SimpleParticleType> SMALL_GUST;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        GUST = helper.registerAuto(Registries.PARTICLE_TYPE, "gust",
                () -> new SimpleParticleType(true) {});
        GUST_EMITTER_LARGE = helper.registerAuto(Registries.PARTICLE_TYPE, "gust_emitter_large",
                () -> new SimpleParticleType(true) {});
        GUST_EMITTER_SMALL = helper.registerAuto(Registries.PARTICLE_TYPE, "gust_emitter_small",
                () -> new SimpleParticleType(true) {});
        SMALL_GUST = helper.registerAuto(Registries.PARTICLE_TYPE, "small_gust",
                () -> new SimpleParticleType(true) {});
    }
}
