package com.drd.trickytrialsbackport.fabric;

import com.drd.trickytrialsbackport.config.CommonConfig;
import com.drd.trickytrialsbackport.effect.InfestedEffect;
import com.drd.trickytrialsbackport.fabric.item.CreativeTabPlacements;
import com.drd.trickytrialsbackport.fabric.registry.FabricRegistryHelper;
import com.drd.trickytrialsbackport.fabric.util.ModAttributeBuilders;
import com.drd.trickytrialsbackport.fabric.util.ModBiomeModifiers;
import com.drd.trickytrialsbackport.fabric.util.ModBrewingRecipes;
import com.drd.trickytrialsbackport.registry.ModEffects;
import com.drd.trickytrialsbackport.registry.RegistryHelper;
import com.drd.trickytrialsbackport.util.ModSoundTypes;
import net.fabricmc.api.ModInitializer;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;

public final class TrickyTrialsBackportFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RegistryHelper.setInstance(new FabricRegistryHelper());

        TrickyTrialsBackport.init();
        CreativeTabPlacements.registerTabPlacements();

        ModSoundTypes.register();
        ModAttributeBuilders.registerAttributes();
        ModBiomeModifiers.register();

        CommonConfig.init(FabricLoader.getInstance().getConfigDir());

        AttackEntityCallback.EVENT.register((player, level, hitPos, target, hand) -> {
            if (!(target instanceof LivingEntity living)) {
                return InteractionResult.PASS;
            }

            if (!living.hasEffect(ModEffects.INFESTED.get())) {
                return InteractionResult.PASS;
            }

            if (living.getRandom().nextFloat() > 0.1f) {
                return InteractionResult.PASS;
            }

            int count = 1 + living.getRandom().nextInt(3);

            for (int i = 0; i < count; i++) {
                InfestedEffect.spawnSilverfish(living.level(), living);
            }

            return InteractionResult.PASS;
        });

        ModBrewingRecipes.register();
    }
}
