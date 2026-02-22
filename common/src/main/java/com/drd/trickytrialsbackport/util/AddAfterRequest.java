package com.drd.trickytrialsbackport.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public record AddAfterRequest(ResourceKey<CreativeModeTab> tab, ItemLike reference, List<ItemLike> values) {
}
