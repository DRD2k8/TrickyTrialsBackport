package com.drd.trickytrialsbackport;

import com.drd.trickytrialsbackport.registry.*;

public final class TrickyTrialsBackport {
    public static final String MOD_ID = "trickytrialsbackport";
    public static final String NAMESPACE = "minecraft";

    public static void init() {
        ModSounds.register();
        ModBannerPatterns.register();
        ModBlocks.register();
        ModEntities.register();
        ModItems.register();
        ModMemoryModuleTypes.register();
        ModParticles.register();
        ModSensorTypes.register();
    }
}
