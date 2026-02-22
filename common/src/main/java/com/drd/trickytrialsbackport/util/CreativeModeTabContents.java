package com.drd.trickytrialsbackport.util;

import com.drd.trickytrialsbackport.platform.CommonPlatformUtils;
import com.drd.trickytrialsbackport.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

public class CreativeModeTabContents {
    public static void register() {
        CommonPlatformUtils.addAfter(CreativeModeTabs.INGREDIENTS, Items.BLAZE_ROD, ModItems.BREEZE_ROD.get());
        CommonPlatformUtils.addAfter(CreativeModeTabs.INGREDIENTS, Items.TRIDENT, ModItems.MACE.get());
    }
}
