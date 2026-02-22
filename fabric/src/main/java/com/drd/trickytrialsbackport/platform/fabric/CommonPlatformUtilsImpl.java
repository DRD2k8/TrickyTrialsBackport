package com.drd.trickytrialsbackport.platform.fabric;

import com.drd.trickytrialsbackport.platform.CommonPlatformUtils;
import com.drd.trickytrialsbackport.util.AddAfterRequest;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

public class CommonPlatformUtilsImpl {
    public static void registerTabContents() {
        for (AddAfterRequest req : CommonPlatformUtils.getRequests()) {
            ItemGroupEvents.modifyEntriesEvent(req.tab()).register(entries -> {
                entries.addAfter(req.reference(), req.values().toArray(ItemLike[]::new));
            });
        }
    }

    public static void addAfter(ResourceKey<CreativeModeTab> tab, ItemLike reference, ItemLike... values) {
        CommonPlatformUtils.recordAddAfter(tab, reference, values);
    }
}
