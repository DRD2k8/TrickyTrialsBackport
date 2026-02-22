package com.drd.trickytrialsbackport.fabric;

import com.drd.trickytrialsbackport.fabric.item.CreativeTabPlacements;
import com.drd.trickytrialsbackport.fabric.registry.FabricRegistryHelper;
import com.drd.trickytrialsbackport.registry.RegistryHelper;
import com.drd.trickytrialsbackport.util.ModSoundTypes;
import net.fabricmc.api.ModInitializer;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;

public final class TrickyTrialsBackportFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RegistryHelper.setInstance(new FabricRegistryHelper());

        TrickyTrialsBackport.init();
        CreativeTabPlacements.registerTabPlacements();

        ModSoundTypes.register();
    }
}
