package com.drd.trickytrialsbackport.fabric.item;

import com.drd.trickytrialsbackport.registry.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

public class CreativeTabPlacements {
    private static void addItemsToCombatTab(FabricItemGroupEntries entries) {
        entries.addAfter(Items.TRIDENT,
                ModItems.MACE.get());
    }

    private static void addItemsToIngredientsTab(FabricItemGroupEntries entries) {
        entries.addAfter(Items.BLAZE_ROD,
                ModItems.BREEZE_ROD.get());
    }

    public static void registerTabPlacements() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(CreativeTabPlacements::addItemsToCombatTab);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(CreativeTabPlacements::addItemsToIngredientsTab);
    }
}
