package com.drd.trickytrialsbackport.datagen.server;

import com.drd.trickytrialsbackport.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        unpack(ModItems.BREEZE_ROD.get(), ModItems.WIND_CHARGE.get(), 4, consumer);
    }

    protected static void unpack(ItemLike packedItem, ItemLike unpackedItem, int count, Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, unpackedItem, count)
                .requires(packedItem)
                .unlockedBy(getHasName(packedItem), has(packedItem))
                .save(consumer);
    }
}
