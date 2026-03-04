package com.drd.trickytrialsbackport.fabric;

import com.drd.trickytrialsbackport.config.CommonConfig;
import com.drd.trickytrialsbackport.fabric.item.CreativeTabPlacements;
import com.drd.trickytrialsbackport.fabric.registry.FabricRegistryHelper;
import com.drd.trickytrialsbackport.fabric.util.ModAttributeBuilders;
import com.drd.trickytrialsbackport.fabric.util.ModBiomeModifiers;
import com.drd.trickytrialsbackport.registry.RegistryHelper;
import com.drd.trickytrialsbackport.util.ModSoundTypes;
import net.fabricmc.api.ModInitializer;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import net.fabricmc.loader.api.FabricLoader;

public final class TrickyTrialsBackportFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RegistryHelper.setInstance(new FabricRegistryHelper());

        TrickyTrialsBackport.init();
        CreativeTabPlacements.registerTabPlacements();

        ModSoundTypes.register();
        ModAttributeBuilders.registerAttributes();
        ModBiomeModifiers.register();

        CommonConfig.init(FabricLoader.getInstance().getConfigDir());
    }
}
