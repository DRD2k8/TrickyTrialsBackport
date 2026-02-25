package com.drd.trickytrialsbackport.datagen.server;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> tagLookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, tagLookup, TrickyTrialsBackport.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(ItemTags.TRIM_TEMPLATES)
                .add(ModItems.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE.get())
                .add(ModItems.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE.get());

        this.tag(ItemTags.DECORATED_POT_SHERDS)
                .add(ModItems.FLOW_POTTERY_SHERD.get())
                .add(ModItems.GUSTER_POTTERY_SHERD.get())
                .add(ModItems.SCRAPE_POTTERY_SHERD.get());
    }
}
