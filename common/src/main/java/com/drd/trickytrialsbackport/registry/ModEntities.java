package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.entity.projectile.windcharge.WindCharge;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class ModEntities {
    public static Supplier<EntityType<WindCharge>> WIND_CHARGE;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        WIND_CHARGE = helper.registerAuto(
                Registries.ENTITY_TYPE,
                "wind_charge",
                () -> EntityType.Builder.<WindCharge>of(WindCharge::new, MobCategory.MISC).sized(0.3125f, 0.3125f).clientTrackingRange(4).updateInterval(10).build("wind_charge")
        );
    }
}
