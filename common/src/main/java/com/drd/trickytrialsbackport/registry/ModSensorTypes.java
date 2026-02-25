package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.entity.ai.sensing.BreezeAttackEntitySensor;
import com.drd.trickytrialsbackport.mixin.SensorTypeInvoker;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.function.Supplier;

public class ModSensorTypes {
    public static Supplier<SensorType<BreezeAttackEntitySensor>> BREEZE_ATTACK_ENTITY_SENSOR;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        BREEZE_ATTACK_ENTITY_SENSOR = helper.registerAuto(Registries.SENSOR_TYPE, "breeze_attack_entity_sensor", () -> SensorTypeInvoker.invokeNew(BreezeAttackEntitySensor::new));
    }
}
