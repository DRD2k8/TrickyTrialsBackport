package com.drd.trickytrialsbackport.forge.client;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.client.model.*;
import com.drd.trickytrialsbackport.client.particle.GustParticle;
import com.drd.trickytrialsbackport.client.particle.GustSeedParticle;
import com.drd.trickytrialsbackport.client.registry.ModModelLayers;
import com.drd.trickytrialsbackport.client.renderer.BoggedRenderer;
import com.drd.trickytrialsbackport.client.renderer.BreezeRenderer;
import com.drd.trickytrialsbackport.client.renderer.BreezeWindChargeRenderer;
import com.drd.trickytrialsbackport.client.renderer.WindChargeRenderer;
import com.drd.trickytrialsbackport.client.screen.CrafterScreen;
import com.drd.trickytrialsbackport.compat.vanillabackport.client.VanillaBackportClientEvents;
import com.drd.trickytrialsbackport.registry.ModEntities;
import com.drd.trickytrialsbackport.registry.ModMenuTypes;
import com.drd.trickytrialsbackport.registry.ModParticles;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TrickyTrialsBackport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TrickyTrialsBackportForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.BOGGED.get(), BoggedRenderer::new);
        EntityRenderers.register(ModEntities.BREEZE.get(), BreezeRenderer::new);
        EntityRenderers.register(ModEntities.BREEZE_WIND_CHARGE.get(), BreezeWindChargeRenderer::new);
        EntityRenderers.register(ModEntities.WIND_CHARGE.get(), WindChargeRenderer::new);
        MenuScreens.register(ModMenuTypes.CRAFTER_3x3.get(), CrafterScreen::new);
        if (ModList.get().isLoaded("vanillabackport")) {
            VanillaBackportClientEvents.specialModels();
        }
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.BOGGED, BoggedModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.BOGGED_OUTER_LAYER, BoggedOuterModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.BREEZE, BreezeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.BREEZE_EYES, BreezeModel::createEyesLayer);
        event.registerLayerDefinition(ModModelLayers.BREEZE_WIND, BreezeModel::createWindBodyLayer);
        event.registerLayerDefinition(ModModelLayers.BREEZE_WIND_CHARGE, BreezeWindChargeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.WIND_CHARGE, WindChargeModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.GUST.get(), GustParticle.Provider::new);
        event.registerSpecial(ModParticles.GUST_EMITTER_LARGE.get(), new GustSeedParticle.Provider(3.0, 7, 0));
        event.registerSpecial(ModParticles.GUST_EMITTER_SMALL.get(), new GustSeedParticle.Provider(1.0, 3, 2));
        event.registerSpriteSet(ModParticles.SMALL_GUST.get(), GustParticle.SmallProvider::new);
    }
}
