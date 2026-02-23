package com.drd.trickytrialsbackport.fabric.client;

import com.drd.trickytrialsbackport.client.model.WindChargeModel;
import com.drd.trickytrialsbackport.client.particle.GustParticle;
import com.drd.trickytrialsbackport.client.particle.GustSeedParticle;
import com.drd.trickytrialsbackport.client.registry.ModModelLayers;
import com.drd.trickytrialsbackport.client.renderer.WindChargeRenderer;
import com.drd.trickytrialsbackport.registry.ModEntities;
import com.drd.trickytrialsbackport.registry.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public final class TrickyTrialsBackportFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.WIND_CHARGE.get(), WindChargeRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WIND_CHARGE, WindChargeModel::createBodyLayer);
        ParticleFactoryRegistry.getInstance().register(ModParticles.GUST.get(), GustParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.GUST_EMITTER_LARGE.get(), new GustSeedParticle.Provider(3.0, 7, 0));
        ParticleFactoryRegistry.getInstance().register(ModParticles.GUST_EMITTER_SMALL.get(), new GustSeedParticle.Provider(1.0, 3, 2));
        ParticleFactoryRegistry.getInstance().register(ModParticles.SMALL_GUST.get(), GustParticle.SmallProvider::new);
    }
}
