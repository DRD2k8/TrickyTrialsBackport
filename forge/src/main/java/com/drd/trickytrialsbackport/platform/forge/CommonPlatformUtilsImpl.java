package com.drd.trickytrialsbackport.platform.forge;

import com.drd.trickytrialsbackport.platform.CommonPlatformUtils;
import com.drd.trickytrialsbackport.util.AddAfterRequest;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonPlatformUtilsImpl {
    @SubscribeEvent
    public static void onBuildTabs(BuildCreativeModeTabContentsEvent event) {

        for (AddAfterRequest req : CommonPlatformUtils.getRequests()) {
            if (req.tab().equals(event.getTabKey())) {

                for (ItemLike value : req.values()) {
                    event.getEntries().putAfter(
                            new ItemStack(req.reference()),
                            new ItemStack(value),
                            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                    );
                }
            }
        }
    }

    public static void registerTabContents() {
    }

    public static void addAfter(ResourceKey<CreativeModeTab> tab, ItemLike reference, ItemLike... values) {
        CommonPlatformUtils.recordAddAfter(tab, reference, values);
    }
}
