package com.drd.trickytrialsbackport;

import com.drd.trickytrialsbackport.registry.*;
import com.drd.trickytrialsbackport.util.CreativeModeTabContents;

public final class TrickyTrialsBackport {
    public static final String MOD_ID = "trickytrialsbackport";
    public static final String NAMESPACE = "minecraft";

    public static void init() {
        ModItems.register();

        CreativeModeTabContents.register();
    }
}
