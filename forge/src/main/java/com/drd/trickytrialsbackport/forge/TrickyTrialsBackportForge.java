package com.drd.trickytrialsbackport.forge;

import com.drd.trickytrialsbackport.config.CommonConfig;
import com.drd.trickytrialsbackport.effect.InfestedEffect;
import com.drd.trickytrialsbackport.effect.OozingEffect;
import com.drd.trickytrialsbackport.effect.WeavingEffect;
import com.drd.trickytrialsbackport.effect.WindChargedEffect;
import com.drd.trickytrialsbackport.entity.monster.Bogged;
import com.drd.trickytrialsbackport.entity.monster.breeze.Breeze;
import com.drd.trickytrialsbackport.forge.registry.ForgeRegistryHelper;
import com.drd.trickytrialsbackport.forge.util.ModBrewingRecipes;
import com.drd.trickytrialsbackport.registry.ModEffects;
import com.drd.trickytrialsbackport.registry.ModEntities;
import com.drd.trickytrialsbackport.registry.RegistryHelper;
import com.drd.trickytrialsbackport.util.ModSoundTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.ArrayList;
import java.util.List;

@Mod(TrickyTrialsBackport.MOD_ID)
public final class TrickyTrialsBackportForge {
    public TrickyTrialsBackportForge() {
        RegistryHelper.setInstance(new ForgeRegistryHelper());

        TrickyTrialsBackport.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        CommonConfig.init(FMLPaths.CONFIGDIR.get());

        MinecraftForge.EVENT_BUS.addListener(this::infestedEffect);
        MinecraftForge.EVENT_BUS.addListener(this::oozingEffect);
        MinecraftForge.EVENT_BUS.addListener(this::weavingEffect);
        MinecraftForge.EVENT_BUS.addListener(this::windChargedEffect);

        ModBrewingRecipes.register();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModSoundTypes.register();
        });
    }

    public void infestedEffect(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();

        if (!entity.hasEffect(ModEffects.INFESTED.get())) return;

        if (entity.getRandom().nextFloat() > 0.1f) return;

        int count = 1 + entity.getRandom().nextInt(3);

        for (int i = 0; i < count; i++) {
            InfestedEffect.spawnSilverfish(entity.level(), entity);
        }
    }

    public void oozingEffect(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        MobEffectInstance inst = entity.getEffect(ModEffects.OOZING.get());
        if (inst == null) return;

        MobEffect effect = inst.getEffect();
        if (!(effect instanceof OozingEffect oozing)) return;

        Level level = entity.level();
        RandomSource random = entity.getRandom();

        int intended = oozing.getSpawnedCount(random);
        int maxCramming = level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);

        List<Slime> nearby = new ArrayList<>();
        AABB box = entity.getBoundingBox().inflate(2.0);

        level.getEntities(EntityType.SLIME, box, slime -> slime != entity, nearby, maxCramming);

        int spawnCount = Mth.clamp(intended - nearby.size(), 0, maxCramming);

        for (int i = 0; i < spawnCount; i++) {
            OozingEffect.spawnSlime(level, entity.getX(), entity.getY() + 0.5, entity.getZ());
        }
    }

    public void weavingEffect(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        MobEffectInstance inst = entity.getEffect(ModEffects.WEAVING.get());
        if (inst == null) return;

        MobEffect effect = inst.getEffect();
        if (!(effect instanceof WeavingEffect weaving)) return;

        Level level = entity.level();
        RandomSource random = entity.getRandom();

        if (!(entity instanceof Player) &&
                !level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }

        int maxCobwebs = weaving.getMaxCobwebs(random);
        WeavingEffect.spawnCobwebsRandomlyAround(level, random, entity.getOnPos(), maxCobwebs);
    }

    public void windChargedEffect(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        MobEffectInstance inst = entity.getEffect(ModEffects.WIND_CHARGED.get());
        if (inst == null) return;

        WindChargedEffect.explodeWindCharge(entity);
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
