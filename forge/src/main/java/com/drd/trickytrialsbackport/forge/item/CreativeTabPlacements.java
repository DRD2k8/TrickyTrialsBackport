package com.drd.trickytrialsbackport.forge.item;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.registry.ModBlocks;
import com.drd.trickytrialsbackport.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TrickyTrialsBackport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTabPlacements {
    interface Entries {
        void addBefore(ItemLike reference, ItemLike... values);
        void addAfter(ItemLike reference, ItemLike... values);
    }

    /**
     * Example usage of adding an item after a modded item when a mod is installed:
     * <pre>{@code if (tab == ExampleModTabs.EXAMPLE_TAB) {
     *     ItemLike tileBlock = BuiltInRegistries.ITEM.get(
     *         new ResourceLocation("othermod", "tile_block")
     *     )
     *     // Makes the item register after the tileBlock if the other mod is installed
     *     entries.addAfter(tileBlock, ExampleModItems.TILE_STAIRS);
     *     // Makes the item register after Redstone if the other mod is not installed
     *     entries.addAfter(Items.REDSTONE, ExampleModItems.TILE_STAIRS);
     * }}</pre>
     */
    @SubscribeEvent
    public static void buildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
        var tab = event.getTabKey();
        var entries = new Entries() {
            @Override
            public void addBefore(ItemLike reference, ItemLike... values) {
                for (ItemLike value : values) {
                    event.getEntries().putBefore(new ItemStack(reference), new ItemStack(value), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }

            @Override
            public void addAfter(ItemLike reference, ItemLike... values) {
                for (ItemLike value : values) {
                    event.getEntries().putAfter(new ItemStack(reference), new ItemStack(value), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        };

        if (tab == CreativeModeTabs.COMBAT) {
            entries.addAfter(Items.TRIDENT, ModItems.MACE.get());

            ItemLike blueEgg = BuiltInRegistries.ITEM.get(
                    new ResourceLocation("minecraft", "blue_egg")
            );
            entries.addAfter(blueEgg, ModItems.WIND_CHARGE.get());
            entries.addAfter(Items.EGG, ModItems.WIND_CHARGE.get());
        }

        if (tab == CreativeModeTabs.INGREDIENTS) {
            entries.addAfter(Items.BLAZE_ROD, ModBlocks.HEAVY_CORE.get(), ModItems.BREEZE_ROD.get());
        }

        if (tab == CreativeModeTabs.SPAWN_EGGS) {
            entries.addAfter(Items.BLAZE_SPAWN_EGG, ModItems.BREEZE_SPAWN_EGG.get());
        }
    }
}
