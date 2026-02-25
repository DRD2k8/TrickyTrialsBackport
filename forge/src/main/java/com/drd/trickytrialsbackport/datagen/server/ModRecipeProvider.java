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
                .save(consumer, TrickyTrialsBackport.MOD_ID + ":bolt_armor_trim_smithing_template_from_waxed_copper_block");
        trimDuplication(ModItems.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE.get(), ModItems.BREEZE_ROD.get(), consumer);
    }

    protected static void unpack(ItemLike packedItem, ItemLike unpackedItem, int count, Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, unpackedItem, count)
                .requires(packedItem)
                .unlockedBy(getHasName(packedItem), has(packedItem))
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
