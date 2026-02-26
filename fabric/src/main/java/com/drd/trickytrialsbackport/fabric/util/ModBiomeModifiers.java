package com.drd.trickytrialsbackport.fabric.util;

import com.drd.trickytrialsbackport.registry.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biomes;

public class ModBiomeModifiers {
    public static void register() {
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.SWAMP),
                MobCategory.MONSTER,
                ModEntities.BOGGED.get(),
                30, 4, 4
        );

        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.MANGROVE_SWAMP),
                MobCategory.MONSTER,
                ModEntities.BOGGED.get(),
                30, 4, 4
        );
    }
}
