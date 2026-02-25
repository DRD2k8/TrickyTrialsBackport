package com.drd.trickytrialsbackport.client.renderer.layer;

import com.drd.trickytrialsbackport.client.model.BreezeModel;
import com.drd.trickytrialsbackport.client.registry.ModModelLayers;
import com.drd.trickytrialsbackport.client.registry.ModRenderTypes;
import com.drd.trickytrialsbackport.entity.monster.breeze.Breeze;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BreezeEyesLayer extends RenderLayer<Breeze, BreezeModel<Breeze>> {
    private final ResourceLocation textureLoc;
    private final BreezeModel<Breeze> model;

    public BreezeEyesLayer(RenderLayerParent<Breeze, BreezeModel<Breeze>> p_310165_, EntityModelSet p_312112_, ResourceLocation p_309706_) {
        super(p_310165_);
        this.model = new BreezeModel(p_312112_.bakeLayer(ModModelLayers.BREEZE_EYES));
        this.textureLoc = p_309706_;
    }

    public void render(PoseStack p_312911_, MultiBufferSource p_312666_, int p_311532_, Breeze p_311391_, float p_311193_, float p_309423_, float p_310215_, float p_311406_, float p_311840_, float p_312197_) {
        this.model.prepareMobModel(p_311391_, p_311193_, p_309423_, p_310215_);
        ((BreezeModel)this.getParentModel()).copyPropertiesTo(this.model);
        VertexConsumer $$10 = p_312666_.getBuffer(ModRenderTypes.breezeEyes(this.textureLoc));
        this.model.setupAnim(p_311391_, p_311193_, p_309423_, p_311406_, p_311840_, p_312197_);
        this.model.root().render(p_312911_, $$10, p_311532_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected ResourceLocation getTextureLocation(Breeze p_312494_) {
        return this.textureLoc;
    }
}
