package com.drd.trickytrialsbackport.forge.client;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.client.model.WindChargeModel;
import com.drd.trickytrialsbackport.client.registry.ModModelLayers;
import com.drd.trickytrialsbackport.client.renderer.WindChargeRenderer;
import com.drd.trickytrialsbackport.registry.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TrickyTrialsBackport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TrickyTrialsBackportForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.WIND_CHARGE.get(), WindChargeRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.WIND_CHARGE, WindChargeModel::createBodyLayer);
    }
}
