package com.drd.trickytrialsbackport.util;

import com.drd.trickytrialsbackport.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public class ModLootTables {
    public static final ResourceKey<LootTable> TRIAL_SPAWNER_CONSUMABLES =
            create("spawners/trial_chamber/consumables");

    public static final ResourceKey<LootTable> TRIAL_SPAWNER_KEY =
            create("spawners/trial_chamber/key");

    public static final ResourceKey<LootTable> TRIAL_SPAWNER_ITEMS_OMINOUS =
            create("spawners/trial_chamber/items_to_drop_when_ominous");

    private static ResourceKey<LootTable> create(String path) {
        return ResourceKey.create(
                TrialSpawnerConfig.LOOT_TABLE_REGISTRY,
                new ResourceLocation(path)
        );
    }
}
