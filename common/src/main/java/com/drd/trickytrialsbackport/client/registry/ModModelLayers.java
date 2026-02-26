package com.drd.trickytrialsbackport.client.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation BOGGED = register("bogged");
    public static final ModelLayerLocation BOGGED_OUTER_LAYER = register("bogged", "outer");
    public static final ModelLayerLocation BREEZE = register("breeze");
    public static final ModelLayerLocation BREEZE_EYES = register("breeze", "eyes");
    public static final ModelLayerLocation BREEZE_WIND = register("breeze", "wind");
    public static final ModelLayerLocation BREEZE_WIND_CHARGE = register("breeze_wind_charge");
    public static final ModelLayerLocation WIND_CHARGE = register("wind_charge");

    private static ModelLayerLocation register(String name) {
        return new ModelLayerLocation(new ResourceLocation(name), "main");
    }

    private static ModelLayerLocation register(String name, String layer) {
        return new ModelLayerLocation(new ResourceLocation(name), layer);
    }
}
