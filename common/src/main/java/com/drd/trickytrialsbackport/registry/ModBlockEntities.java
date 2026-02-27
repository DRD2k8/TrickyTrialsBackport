package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.block.entity.CrafterBlockEntity;
import com.drd.trickytrialsbackport.block.entity.trialspawner.TrialSpawnerBlockEntity;
import com.drd.trickytrialsbackport.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static Supplier<BlockEntityType<CrafterBlockEntity>> CRAFTER;
    public static Supplier<BlockEntityType<TrialSpawnerBlockEntity>> TRIAL_SPAWNER;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        CRAFTER = helper.registerAuto(Registries.BLOCK_ENTITY_TYPE, "crafter",
                () -> Services.BLOCK_ENTITY.createBlockEntityType(CrafterBlockEntity::new, ModBlocks.CRAFTER.get()));
        TRIAL_SPAWNER = helper.registerAuto(Registries.BLOCK_ENTITY_TYPE, "trial_spawner",
                () -> Services.BLOCK_ENTITY.createBlockEntityType(TrialSpawnerBlockEntity::new, ModBlocks.TRIAL_SPAWNER.get()));
    }
}
