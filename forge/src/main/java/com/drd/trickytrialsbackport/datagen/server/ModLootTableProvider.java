package com.drd.trickytrialsbackport.datagen.server;

import com.drd.trickytrialsbackport.registry.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ModLootTableProvider {
    public static LootTableProvider create(PackOutput output) {
        return new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK)
        ));
    }

    public static class Blocks extends BlockLootSubProvider {
        protected Blocks() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            this.dropSelf(ModBlocks.HEAVY_CORE.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModBlocks.BLOCKS.stream()
                    .map(Supplier::get)
                    .toList();
        }
    }
}
