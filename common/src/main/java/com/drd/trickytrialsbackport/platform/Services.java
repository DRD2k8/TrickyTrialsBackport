package com.drd.trickytrialsbackport.platform;

import com.drd.trickytrialsbackport.block.entity.ModBlockEntityHelper;
import com.drd.trickytrialsbackport.item.ModItemHelper;

import java.util.ServiceLoader;

public class Services {
    public static final ModBlockEntityHelper BLOCK_ENTITY = load(ModBlockEntityHelper.class);
    public static final ModItemHelper ITEM_HELPER = load(ModItemHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        return loadedService;
    }
}
