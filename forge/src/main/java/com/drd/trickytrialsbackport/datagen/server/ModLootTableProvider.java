package com.drd.trickytrialsbackport.datagen.server;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.registry.ModBlocks;
import com.drd.trickytrialsbackport.registry.ModEntities;
import com.drd.trickytrialsbackport.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModLootTableProvider {
    public static LootTableProvider create(PackOutput output) {
        return new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(Entities::new, LootContextParamSets.ENTITY)
        ));
    }

    public static class Blocks extends BlockLootSubProvider {
        protected Blocks() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            this.dropSelf(ModBlocks.CHISELED_TUFF.get());
            this.dropSelf(ModBlocks.CHISELED_TUFF_BRICKS.get());
            this.dropSelf(ModBlocks.HEAVY_CORE.get());
            this.dropSelf(ModBlocks.POLISHED_TUFF.get());
            this.add(ModBlocks.POLISHED_TUFF_SLAB.get(), block -> createSlabItemTable(ModBlocks.POLISHED_TUFF_SLAB.get()));
            this.dropSelf(ModBlocks.POLISHED_TUFF_STAIRS.get());
            this.dropSelf(ModBlocks.POLISHED_TUFF_WALL.get());
            this.dropSelf(ModBlocks.TUFF_BRICKS.get());
            this.add(ModBlocks.TUFF_BRICK_SLAB.get(), block -> createSlabItemTable(ModBlocks.TUFF_BRICK_SLAB.get()));
            this.dropSelf(ModBlocks.TUFF_BRICK_STAIRS.get());
            this.dropSelf(ModBlocks.TUFF_BRICK_WALL.get());
            this.add(ModBlocks.TUFF_SLAB.get(), block -> createSlabItemTable(ModBlocks.TUFF_SLAB.get()));
            this.dropSelf(ModBlocks.TUFF_STAIRS.get());
            this.dropSelf(ModBlocks.TUFF_WALL.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModBlocks.BLOCKS.stream()
                    .map(Supplier::get)
                    .toList();
        }
    }

    public static class Entities extends EntityLootSubProvider {
        protected Entities() {
            super(FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        public void generate() {
            LootPool.Builder arrows = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.ARROW)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                            .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                    );

            LootPool.Builder bones = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.BONE)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                            .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                    );

            LootPool.Builder tipped = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
                    .add(LootItem.lootTableItem(Items.TIPPED_ARROW)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                            .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)).setLimit(1))
                            .apply(SetPotionFunction.setPotion(Potions.POISON))
                    );

            this.add(ModEntities.BOGGED.get(),
                    LootTable.lootTable()
                            .withPool(arrows)
                            .withPool(bones)
                            .withPool(tipped)
                            .setRandomSequence(new ResourceLocation(TrickyTrialsBackport.NAMESPACE, "entities/bogged"))
            );

            this.add(ModEntities.BREEZE.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .add(LootItem.lootTableItem(ModItems.BREEZE_ROD.get())
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1.0F, 2.0F)))
                            )
                    )
            );
        }

        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes() {
            return ModEntities.ENTITIES.stream().map(Supplier::get);
        }
    }
}
