package com.drd.trickytrialsbackport.item;

import com.drd.trickytrialsbackport.platform.Services;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public interface ModItemHelper {
    <T extends Mob> Item createSpawnEgg(Supplier<EntityType<T>> entity, int primaryColor, int secondaryColor);

    static <T extends Mob> Item createSpawnEggItem(Supplier<EntityType<T>> entity, int primaryColor, int secondaryColor) {
        return Services.ITEM_HELPER.createSpawnEgg(entity, primaryColor, secondaryColor);
    }
}
