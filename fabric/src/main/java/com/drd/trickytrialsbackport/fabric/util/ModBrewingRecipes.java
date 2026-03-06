package com.drd.trickytrialsbackport.fabric.util;

import com.drd.trickytrialsbackport.registry.ModItems;
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
        FabricBrewingRecipeRegistry.registerPotionRecipe(
                Potions.AWKWARD,
                Ingredient.of(Items.SLIME_BLOCK),
                ModPotions.OOZING.get()
        );
        FabricBrewingRecipeRegistry.registerPotionRecipe(
                Potions.AWKWARD,
                Ingredient.of(Items.COBWEB),
                ModPotions.WEAVING.get()
        );
        FabricBrewingRecipeRegistry.registerPotionRecipe(
                Potions.AWKWARD,
                Ingredient.of(ModItems.BREEZE_ROD.get()),
                ModPotions.WIND_CHARGED.get()
        );
    }
}
