package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.block.HeavyCoreBlock;
import com.drd.trickytrialsbackport.util.ModSoundTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {
    public static final List<Supplier<Block>> BLOCKS = new ArrayList<>();

    public static Supplier<Block> HEAVY_CORE;

    public static <T extends Block> Supplier<T> registerBlockWithItem(String name, Supplier<T> block) {
        Supplier<T> toReturn = RegistryHelper.getInstance().registerAuto(Registries.BLOCK, name, block);
        BLOCKS.add((Supplier<Block>) toReturn);
        RegistryHelper.getInstance().registerAuto(Registries.ITEM, name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    public static <T extends Block> Supplier<T> registerBlockWithRarity(String name, Supplier<T> block, Rarity rarity) {
        Supplier<T> toReturn = RegistryHelper.getInstance().registerAuto(Registries.BLOCK, name, block);
        BLOCKS.add((Supplier<Block>) toReturn);
        RegistryHelper.getInstance().registerAuto(Registries.ITEM, name, () -> new BlockItem(toReturn.get(), new Item.Properties().rarity(rarity)));
        return toReturn;
    }

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        HEAVY_CORE = registerBlockWithRarity("heavy_core",
                () -> new HeavyCoreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.SNARE)
                        .sound(ModSoundTypes.HEAVY_CORE).explosionResistance(1200f).strength(10f).pushReaction(PushReaction.NORMAL)), Rarity.EPIC);
    }
}
