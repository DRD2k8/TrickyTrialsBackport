package com.drd.trickytrialsbackport.forge;

import com.drd.trickytrialsbackport.forge.registry.ForgeRegistryHelper;
import com.drd.trickytrialsbackport.registry.RegistryHelper;
import com.drd.trickytrialsbackport.util.ModSoundTypes;
import net.minecraftforge.fml.common.Mod;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TrickyTrialsBackport.MOD_ID)
public final class TrickyTrialsBackportForge {
    public TrickyTrialsBackportForge() {
        RegistryHelper.setInstance(new ForgeRegistryHelper());

        TrickyTrialsBackport.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModSoundTypes.register();
        });
    }
}
