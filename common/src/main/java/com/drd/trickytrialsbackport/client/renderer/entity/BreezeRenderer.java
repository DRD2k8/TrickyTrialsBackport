package com.drd.trickytrialsbackport.client.renderer.entity;

import com.drd.trickytrialsbackport.client.model.BreezeModel;
import com.drd.trickytrialsbackport.client.registry.ModModelLayers;
import com.drd.trickytrialsbackport.client.renderer.entity.layer.BreezeEyesLayer;
import com.drd.trickytrialsbackport.client.renderer.entity.layer.BreezeWindLayer;
import com.drd.trickytrialsbackport.entity.monster.breeze.Breeze;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BreezeRenderer extends MobRenderer<Breeze, BreezeModel<Breeze>> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/breeze/breeze.png");
    private static final ResourceLocation WIND_TEXTURE_LOCATION = new ResourceLocation("textures/entity/breeze/breeze_wind.png");

    public BreezeRenderer(EntityRendererProvider.Context p_311628_) {
        super(p_311628_, new BreezeModel(p_311628_.bakeLayer(ModModelLayers.BREEZE)), 0.8F);
        this.addLayer(new BreezeWindLayer(this, p_311628_.getModelSet(), WIND_TEXTURE_LOCATION));
        this.addLayer(new BreezeEyesLayer(this, p_311628_.getModelSet(), TEXTURE_LOCATION));
    }

    public ResourceLocation getTextureLocation(Breeze p_312626_) {
        return TEXTURE_LOCATION;
    }
}
