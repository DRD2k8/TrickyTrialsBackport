package com.drd.trickytrialsbackport.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.Set;
import java.util.function.ToIntFunction;

public class WeavingEffect extends MobEffect {
    private final ToIntFunction<RandomSource> maxCobwebs;

    public WeavingEffect(MobEffectCategory category, int color, ToIntFunction<RandomSource> maxCobwebs) {
        super(category, color);
        this.maxCobwebs = maxCobwebs;
    }

    public int getMaxCobwebs(RandomSource random) {
        return this.maxCobwebs.applyAsInt(random);
    }

    public static void spawnCobwebsRandomlyAround(Level level, RandomSource random, BlockPos center, int maxCobwebs) {
        Set<BlockPos> placed = new HashSet<>();

        for (BlockPos pos : BlockPos.randomInCube(random, 15, center, 1)) {
            if (placed.size() >= maxCobwebs) break;

            BlockPos below = pos.below();

            boolean replaceable = level.getBlockState(pos).canBeReplaced();
            boolean sturdyBelow = level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);

            if (replaceable && sturdyBelow) {
                placed.add(pos.immutable());
            }
        }

        for (BlockPos pos : placed) {
            level.setBlock(pos, Blocks.COBWEB.defaultBlockState(), 3);
            level.levelEvent(3018, pos, 0);
        }
    }
}
