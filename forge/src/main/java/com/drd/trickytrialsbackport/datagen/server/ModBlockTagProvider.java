package com.drd.trickytrialsbackport.datagen.server;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TrickyTrialsBackport.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(BlockTags.STAIRS)
                .add(ModBlocks.TUFF_STAIRS.get())
                .add(ModBlocks.POLISHED_TUFF_STAIRS.get())
                .add(ModBlocks.TUFF_BRICK_STAIRS.get());

        this.tag(BlockTags.SLABS)
                .add(ModBlocks.TUFF_SLAB.get())
                .add(ModBlocks.POLISHED_TUFF_SLAB.get())
                .add(ModBlocks.TUFF_BRICK_SLAB.get());

        this.tag(BlockTags.WALLS)
                .add(ModBlocks.TUFF_WALL.get())
                .add(ModBlocks.POLISHED_TUFF_WALL.get())
                .add(ModBlocks.TUFF_BRICK_WALL.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.HEAVY_CORE.get())
                .add(ModBlocks.TUFF_STAIRS.get())
                .add(ModBlocks.TUFF_SLAB.get())
                .add(ModBlocks.TUFF_WALL.get())
                .add(ModBlocks.CHISELED_TUFF.get())
                .add(ModBlocks.POLISHED_TUFF.get())
                .add(ModBlocks.POLISHED_TUFF_STAIRS.get())
                .add(ModBlocks.POLISHED_TUFF_SLAB.get())
                .add(ModBlocks.POLISHED_TUFF_WALL.get())
                .add(ModBlocks.TUFF_BRICKS.get())
                .add(ModBlocks.TUFF_BRICK_STAIRS.get())
                .add(ModBlocks.TUFF_BRICK_SLAB.get())
                .add(ModBlocks.TUFF_BRICK_WALL.get())
                .add(ModBlocks.CHISELED_TUFF_BRICKS.get())
                .add(ModBlocks.CRAFTER.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.CRAFTER.get());
    }
}
