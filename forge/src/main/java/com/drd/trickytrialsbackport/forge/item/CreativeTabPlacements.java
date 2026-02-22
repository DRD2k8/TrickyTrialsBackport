package com.drd.trickytrialsbackport.forge.item;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.registry.ModItems;
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
            entries.addAfter(Items.TRIDENT,
                    ModItems.MACE.get()
            );
        }

        if (tab == CreativeModeTabs.INGREDIENTS) {
            entries.addAfter(Items.BLAZE_ROD,
                    ModItems.BREEZE_ROD.get()
            );
        }
    }
}
