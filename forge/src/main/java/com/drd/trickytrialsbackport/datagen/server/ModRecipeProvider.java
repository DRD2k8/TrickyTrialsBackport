package com.drd.trickytrialsbackport.datagen.server;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.registry.ModBlocks;
import com.drd.trickytrialsbackport.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        stairs(Items.TUFF, ModBlocks.TUFF_STAIRS.get(), consumer);
        slab(Items.TUFF, ModBlocks.TUFF_SLAB.get(), consumer);
        wall(Items.TUFF, ModBlocks.TUFF_WALL.get(), consumer);
        chiseledBlock(ModBlocks.TUFF_SLAB.get(), ModBlocks.CHISELED_TUFF.get(), consumer);
        polishing(Items.TUFF, ModBlocks.POLISHED_TUFF.get(), consumer);
        stairs(Items.TUFF, ModBlocks.POLISHED_TUFF_STAIRS.get(), consumer);
        slab(Items.TUFF, ModBlocks.POLISHED_TUFF_SLAB.get(), consumer);
        wall(Items.TUFF, ModBlocks.POLISHED_TUFF_WALL.get(), consumer);
        polishing(ModBlocks.POLISHED_TUFF.get(), ModBlocks.TUFF_BRICKS.get(), consumer);
        stairs(ModBlocks.TUFF_BRICKS.get(), ModBlocks.TUFF_BRICK_STAIRS.get(), consumer);
        slab(ModBlocks.TUFF_BRICKS.get(), ModBlocks.TUFF_BRICK_SLAB.get(), consumer);
        wall(ModBlocks.TUFF_BRICKS.get(), ModBlocks.TUFF_BRICK_WALL.get(), consumer);
        chiseledBlock(ModBlocks.TUFF_BRICK_SLAB.get(), ModBlocks.CHISELED_TUFF_BRICKS.get(), consumer);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_STAIRS.get(), Items.TUFF);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_SLAB.get(), Items.TUFF, 2);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_WALL.get(), Items.TUFF);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_TUFF.get(), Items.TUFF);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_TUFF.get(), Items.TUFF);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_TUFF_STAIRS.get(), ModBlocks.POLISHED_TUFF.get());
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_TUFF_SLAB.get(), ModBlocks.POLISHED_TUFF.get(), 2);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_TUFF_WALL.get(), ModBlocks.POLISHED_TUFF.get());
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_TUFF_STAIRS.get(), Items.TUFF);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_TUFF_SLAB.get(), Items.TUFF, 2);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_TUFF_WALL.get(), Items.TUFF);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICKS.get(), ModBlocks.POLISHED_TUFF.get());
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICKS.get(), Items.TUFF);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICK_STAIRS.get(), ModBlocks.TUFF_BRICKS.get());
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICK_SLAB.get(), ModBlocks.TUFF_BRICKS.get(), 2);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICK_WALL.get(), ModBlocks.TUFF_BRICKS.get());
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICK_STAIRS.get(), ModBlocks.POLISHED_TUFF.get());
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICK_SLAB.get(), ModBlocks.POLISHED_TUFF.get(), 2);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICK_WALL.get(), ModBlocks.POLISHED_TUFF.get());
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICK_STAIRS.get(), Items.TUFF);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICK_SLAB.get(), Items.TUFF, 2);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICK_WALL.get(), Items.TUFF);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_TUFF_BRICKS.get(), ModBlocks.TUFF_BRICKS.get());
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_TUFF_BRICKS.get(), ModBlocks.POLISHED_TUFF.get());
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_TUFF_BRICKS.get(), Items.TUFF);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.MACE.get())
                .pattern("#")
                .pattern("I")
                .define('#', ModBlocks.HEAVY_CORE.get())
                .define('I', ModItems.BREEZE_ROD.get())
                .unlockedBy(getHasName(ModItems.BREEZE_ROD.get()), has(ModItems.BREEZE_ROD.get()))
                .unlockedBy(getHasName(ModBlocks.HEAVY_CORE.get()), has(ModBlocks.HEAVY_CORE.get()))
                .save(consumer);
        unpack(ModItems.BREEZE_ROD.get(), ModItems.WIND_CHARGE.get(), 4, consumer);
        trimDuplication(ModItems.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE.get(), Items.COPPER_BLOCK, consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE.get(), 2)
                .pattern("#S#")
                .pattern("#C#")
                .pattern("###")
                .define('#', Items.DIAMOND)
                .define('C', Items.WAXED_COPPER_BLOCK)
                .define('S', ModItems.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE.get())
                .unlockedBy(getHasName(ModItems.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE.get()), has(ModItems.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE.get()))
                .save(consumer, TrickyTrialsBackport.NAMESPACE + ":bolt_armor_trim_smithing_template_from_waxed_copper_block");
        trimDuplication(ModItems.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE.get(), ModItems.BREEZE_ROD.get(), consumer);
    }

    protected static void unpack(ItemLike packedItem, ItemLike unpackedItem, int count, Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, unpackedItem, count)
                .requires(packedItem)
                .unlockedBy(getHasName(packedItem), has(packedItem))
                .save(consumer);
    }

    protected static void stairs(ItemLike base, ItemLike stairs, Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 4)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .define('#', base)
                .unlockedBy(getHasName(base), has(base))
                .save(consumer);
    }

    protected static void slab(ItemLike base, ItemLike slab, Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, slab, 6)
                .pattern("###")
                .define('#', base)
                .unlockedBy(getHasName(base), has(base))
                .save(consumer);
    }

    protected static void wall(ItemLike base, ItemLike wall, Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, wall, 6)
                .pattern("###")
                .pattern("###")
                .define('#', base)
                .unlockedBy(getHasName(base), has(base))
                .save(consumer);
    }

    protected static void polishing(ItemLike unpolishedBlock, ItemLike polishedBlock, Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, polishedBlock, 4)
                .pattern("##")
                .pattern("##")
                .define('#', unpolishedBlock)
                .unlockedBy(getHasName(unpolishedBlock), has(unpolishedBlock))
                .save(consumer);
    }

    protected static void chiseledBlock(ItemLike slab, ItemLike chiseledBlock, Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, chiseledBlock)
                .pattern("#")
                .pattern("#")
                .define('#', slab)
                .unlockedBy(getHasName(slab), has(slab))
                .save(consumer);
    }

    protected static void trimDuplication(ItemLike armorTrim, ItemLike ingredient, Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, armorTrim, 2)
                .pattern("#S#")
                .pattern("#C#")
                .pattern("###")
                .define('#', Items.DIAMOND)
                .define('C', ingredient)
                .define('S', armorTrim)
                .unlockedBy(getHasName(armorTrim), has(armorTrim))
                .save(consumer);
    }
}
