package com.drd.trickytrialsbackport.client.renderer.blockentity;

import com.drd.trickytrialsbackport.block.VaultBlock;
import com.drd.trickytrialsbackport.block.entity.vault.VaultBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VaultRenderer implements BlockEntityRenderer<VaultBlockEntity> {
    public VaultRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(VaultBlockEntity vault, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int light, int overlay) {

        renderDisplayItem(vault, poseStack, buffer, light, overlay, partialTicks);
        renderEjectedItem(vault, poseStack, buffer, light, overlay, partialTicks);
    }

    private void renderDisplayItem(VaultBlockEntity vault, PoseStack poseStack,
                                   MultiBufferSource buffer, int light, int overlay, float partialTicks) {

        ItemStack stack = vault.getSharedData().getDisplayItem();
        if (stack == null || stack.isEmpty()) return;

        Level level = vault.getLevel();
        if (level == null) return;

        poseStack.pushPose();

        poseStack.translate(0.5, 0.4, 0.5);

        float rotation = (level.getGameTime() + partialTicks) * 4;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        poseStack.scale(1f, 1f, 1f);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                light,
                overlay,
                poseStack,
                buffer,
                level,
                0
        );

        poseStack.popPose();
    }

    private void renderEjectedItem(VaultBlockEntity vault, PoseStack poseStack,
                                   MultiBufferSource buffer, int light, int overlay, float partialTicks) {

        ItemStack ejecting = vault.getServerData().getCurrentEjectingItem();
        if (ejecting == null || ejecting.isEmpty()) return;

        float progress = vault.getServerData().ejectionProgress(partialTicks);

        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);

        Direction dir = vault.getBlockState().getValue(VaultBlock.FACING);
        poseStack.translate(dir.getStepX() * progress, dir.getStepY() * progress, dir.getStepZ() * progress);

        poseStack.translate(0, progress * 0.25f, 0);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                ejecting,
                ItemDisplayContext.GROUND,
                light,
                overlay,
                poseStack,
                buffer,
                vault.getLevel(),
                0
        );

        poseStack.popPose();
    }
}
