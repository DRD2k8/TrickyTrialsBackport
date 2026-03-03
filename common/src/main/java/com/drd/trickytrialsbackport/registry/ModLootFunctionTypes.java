package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.loot.SetOminousBottleAmplifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import java.util.function.Supplier;

public class ModLootFunctionTypes {
    public static Supplier<LootItemFunctionType> SET_OMINOUS_BOTTLE_AMPLIFIER;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        SET_OMINOUS_BOTTLE_AMPLIFIER = helper.registerAuto(Registries.LOOT_FUNCTION_TYPE, "set_ominous_bottle_amplifier", () -> new LootItemFunctionType(new SetOminousBottleAmplifier.Serializer()));
    }
}
