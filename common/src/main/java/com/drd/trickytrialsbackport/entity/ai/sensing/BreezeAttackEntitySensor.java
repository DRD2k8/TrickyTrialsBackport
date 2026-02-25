package com.drd.trickytrialsbackport.entity.ai.sensing;

import com.drd.trickytrialsbackport.entity.monster.breeze.Breeze;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BreezeAttackEntitySensor extends NearestLivingEntitySensor<Breeze> {
    public static final int BREEZE_SENSOR_RADIUS = 24;

    public BreezeAttackEntitySensor() {
    }

    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.copyOf(Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    protected void doTick(ServerLevel p_310391_, Breeze p_312097_) {
        super.doTick(p_310391_, p_312097_);
        p_312097_.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).stream().flatMap(Collection::stream).filter((p_311534_) -> {
            return Sensor.isEntityAttackable(p_312097_, p_311534_);
        }).findFirst().ifPresentOrElse((p_310804_) -> {
            p_312097_.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, p_310804_);
        }, () -> {
            p_312097_.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        });
    }

    protected int radiusXZ() {
        return 24;
    }

    protected int radiusY() {
        return 24;
    }
}
