package com.drd.trickytrialsbackport.client.renderer.entity.layer;

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

public class BreezeWindLayer extends RenderLayer<Breeze, BreezeModel<Breeze>> {
    private static final float TOP_PART_ALPHA = 1.0F;
    private static final float MIDDLE_PART_ALPHA = 1.0F;
    private static final float BOTTOM_PART_ALPHA = 1.0F;
    private final ResourceLocation textureLoc;
    private final BreezeModel<Breeze> model;

    public BreezeWindLayer(RenderLayerParent<Breeze, BreezeModel<Breeze>> p_312719_, EntityModelSet p_311451_, ResourceLocation p_312874_) {
        super(p_312719_);
        this.model = new BreezeModel(p_311451_.bakeLayer(ModModelLayers.BREEZE_WIND));
        this.textureLoc = p_312874_;
    }

    public void render(PoseStack p_312401_, MultiBufferSource p_310855_, int p_312784_, Breeze p_309942_, float p_311307_, float p_312259_, float p_311774_, float p_312816_, float p_312844_, float p_313068_) {
        float $$10 = (float)p_309942_.tickCount + p_311774_;
        this.model.prepareMobModel(p_309942_, p_311307_, p_312259_, p_311774_);
        ((BreezeModel)this.getParentModel()).copyPropertiesTo(this.model);
        VertexConsumer $$11 = p_310855_.getBuffer(ModRenderTypes.breezeWind(this.getTextureLocation(p_309942_), this.xOffset($$10) % 1.0F, 0.0F));
        this.model.setupAnim(p_309942_, p_311307_, p_312259_, p_312816_, p_312844_, p_313068_);
        this.model.windTop().skipDraw = true;
        this.model.windMiddle().skipDraw = true;
        this.model.windBottom().skipDraw = false;
        this.model.root().render(p_312401_, $$11, p_312784_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        this.model.windTop().skipDraw = true;
        this.model.windMiddle().skipDraw = false;
        this.model.windBottom().skipDraw = true;
        this.model.root().render(p_312401_, $$11, p_312784_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        this.model.windTop().skipDraw = false;
        this.model.windMiddle().skipDraw = true;
        this.model.windBottom().skipDraw = true;
        this.model.root().render(p_312401_, $$11, p_312784_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private float xOffset(float p_310525_) {
        return p_310525_ * 0.02F;
    }

    protected ResourceLocation getTextureLocation(Breeze p_312835_) {
        return this.textureLoc;
    }
}
