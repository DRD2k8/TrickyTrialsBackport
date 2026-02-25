package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.block.HeavyCoreBlock;
import com.drd.trickytrialsbackport.mixin.StairBlockInvoker;
import com.drd.trickytrialsbackport.util.ModSoundTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {
    public static final List<Supplier<Block>> BLOCKS = new ArrayList<>();

    public static Supplier<Block> CHISELED_TUFF;
    public static Supplier<Block> CHISELED_TUFF_BRICKS;
    public static Supplier<Block> HEAVY_CORE;
    public static Supplier<Block> POLISHED_TUFF;
    public static Supplier<Block> POLISHED_TUFF_SLAB;
    public static Supplier<Block> POLISHED_TUFF_STAIRS;
    public static Supplier<Block> POLISHED_TUFF_WALL;
    public static Supplier<Block> TUFF_BRICKS;
    public static Supplier<Block> TUFF_BRICK_SLAB;
    public static Supplier<Block> TUFF_BRICK_STAIRS;
    public static Supplier<Block> TUFF_BRICK_WALL;
    public static Supplier<Block> TUFF_SLAB;
    public static Supplier<Block> TUFF_STAIRS;
    public static Supplier<Block> TUFF_WALL;

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

    public static Supplier<Block> registerStairs(String material, Supplier<Block> copiedBlock) {
        return registerBlockWithItem(material + "_stairs", () -> StairBlockInvoker.create(copiedBlock.get().defaultBlockState(), BlockBehaviour.Properties.copy(copiedBlock.get())));
    }

    public static Supplier<Block> registerSlab(String material, Supplier<Block> copiedBlock) {
        return registerBlockWithItem(material + "_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(copiedBlock.get())));
    }

    public static Supplier<Block> registerWall(String material, Supplier<Block> copiedBlock) {
        return registerBlockWithItem(material + "_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(copiedBlock.get())));
    }

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        CHISELED_TUFF = registerBlockWithItem("chiseled_tuff",
                () -> new Block(BlockBehaviour.Properties.copy(Blocks.TUFF)));
        CHISELED_TUFF_BRICKS = registerBlockWithItem("chiseled_tuff_bricks",
                () -> new Block(BlockBehaviour.Properties.copy(Blocks.TUFF).sound(ModSoundTypes.TUFF_BRICKS)));
        HEAVY_CORE = registerBlockWithRarity("heavy_core",
                () -> new HeavyCoreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.SNARE)
                        .sound(ModSoundTypes.HEAVY_CORE).explosionResistance(1200f).strength(10f).pushReaction(PushReaction.NORMAL)), Rarity.EPIC);
        POLISHED_TUFF = registerBlockWithItem("polished_tuff",
                () -> new Block(BlockBehaviour.Properties.copy(Blocks.TUFF).sound(ModSoundTypes.POLISHED_TUFF)));
        POLISHED_TUFF_SLAB = registerSlab("polished_tuff", POLISHED_TUFF);
        POLISHED_TUFF_STAIRS = registerStairs("polished_tuff", POLISHED_TUFF);
        POLISHED_TUFF_WALL = registerWall("polished_tuff", POLISHED_TUFF);
        TUFF_BRICKS = registerBlockWithItem("tuff_bricks",
                () -> new Block(BlockBehaviour.Properties.copy(Blocks.TUFF).sound(ModSoundTypes.TUFF_BRICKS)));
        TUFF_BRICK_SLAB = registerSlab("tuff_brick", TUFF_BRICKS);
        TUFF_BRICK_STAIRS = registerStairs("tuff_brick", TUFF_BRICKS);
        TUFF_BRICK_WALL = registerWall("tuff_brick", TUFF_BRICKS);
        TUFF_SLAB = registerBlockWithItem("tuff_slab",
                () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.TUFF)));
        TUFF_STAIRS = registerBlockWithItem("tuff_stairs",
                () -> StairBlockInvoker.create(Blocks.TUFF.defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.TUFF)));
        TUFF_WALL = registerBlockWithItem("tuff_wall",
                () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.TUFF)));
    }
}
