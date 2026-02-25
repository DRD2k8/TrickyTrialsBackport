package com.drd.trickytrialsbackport.compat.vanillabackport.client;

import com.blackgear.platform.client.v2.render.DynamicItemRenderer;
import com.drd.trickytrialsbackport.compat.vanillabackport.client.renderer.ModSpawnEggRenderer;

public class VanillaBackportClientEvents {
    public static void specialModels() {
        ModSpawnEggRenderer.SPAWN_EGGS.forEach(item -> DynamicItemRenderer.INSTANCE.get().register(item, new ModSpawnEggRenderer()));
    }
}
