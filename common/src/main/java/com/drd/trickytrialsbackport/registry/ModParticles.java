package com.drd.trickytrialsbackport.registry;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class ModParticles {
    public static Supplier<SimpleParticleType> GUST;
    public static Supplier<SimpleParticleType> GUST_EMITTER_LARGE;
    public static Supplier<SimpleParticleType> GUST_EMITTER_SMALL;
    public static Supplier<SimpleParticleType> OMINOUS_SPAWNING;
    public static Supplier<SimpleParticleType> SMALL_GUST;
    public static Supplier<SimpleParticleType> TRIAL_OMEN;
    public static Supplier<SimpleParticleType> TRIAL_SPAWNER_DETECTED_PLAYER;
    public static Supplier<SimpleParticleType> TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS;
    public static Supplier<SimpleParticleType> VAULT_CONNECTION;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        GUST = helper.registerAuto(Registries.PARTICLE_TYPE, "gust",
                () -> new SimpleParticleType(true) {});
        GUST_EMITTER_LARGE = helper.registerAuto(Registries.PARTICLE_TYPE, "gust_emitter_large",
                () -> new SimpleParticleType(true) {});
        GUST_EMITTER_SMALL = helper.registerAuto(Registries.PARTICLE_TYPE, "gust_emitter_small",
                () -> new SimpleParticleType(true) {});
        OMINOUS_SPAWNING = helper.registerAuto(Registries.PARTICLE_TYPE, "ominous_spawning",
                () -> new SimpleParticleType(true) {});
        SMALL_GUST = helper.registerAuto(Registries.PARTICLE_TYPE, "small_gust",
                () -> new SimpleParticleType(true) {});
        TRIAL_OMEN = helper.registerAuto(Registries.PARTICLE_TYPE, "trial_omen",
                () -> new SimpleParticleType(false) {});
        TRIAL_SPAWNER_DETECTED_PLAYER = helper.registerAuto(Registries.PARTICLE_TYPE, "trial_spawner_detection",
                () -> new SimpleParticleType(true) {});
        TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS = helper.registerAuto(Registries.PARTICLE_TYPE, "trial_spawner_detection_ominous",
                () -> new SimpleParticleType(true) {});
        VAULT_CONNECTION = helper.registerAuto(Registries.PARTICLE_TYPE, "vault_connection",
                () -> new SimpleParticleType(true) {});
    }
}
