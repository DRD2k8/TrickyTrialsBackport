package com.drd.trickytrialsbackport.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;

@Mod(TrickyTrialsBackport.MOD_ID)
public final class TrickyTrialsBackportForge {
    public TrickyTrialsBackportForge() {
        EventBuses.registerModEventBus(TrickyTrialsBackport.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        TrickyTrialsBackport.init();
    }
}
