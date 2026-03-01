package com.drd.trickytrialsbackport.fabric.client;

import com.drd.trickytrialsbackport.client.model.*;
import com.drd.trickytrialsbackport.client.particle.FlyStraightTowardsParticle;
import com.drd.trickytrialsbackport.client.particle.GustParticle;
import com.drd.trickytrialsbackport.client.particle.GustSeedParticle;
import com.drd.trickytrialsbackport.client.particle.TrialSpawnerDetectionParticle;
import com.drd.trickytrialsbackport.client.registry.ModModelLayers;
import com.drd.trickytrialsbackport.client.renderer.blockentity.TrialSpawnerRenderer;
import com.drd.trickytrialsbackport.client.renderer.entity.BoggedRenderer;
import com.drd.trickytrialsbackport.client.renderer.entity.BreezeRenderer;
import com.drd.trickytrialsbackport.client.renderer.entity.BreezeWindChargeRenderer;
import com.drd.trickytrialsbackport.client.renderer.entity.WindChargeRenderer;
import com.drd.trickytrialsbackport.client.screen.CrafterScreen;
import com.drd.trickytrialsbackport.compat.vanillabackport.client.VanillaBackportClientEvents;
import com.drd.trickytrialsbackport.registry.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.renderer.RenderType;

public final class TrickyTrialsBackportFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.TRIAL_SPAWNER.get(), TrialSpawnerRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TRIAL_SPAWNER.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.VAULT.get(), RenderType.cutout());
        EntityRendererRegistry.register(ModEntities.BOGGED.get(), BoggedRenderer::new);
        EntityRendererRegistry.register(ModEntities.BREEZE.get(), BreezeRenderer::new);
        EntityRendererRegistry.register(ModEntities.BREEZE_WIND_CHARGE.get(), BreezeWindChargeRenderer::new);
        EntityRendererRegistry.register(ModEntities.WIND_CHARGE.get(), WindChargeRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.BOGGED, BoggedModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.BOGGED_OUTER_LAYER, BoggedOuterModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.BREEZE, BreezeModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.BREEZE_EYES, BreezeModel::createEyesLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.BREEZE_WIND, BreezeModel::createWindBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.BREEZE_WIND_CHARGE, BreezeWindChargeModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WIND_CHARGE, WindChargeModel::createBodyLayer);
        MenuScreens.register(ModMenuTypes.CRAFTER_3x3.get(), CrafterScreen::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.GUST.get(), GustParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.GUST_EMITTER_LARGE.get(), new GustSeedParticle.Provider(3.0, 7, 0));
        ParticleFactoryRegistry.getInstance().register(ModParticles.GUST_EMITTER_SMALL.get(), new GustSeedParticle.Provider(1.0, 3, 2));
        ParticleFactoryRegistry.getInstance().register(ModParticles.OMINOUS_SPAWNING.get(), FlyStraightTowardsParticle.OminousSpawnProvider::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SMALL_GUST.get(), GustParticle.SmallProvider::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.TRIAL_OMEN.get(), SpellParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.TRIAL_SPAWNER_DETECTED_PLAYER.get(), TrialSpawnerDetectionParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS.get(), TrialSpawnerDetectionParticle.Provider::new);

        if (FabricLoader.getInstance().isModLoaded("vanillabackport")) {
            VanillaBackportClientEvents.specialModels();
        }
    }
}
