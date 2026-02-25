package com.drd.trickytrialsbackport.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.function.Supplier;

public class ModBannerPatterns {
    public static Supplier<BannerPattern> FLOW;
    public static Supplier<BannerPattern> GUSTER;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        FLOW = helper.registerAuto(Registries.BANNER_PATTERN, "flow",
                () -> new BannerPattern("flow"));
        GUSTER = helper.registerAuto(Registries.BANNER_PATTERN, "guster",
                () -> new BannerPattern("guster"));
    }
}
