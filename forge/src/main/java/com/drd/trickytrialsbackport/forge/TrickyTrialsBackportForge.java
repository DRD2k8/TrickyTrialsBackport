package com.drd.trickytrialsbackport.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;

@Mod(TrickyTrialsBackport.MOD_ID)
public final class TrickyTrialsBackportForge {
    public TrickyTrialsBackportForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(TrickyTrialsBackport.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        TrickyTrialsBackport.init();
    }
}
