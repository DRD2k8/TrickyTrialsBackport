package com.drd.trickytrialsbackport.forge;

import com.drd.trickytrialsbackport.config.CommonConfig;
import com.drd.trickytrialsbackport.effect.InfestedEffect;
import com.drd.trickytrialsbackport.entity.monster.Bogged;
import com.drd.trickytrialsbackport.entity.monster.breeze.Breeze;
import com.drd.trickytrialsbackport.forge.registry.ForgeRegistryHelper;
import com.drd.trickytrialsbackport.forge.util.ModBrewingRecipes;
import com.drd.trickytrialsbackport.registry.ModEffects;
import com.drd.trickytrialsbackport.registry.ModEntities;
import com.drd.trickytrialsbackport.registry.RegistryHelper;
import com.drd.trickytrialsbackport.util.ModSoundTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(TrickyTrialsBackport.MOD_ID)
public final class TrickyTrialsBackportForge {
    public TrickyTrialsBackportForge() {
        RegistryHelper.setInstance(new ForgeRegistryHelper());

        TrickyTrialsBackport.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        CommonConfig.init(FMLPaths.CONFIGDIR.get());

        MinecraftForge.EVENT_BUS.addListener(this::onLivingHurt);

        ModBrewingRecipes.register();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModSoundTypes.register();
        });
    }

    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();

        if (!entity.hasEffect(ModEffects.INFESTED.get())) return;

        if (entity.getRandom().nextFloat() > 0.1f) return;

        int count = 1 + entity.getRandom().nextInt(3);

        for (int i = 0; i < count; i++) {
            InfestedEffect.spawnSilverfish(entity.level(), entity);
        }
    }

    @Mod.EventBusSubscriber(modid = TrickyTrialsBackport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.BOGGED.get(), Bogged.createAttributes().build());
            event.put(ModEntities.BREEZE.get(), Breeze.createAttributes().build());
        }
    }
}
