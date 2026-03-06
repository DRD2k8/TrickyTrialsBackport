package com.drd.trickytrialsbackport.effect;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;

import java.util.function.ToIntFunction;

public class OozingEffect extends MobEffect {
    private final ToIntFunction<RandomSource> spawnedCount;

    public OozingEffect(MobEffectCategory category, int color, ToIntFunction<RandomSource> spawnedCount) {
        super(category, color);
        this.spawnedCount = spawnedCount;
    }

    public int getSpawnedCount(RandomSource random) {
        return this.spawnedCount.applyAsInt(random);
    }

    public static void spawnSlime(Level level, double x, double y, double z) {
        Slime slime = EntityType.SLIME.create(level);
        if (slime != null) {
            slime.setSize(2, true);
            slime.moveTo(x, y, z, level.getRandom().nextFloat() * 360.0F, 0.0F);
            level.addFreshEntity(slime);
        }
    }
}
