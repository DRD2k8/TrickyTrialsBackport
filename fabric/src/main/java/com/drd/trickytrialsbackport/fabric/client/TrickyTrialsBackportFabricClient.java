package com.drd.trickytrialsbackport.fabric.client;

import com.drd.trickytrialsbackport.client.model.WindChargeModel;
import com.drd.trickytrialsbackport.client.registry.ModModelLayers;
import com.drd.trickytrialsbackport.client.renderer.WindChargeRenderer;
import com.drd.trickytrialsbackport.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public final class TrickyTrialsBackportFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.WIND_CHARGE.get(), WindChargeRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WIND_CHARGE, WindChargeModel::createBodyLayer);
    }
}
