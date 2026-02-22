package com.drd.trickytrialsbackport.client.renderer;

import com.drd.trickytrialsbackport.client.model.WindChargeModel;
import com.drd.trickytrialsbackport.client.registry.ModModelLayers;
import com.drd.trickytrialsbackport.entity.WindCharge;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class WindChargeRenderer extends EntityRenderer<WindCharge> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/entity/projectiles/wind_charge.png");

    private final WindChargeModel model;

    public WindChargeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new WindChargeModel(ctx.bakeLayer(ModModelLayers.WIND_CHARGE));
    }

    @Override
    public void render(WindCharge entity, float yaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        poseStack.scale(0.5F, 0.5F, 0.5F);

        model.setupAnim(entity, 0, 0, entity.tickCount + partialTicks, 0, 0);

        float xOffset = (entity.tickCount + partialTicks) * 0.03F;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));

        model.renderToBuffer(
                poseStack,
                vc,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WindCharge entity) {
        return TEXTURE;
    }
}
