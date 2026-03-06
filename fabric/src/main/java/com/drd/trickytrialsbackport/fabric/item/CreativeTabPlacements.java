package com.drd.trickytrialsbackport.fabric.item;

import com.drd.trickytrialsbackport.registry.ModBlocks;
import com.drd.trickytrialsbackport.registry.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class CreativeTabPlacements {
    /**
     * Example usage of adding an item after a modded item when a mod is installed:
     * <pre>{@code private static void addItemsToExampleTab(FabricItemGroupEntries entries) {
     *     ItemLike tileBlock = BuiltInRegistries.ITEM.get(
     *         new ResourceLocation("othermod", "tile_block")
     *     )
     *     // Makes the item register after the tileBlock if the other mod is installed
     *     entries.addAfter(tileBlock, ExampleModItems.TILE_STAIRS);
     *     // Makes the item register after Redstone if the other mod is not installed
     *     entries.addAfter(Items.REDSTONE, ExampleModItems.TILE_STAIRS);
     * }}</pre>
     */

    private static void addItemsToBuildingBlocksTab(FabricItemGroupEntries entries) {
        entries.addAfter(Items.REINFORCED_DEEPSLATE,
                Items.TUFF,
                ModBlocks.TUFF_STAIRS.get(),
                ModBlocks.TUFF_SLAB.get(),
                ModBlocks.TUFF_WALL.get(),
                ModBlocks.CHISELED_TUFF.get(),
                ModBlocks.POLISHED_TUFF.get(),
                ModBlocks.POLISHED_TUFF_STAIRS.get(),
                ModBlocks.POLISHED_TUFF_SLAB.get(),
                ModBlocks.POLISHED_TUFF_WALL.get(),
                ModBlocks.TUFF_BRICKS.get(),
                ModBlocks.TUFF_BRICK_STAIRS.get(),
                ModBlocks.TUFF_BRICK_SLAB.get(),
                ModBlocks.TUFF_BRICK_WALL.get(),
                ModBlocks.CHISELED_TUFF_BRICKS.get());
    }

    private static void addItemsToFunctionalBlocksTab(FabricItemGroupEntries entries) {
        entries.addAfter(Items.ENDER_EYE, ModBlocks.VAULT.get());
    }

    private static void addItemsToRedstoneTab(FabricItemGroupEntries entries) {
        entries.addAfter(Items.DROPPER, ModBlocks.CRAFTER.get());
    }

    private static void addItemsToToolsTab(FabricItemGroupEntries entries) {
        entries.addAfter(Items.MUSIC_DISC_11, ModItems.MUSIC_DISC_CREATOR_MUSIC_BOX.get());
        entries.addAfter(Items.MUSIC_DISC_WAIT, ModItems.MUSIC_DISC_CREATOR.get(), ModItems.MUSIC_DISC_PRECIPICE.get());
    }

    private static void addItemsToCombatTab(FabricItemGroupEntries entries) {
        entries.addAfter(Items.TRIDENT, ModItems.MACE.get());

        ItemLike blueEgg = BuiltInRegistries.ITEM.get(
                new ResourceLocation("minecraft", "blue_egg")
        );
        entries.addAfter(blueEgg, ModItems.WIND_CHARGE.get());
        entries.addAfter(Items.EGG, ModItems.WIND_CHARGE.get());
    }

    private static void addItemsToFoodTab(FabricItemGroupEntries entries) {
        ItemStack stack1 = new ItemStack(ModItems.OMINOUS_BOTTLE.get());
        ItemStack stack2 = new ItemStack(ModItems.OMINOUS_BOTTLE.get());
        ItemStack stack3 = new ItemStack(ModItems.OMINOUS_BOTTLE.get());
        ItemStack stack4 = new ItemStack(ModItems.OMINOUS_BOTTLE.get());
        ItemStack stack5 = new ItemStack(ModItems.OMINOUS_BOTTLE.get());
        stack1.getOrCreateTag().putInt("OminousAmplifier", 0);
        stack2.getOrCreateTag().putInt("OminousAmplifier", 1);
        stack3.getOrCreateTag().putInt("OminousAmplifier", 2);
        stack4.getOrCreateTag().putInt("OminousAmplifier", 3);
        stack5.getOrCreateTag().putInt("OminousAmplifier", 4);

        entries.addAfter(Items.HONEY_BOTTLE, stack1);
        entries.addAfter(stack1, stack2);
        entries.addAfter(stack2, stack3);
        entries.addAfter(stack3, stack4);
        entries.addAfter(stack4, stack5);
    }

    private static void addItemsToIngredientsTab(FabricItemGroupEntries entries) {
        entries.addAfter(Items.BLAZE_ROD, ModItems.BREEZE_ROD.get(), ModBlocks.HEAVY_CORE.get());
        entries.addAfter(Items.PIGLIN_BANNER_PATTERN, ModItems.FLOW_BANNER_PATTERN.get(), ModItems.GUSTER_BANNER_PATTERN.get());
        entries.addAfter(Items.EXPLORER_POTTERY_SHERD, ModItems.FLOW_POTTERY_SHERD.get());
        entries.addAfter(Items.FRIEND_POTTERY_SHERD, ModItems.GUSTER_POTTERY_SHERD.get());
        entries.addAfter(Items.PRIZE_POTTERY_SHERD, ModItems.SCRAPE_POTTERY_SHERD.get());
        entries.addAfter(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ModItems.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE.get(), ModItems.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE.get());
        entries.addAfter(Items.EXPERIENCE_BOTTLE, ModItems.TRIAL_KEY.get(), ModItems.OMINOUS_TRIAL_KEY.get());
    }

    private static void addItemsToSpawnEggsTab(FabricItemGroupEntries entries) {
        entries.addAfter(Items.BLAZE_SPAWN_EGG, ModItems.BREEZE_SPAWN_EGG.get(), ModItems.BOGGED_SPAWN_EGG.get());

        ItemLike creakingHeart = BuiltInRegistries.ITEM.get(
                new ResourceLocation("minecraft", "creaking_heart")
        );
        entries.addBefore(creakingHeart, ModItems.TRIAL_SPAWNER.get());
        entries.addAfter(Items.SPAWNER, ModItems.TRIAL_SPAWNER.get());
    }

    public static void registerTabPlacements() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(CreativeTabPlacements::addItemsToBuildingBlocksTab);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(CreativeTabPlacements::addItemsToFunctionalBlocksTab);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(CreativeTabPlacements::addItemsToRedstoneTab);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(CreativeTabPlacements::addItemsToToolsTab);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(CreativeTabPlacements::addItemsToCombatTab);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(CreativeTabPlacements::addItemsToFoodTab);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(CreativeTabPlacements::addItemsToIngredientsTab);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(CreativeTabPlacements::addItemsToSpawnEggsTab);
    }
}
