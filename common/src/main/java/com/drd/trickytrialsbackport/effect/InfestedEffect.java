package com.drd.trickytrialsbackport.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InfestedEffect extends MobEffect {
    public InfestedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
    }

    public static void spawnSilverfish(Level level, LivingEntity owner) {
        if (level.isClientSide()) return;

        BlockPos pos = owner.blockPosition().below();

        Silverfish silverfish = EntityType.SILVERFISH.spawn(
                (ServerLevel) level,
                (ItemStack) null,
                null,
                pos,
                MobSpawnType.TRIGGERED,
                false,
                false
        );

        if (silverfish != null) {
            silverfish.setPersistenceRequired();
        }
    }
}
