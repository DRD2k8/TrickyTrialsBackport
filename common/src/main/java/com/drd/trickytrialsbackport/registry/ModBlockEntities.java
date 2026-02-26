package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.block.entity.CrafterBlockEntity;
import com.drd.trickytrialsbackport.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static Supplier<BlockEntityType<CrafterBlockEntity>> CRAFTER;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        CRAFTER = helper.registerAuto(Registries.BLOCK_ENTITY_TYPE, "crafter",
                () -> Services.BLOCK_ENTITY.createBlockEntityType(CrafterBlockEntity::new, ModBlocks.CRAFTER.get()));
    }
}
