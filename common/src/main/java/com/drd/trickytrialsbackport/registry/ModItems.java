package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.item.MaceItem;
import com.drd.trickytrialsbackport.item.ModItemHelper;
import com.drd.trickytrialsbackport.item.WindChargeItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class ModItems {
    public static Supplier<Item> BREEZE_ROD;
    public static Supplier<Item> BREEZE_SPAWN_EGG;
    public static Supplier<Item> MACE;
    public static Supplier<Item> WIND_CHARGE;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        BREEZE_ROD = helper.registerAuto(Registries.ITEM, "breeze_rod", () -> new Item(new Item.Properties()));
        BREEZE_SPAWN_EGG = helper.registerAuto(Registries.ITEM, "breeze_spawn_egg", () -> ModItemHelper.createSpawnEggItem(ModEntities.BREEZE, 11506911, 9529055));
        MACE = helper.registerAuto(Registries.ITEM, "mace", () -> new MaceItem(new Item.Properties().rarity(Rarity.EPIC).durability(500)));
        WIND_CHARGE = helper.registerAuto(Registries.ITEM, "wind_charge", () -> new WindChargeItem(new Item.Properties()));
    }
}
