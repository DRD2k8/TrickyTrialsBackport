package com.drd.trickytrialsbackport.client.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation WIND_CHARGE = register("wind_charge");

    private static ModelLayerLocation register(String name) {
        return new ModelLayerLocation(new ResourceLocation(name), "main");
    }
}
