package com.drd.trickytrialsbackport;

import com.drd.trickytrialsbackport.advancement.ModCriteriaTriggers;
import com.drd.trickytrialsbackport.registry.*;

public final class TrickyTrialsBackport {
    public static final String MOD_ID = "trickytrialsbackport";
    public static final String NAMESPACE = "minecraft";

    public static void init() {
        ModSounds.register();
        ModBannerPatterns.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModEffects.register();
        ModEntities.register();
        ModItems.register();
        ModLootFunctionTypes.register();
        ModMemoryModuleTypes.register();
        ModMenuTypes.register();
        ModPaintings.register();
        ModParticles.register();
        ModSensorTypes.register();
        ModSherds.register();

        ModCriteriaTriggers.register();
    }
}
