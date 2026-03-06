package com.drd.trickytrialsbackport.effect.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;

public class WindBurstDamageCalculator extends ExplosionDamageCalculator {
    @Override
    public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float power) {
        return false;
    }

    public float getKnockbackMultiplier(Entity entity) {
        return 1.5F;
    }

    public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
        return false;
    }
}
