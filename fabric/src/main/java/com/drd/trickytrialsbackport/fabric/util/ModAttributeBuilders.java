package com.drd.trickytrialsbackport.fabric.util;

import com.drd.trickytrialsbackport.entity.monster.Bogged;
import com.drd.trickytrialsbackport.entity.monster.breeze.Breeze;
import com.drd.trickytrialsbackport.registry.ModEntities;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class ModAttributeBuilders {
    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(
                ModEntities.BOGGED.get(),
                Bogged.createAttributes());
        FabricDefaultAttributeRegistry.register(
                ModEntities.BREEZE.get(),
                Breeze.createAttributes());
    }
}
