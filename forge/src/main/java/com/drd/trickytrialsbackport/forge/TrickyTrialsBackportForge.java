package com.drd.trickytrialsbackport.forge;

import com.drd.trickytrialsbackport.forge.registry.ForgeRegistryHelper;
import com.drd.trickytrialsbackport.registry.RegistryHelper;
import net.minecraftforge.fml.common.Mod;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;

@Mod(TrickyTrialsBackport.MOD_ID)
public final class TrickyTrialsBackportForge {
    public TrickyTrialsBackportForge() {
        RegistryHelper.setInstance(new ForgeRegistryHelper());

        TrickyTrialsBackport.init();
    }
}
