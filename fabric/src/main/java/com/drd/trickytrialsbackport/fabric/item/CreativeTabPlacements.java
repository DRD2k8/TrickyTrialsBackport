package com.drd.trickytrialsbackport.fabric.item;

import com.drd.trickytrialsbackport.registry.ModBlocks;
import com.drd.trickytrialsbackport.registry.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
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

    private static void addItemsToCombatTab(FabricItemGroupEntries entries) {
        entries.addAfter(Items.TRIDENT, ModItems.MACE.get());

        ItemLike blueEgg = BuiltInRegistries.ITEM.get(
                new ResourceLocation("minecraft", "blue_egg")
        );
        entries.addAfter(blueEgg, ModItems.WIND_CHARGE.get());
        entries.addAfter(Items.EGG, ModItems.WIND_CHARGE.get());
    }

    private static void addItemsToIngredientsTab(FabricItemGroupEntries entries) {
        entries.addAfter(Items.BLAZE_ROD, ModItems.BREEZE_ROD.get(), ModBlocks.HEAVY_CORE.get());
        entries.addAfter(Items.PIGLIN_BANNER_PATTERN, ModItems.FLOW_BANNER_PATTERN.get(), ModItems.GUSTER_BANNER_PATTERN.get());
    }

    private static void addItemsToSpawnEggsTab(FabricItemGroupEntries entries) {
        entries.addAfter(Items.BLAZE_SPAWN_EGG, ModItems.BREEZE_SPAWN_EGG.get());
    }

    public static void registerTabPlacements() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(CreativeTabPlacements::addItemsToCombatTab);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(CreativeTabPlacements::addItemsToIngredientsTab);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(CreativeTabPlacements::addItemsToSpawnEggsTab);
    }
}
