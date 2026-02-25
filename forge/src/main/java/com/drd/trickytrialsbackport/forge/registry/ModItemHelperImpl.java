package com.drd.trickytrialsbackport.forge.registry;

import com.drd.trickytrialsbackport.item.ModItemHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

public class ModItemHelperImpl implements ModItemHelper {
    @Override
    public <T extends Mob> Item createSpawnEgg(Supplier<EntityType<T>> entity, int primaryColor, int secondaryColor) {
        return new ForgeSpawnEggItem(
                entity,
                primaryColor,
                secondaryColor,
                new Item.Properties()
        );
    }
}
