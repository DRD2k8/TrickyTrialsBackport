package com.drd.trickytrialsbackport.client.registry;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ModRenderTypes {
    public static RenderType breezeWind(ResourceLocation texture, float xOffset, float yOffset) {
        return RenderType.entityTranslucent(texture);
    }

    public static RenderType breezeEyes(ResourceLocation texture) {
        return RenderType.entityTranslucentEmissive(texture, false);
    }
}
