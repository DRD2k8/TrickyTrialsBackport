package com.drd.trickytrialsbackport.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.block.entity.BannerPattern;

public class ModTags {
    public static class BannerPatterns {
        public static final TagKey<BannerPattern> FLOW = tag("pattern_item/flow");
        public static final TagKey<BannerPattern> GUSTER = tag("pattern_item/guster");

        private static TagKey<BannerPattern> tag(String name) {
            return TagKey.create(Registries.BANNER_PATTERN, new ResourceLocation(name));
        }
    }

    public static class DamageTypes {
        public static final TagKey<DamageType> BREEZE_IMMUNE_TO = tag("breeze_immune_to");

        private static TagKey<DamageType> tag(String name) {
            return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(name));
        }
    }
}
