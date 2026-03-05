package com.drd.trickytrialsbackport.client.renderer.blockentity;

import com.drd.trickytrialsbackport.block.entity.trialspawner.TrialSpawner;
import com.drd.trickytrialsbackport.block.entity.trialspawner.TrialSpawnerBlockEntity;
import com.drd.trickytrialsbackport.block.entity.trialspawner.TrialSpawnerData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class TrialSpawnerRenderer implements BlockEntityRenderer<TrialSpawnerBlockEntity> {
    private final EntityRenderDispatcher entityRenderer;

    public TrialSpawnerRenderer(BlockEntityRendererProvider.Context ctx) {
        this.entityRenderer = ctx.getEntityRenderer();
    }

    @Override
    public void render(
            TrialSpawnerBlockEntity be,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        Level level = be.getLevel();
        if (level == null) return;

        TrialSpawner spawner = be.getTrialSpawner();
        TrialSpawnerData data = spawner.getData();

        Entity entity = data.getOrCreateDisplayEntity(spawner, level, spawner.getState());
        if (entity == null) return;

        poseStack.pushPose();

        poseStack.translate(0.5D, 0.15D, 0.5D);

        float scale = 0.35F;
        poseStack.scale(scale, scale, scale);

        float spin = (Minecraft.getInstance().level.getGameTime() * 20) % 360;
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));

        this.entityRenderer.render(
                entity,
                0.0D,
                0.0D,
                0.0D,
                0.0F,
                partialTicks,
                poseStack,
                buffer,
                packedLight
        );

        poseStack.popPose();
    }
}
