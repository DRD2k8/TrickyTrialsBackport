package com.drd.trickytrialsbackport.client.renderer.layer;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.client.model.BoggedModel;
import com.drd.trickytrialsbackport.client.model.BoggedOuterModel;
import com.drd.trickytrialsbackport.client.registry.ModModelLayers;
import com.drd.trickytrialsbackport.entity.monster.Bogged;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BoggedOuterLayer<T extends Bogged> extends RenderLayer<T, BoggedModel<T>> {
    private final BoggedOuterModel<T> outerModel;
    private static final ResourceLocation OVERLAY =
            new ResourceLocation(TrickyTrialsBackport.NAMESPACE, "textures/entity/skeleton/bogged_overlay.png");

    public BoggedOuterLayer(RenderLayerParent<T, BoggedModel<T>> parent, EntityModelSet set) {
        super(parent);
        this.outerModel = new BoggedOuterModel<>(set.bakeLayer(ModModelLayers.BOGGED_OUTER_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int light, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        this.getParentModel().copyPropertiesTo(this.outerModel);

        this.outerModel.head.copyFrom(this.getParentModel().head);
        this.outerModel.hat.copyFrom(this.getParentModel().hat);

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(OVERLAY));
        this.outerModel.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }
}
