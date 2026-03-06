package com.drd.trickytrialsbackport.fabric;

import com.drd.trickytrialsbackport.config.CommonConfig;
import com.drd.trickytrialsbackport.effect.InfestedEffect;
import com.drd.trickytrialsbackport.effect.OozingEffect;
import com.drd.trickytrialsbackport.effect.WeavingEffect;
import com.drd.trickytrialsbackport.effect.WindChargedEffect;
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
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

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

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
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
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {

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
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            MobEffectInstance inst = entity.getEffect(ModEffects.WIND_CHARGED.get());
            if (inst == null) return;

            WindChargedEffect.explodeWindCharge(entity);
        });

        ModBrewingRecipes.register();
    }
}
