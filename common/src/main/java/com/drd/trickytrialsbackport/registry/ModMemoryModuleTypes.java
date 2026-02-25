package com.drd.trickytrialsbackport.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Optional;
import java.util.function.Supplier;

public class ModMemoryModuleTypes {
    public static Supplier<MemoryModuleType<Unit>> BREEZE_JUMP_COOLDOWN;
    public static Supplier<MemoryModuleType<Unit>> BREEZE_SHOOT;
    public static Supplier<MemoryModuleType<Unit>> BREEZE_SHOOT_CHARGING;
    public static Supplier<MemoryModuleType<Unit>> BREEZE_SHOOT_RECOVERING;
    public static Supplier<MemoryModuleType<Unit>> BREEZE_SHOOT_COOLDOWN;
    public static Supplier<MemoryModuleType<Unit>> BREEZE_JUMP_INHALING;
    public static Supplier<MemoryModuleType<BlockPos>> BREEZE_JUMP_TARGET;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        BREEZE_JUMP_COOLDOWN = helper.registerAuto(Registries.MEMORY_MODULE_TYPE, "breeze_jump_cooldown", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
        BREEZE_SHOOT = helper.registerAuto(Registries.MEMORY_MODULE_TYPE, "breeze_shoot", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
        BREEZE_SHOOT_CHARGING = helper.registerAuto(Registries.MEMORY_MODULE_TYPE, "breeze_shoot_charging", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
        BREEZE_SHOOT_RECOVERING = helper.registerAuto(Registries.MEMORY_MODULE_TYPE, "breeze_shoot_recovering", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
        BREEZE_SHOOT_COOLDOWN = helper.registerAuto(Registries.MEMORY_MODULE_TYPE, "breeze_shoot_cooldown", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
        BREEZE_JUMP_INHALING = helper.registerAuto(Registries.MEMORY_MODULE_TYPE, "breeze_jump_inhaling", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
        BREEZE_JUMP_TARGET = helper.registerAuto(Registries.MEMORY_MODULE_TYPE, "breeze_jump_target", () -> new MemoryModuleType<>(Optional.of(BlockPos.CODEC)));
    }
}
