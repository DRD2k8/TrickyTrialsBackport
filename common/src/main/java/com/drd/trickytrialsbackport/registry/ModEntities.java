package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.entity.monster.breeze.Breeze;
import com.drd.trickytrialsbackport.entity.projectile.windcharge.BreezeWindCharge;
import com.drd.trickytrialsbackport.entity.projectile.windcharge.WindCharge;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModEntities {
    public static final List<Supplier<EntityType<?>>> ENTITIES = new ArrayList<>();

    public static Supplier<EntityType<Breeze>> BREEZE;
    public static Supplier<EntityType<BreezeWindCharge>> BREEZE_WIND_CHARGE;
    public static Supplier<EntityType<WindCharge>> WIND_CHARGE;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        BREEZE = registerMob("breeze",
                () -> EntityType.Builder.of(Breeze::new, MobCategory.MONSTER).sized(0.6f, 1.7f).clientTrackingRange(10).build("breeze"));
        BREEZE_WIND_CHARGE = helper.registerAuto(
                Registries.ENTITY_TYPE,
                "breeze_wind_charge",
                () -> EntityType.Builder.<BreezeWindCharge>of(BreezeWindCharge::new, MobCategory.MISC).sized(0.3125f, 0.3125f).clientTrackingRange(4).updateInterval(10).build("breeze_wind_charge")
        );
        WIND_CHARGE = helper.registerAuto(
                Registries.ENTITY_TYPE,
                "wind_charge",
                () -> EntityType.Builder.<WindCharge>of(WindCharge::new, MobCategory.MISC).sized(0.3125f, 0.3125f).clientTrackingRange(4).updateInterval(10).build("wind_charge")
        );
    }

    public static <T extends Entity> Supplier<EntityType<T>> registerMob(String name, Supplier<EntityType<T>> entity) {
        Supplier<EntityType<T>> toReturn =
                RegistryHelper.getInstance().registerAuto(Registries.ENTITY_TYPE, name, entity);

        ENTITIES.add((Supplier<EntityType<?>>) (Supplier<?>) toReturn);
        return toReturn;
    }
}
