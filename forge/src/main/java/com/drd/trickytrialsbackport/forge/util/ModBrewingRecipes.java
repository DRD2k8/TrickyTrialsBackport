package com.drd.trickytrialsbackport.forge.util;

import com.drd.trickytrialsbackport.registry.ModItems;
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
        BrewingRecipeRegistry.addRecipe(new OozingBrewingRecipe());
        BrewingRecipeRegistry.addRecipe(new WeavingBrewingRecipe());
        BrewingRecipeRegistry.addRecipe(new WindChargedBrewingRecipe());
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

    public static class OozingBrewingRecipe implements IBrewingRecipe {
        @Override
        public boolean isInput(ItemStack stack) {
            return stack.getItem() == Items.POTION &&
                    PotionUtils.getPotion(stack) == Potions.AWKWARD;
        }

        @Override
        public boolean isIngredient(ItemStack stack) {
            return stack.getItem() == Items.SLIME_BLOCK;
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (isInput(input) && isIngredient(ingredient)) {
                return PotionUtils.setPotion(
                        new ItemStack(Items.POTION),
                        ModPotions.OOZING.get()
                );
            }
            return ItemStack.EMPTY;
        }
    }

    public static class WeavingBrewingRecipe implements IBrewingRecipe {
        @Override
        public boolean isInput(ItemStack stack) {
            return stack.getItem() == Items.POTION &&
                    PotionUtils.getPotion(stack) == Potions.AWKWARD;
        }

        @Override
        public boolean isIngredient(ItemStack stack) {
            return stack.getItem() == Items.COBWEB;
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (isInput(input) && isIngredient(ingredient)) {
                return PotionUtils.setPotion(
                        new ItemStack(Items.POTION),
                        ModPotions.WEAVING.get()
                );
            }
            return ItemStack.EMPTY;
        }
    }

    public static class WindChargedBrewingRecipe implements IBrewingRecipe {
        @Override
        public boolean isInput(ItemStack stack) {
            return stack.getItem() == Items.POTION &&
                    PotionUtils.getPotion(stack) == Potions.AWKWARD;
        }

        @Override
        public boolean isIngredient(ItemStack stack) {
            return stack.getItem() == ModItems.BREEZE_ROD.get();
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (isInput(input) && isIngredient(ingredient)) {
                return PotionUtils.setPotion(
                        new ItemStack(Items.POTION),
                        ModPotions.WIND_CHARGED.get()
                );
            }
            return ItemStack.EMPTY;
        }
    }
}
