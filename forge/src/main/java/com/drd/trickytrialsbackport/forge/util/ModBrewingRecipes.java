package com.drd.trickytrialsbackport.forge.util;

import com.drd.trickytrialsbackport.registry.ModPotions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class ModBrewingRecipes {
    public static void register() {
        BrewingRecipeRegistry.addRecipe(new InfestedBrewingRecipe());
    }

    public static class InfestedBrewingRecipe implements IBrewingRecipe {
        @Override
        public boolean isInput(ItemStack stack) {
            return stack.getItem() == Items.POTION &&
                    PotionUtils.getPotion(stack) == Potions.AWKWARD;
        }

        @Override
        public boolean isIngredient(ItemStack stack) {
            return stack.getItem() == Items.STONE;
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (isInput(input) && isIngredient(ingredient)) {
                return PotionUtils.setPotion(
                        new ItemStack(Items.POTION),
                        ModPotions.INFESTED.get()
                );
            }
            return ItemStack.EMPTY;
        }
    }
}
