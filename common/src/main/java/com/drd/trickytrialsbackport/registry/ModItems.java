package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.item.MaceItem;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class ModItems {
    public static final RegistrySupplier<Item> BREEZE_ROD = basicItem("breeze_rod");
    public static final RegistrySupplier<Item> MACE = registerItem("mace", () -> new MaceItem(new Item.Properties().rarity(Rarity.EPIC).durability(500)));

    private static <T extends Item> RegistrySupplier<T> registerItem(String name, Supplier<T> item) {
        return ModRegistries.ITEMS.register(name, item);
    }

    private static RegistrySupplier<Item> basicItem(String name) {
        return registerItem(name, () -> new Item(new Item.Properties()));
    }

    public static void register() {
        ModRegistries.ITEMS.register();
    }
}
