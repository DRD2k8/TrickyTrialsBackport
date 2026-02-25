package com.drd.trickytrialsbackport.compat.vanillabackport.client.renderer;

import com.blackgear.platform.client.v2.render.DynamicItemRenderer;
import com.blackgear.platform.core.util.event.ResultHolder;
import com.blackgear.vanillabackport.core.VanillaBackport;
import com.drd.trickytrialsbackport.registry.ModItems;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModSpawnEggRenderer implements DynamicItemRenderer.Renderer {
    public static final Set<ItemLike> SPAWN_EGGS = Set.of(
            ModItems.BREEZE_SPAWN_EGG.get()
    );

    private static final Map<ItemLike, ModelResourceLocation> EGG_MODELS = buildModels();

    private static Map<ItemLike, ModelResourceLocation> buildModels() {
        Map<ItemLike, ModelResourceLocation> models = new HashMap<>();
        for (ItemLike item : SPAWN_EGGS) models.put(item, create(item.asItem()));
        return models;
    }

    private static ModelResourceLocation create(Item item) {
        return new ModelResourceLocation(VanillaBackport.resource(BuiltInRegistries.ITEM.getKey(item).getPath()), "inventory");
    }

    @Override
    public boolean shouldUse() {
        return !VanillaBackport.CLIENT_CONFIG.useLegacySpawnEggs.get();
    }

    @Override
    public void renderFirstPerson(
            ItemStack stack,
            ItemDisplayContext context,
            boolean leftHand,
            PoseStack pose,
            MultiBufferSource buffer,
            int light,
            int overlay,
            BakedModel model,
            ItemModelShaper shaper,
            ItemColors colors
    ) {
        model = shaper.getModelManager().getModel(EGG_MODELS.get(stack.getItem()));
        model.getTransforms().getTransform(context).apply(leftHand, pose);
        pose.translate(-0.5F, -0.5F, -0.5F);
        RenderType renderType = ItemBlockRenderTypes.getRenderType(stack, true);
        VertexConsumer vertices = ItemRenderer.getFoilBufferDirect(buffer, renderType, true, stack.hasFoil());
        this.renderModelLists(model, stack, light, overlay, pose, vertices, colors);
    }

    @Override
    public ResultHolder<BakedModel> renderThirdPerson(ItemStack stack, ItemModelShaper shaper) {
        return ResultHolder.submit(shaper.getModelManager().getModel(EGG_MODELS.get(stack.getItem())));
    }

    @Override
    public Set<ModelResourceLocation> registerModels() {
        Set<ModelResourceLocation> models = ImmutableSet.of();
        models = ImmutableSet.<ModelResourceLocation>builder()
                .addAll(models)
                .addAll(EGG_MODELS.values())
                .build();
        return models;
    }

    @Override
    public void renderQuadList(PoseStack pose, VertexConsumer buffer, List<BakedQuad> quads, ItemStack stack, int light, int overlay, ItemColors colors) {
        PoseStack.Pose last = pose.last();
        for (BakedQuad quad : quads) {
            int tint = -1;
            float red = (float) (tint >> 16 & 0xFF) / 255.0F;
            float green = (float) (tint >> 8 & 0xFF) / 255.0F;
            float blue = (float) (tint & 0xFF) / 255.0F;
            buffer.putBulkData(last, quad, red, green, blue, light, overlay);
        }
    }
}
