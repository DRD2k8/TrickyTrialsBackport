package com.drd.trickytrialsbackport.client.renderer.entity;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.client.model.BoggedModel;
import com.drd.trickytrialsbackport.client.registry.ModModelLayers;
import com.drd.trickytrialsbackport.client.renderer.entity.layer.BoggedOuterLayer;
import com.drd.trickytrialsbackport.entity.monster.Bogged;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BoggedRenderer extends MobRenderer<Bogged, BoggedModel<Bogged>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TrickyTrialsBackport.NAMESPACE, "textures/entity/skeleton/bogged.png");

    public BoggedRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BoggedModel<>(ctx.bakeLayer(ModModelLayers.BOGGED)), 0.5F);

        this.addLayer(new BoggedOuterLayer<>(this, ctx.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Bogged entity) {
        return TEXTURE;
    }
}
