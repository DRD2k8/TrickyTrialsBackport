package com.drd.trickytrialsbackport.fabric.util;

import com.drd.trickytrialsbackport.registry.ModPotions;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

public class ModBrewingRecipes {
    public static void register() {
        FabricBrewingRecipeRegistry.registerPotionRecipe(
                Potions.AWKWARD,
                Ingredient.of(Items.STONE),
                ModPotions.INFESTED.get()
        );
    }
}
