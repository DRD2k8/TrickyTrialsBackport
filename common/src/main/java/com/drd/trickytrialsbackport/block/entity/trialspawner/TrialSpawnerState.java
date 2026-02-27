package com.drd.trickytrialsbackport.block.entity.trialspawner;

import com.drd.trickytrialsbackport.entity.OminousItemSpawner;
import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public enum TrialSpawnerState implements StringRepresentable {
    INACTIVE("inactive", 0, TrialSpawnerState.ParticleEmission.NONE, -1.0, false),
    WAITING_FOR_PLAYERS("waiting_for_players", 4, TrialSpawnerState.ParticleEmission.SMALL_FLAMES, 200.0, true),
    ACTIVE("active", 8, TrialSpawnerState.ParticleEmission.FLAMES_AND_SMOKE, 1000.0, true),
    WAITING_FOR_REWARD_EJECTION("waiting_for_reward_ejection", 8, TrialSpawnerState.ParticleEmission.SMALL_FLAMES, -1.0, false),
    EJECTING_REWARD("ejecting_reward", 8, TrialSpawnerState.ParticleEmission.SMALL_FLAMES, -1.0, false),
    COOLDOWN("cooldown", 0, TrialSpawnerState.ParticleEmission.SMOKE_INSIDE_AND_TOP_FACE, -1.0, false);

    private static final float DELAY_BEFORE_EJECT_AFTER_KILLING_LAST_MOB = 40.0F;
    private static final int TIME_BETWEEN_EACH_EJECTION = Mth.floor(30.0F);

    private final String name;
    private final int lightLevel;
    private final double spinningMobSpeed;
    private final TrialSpawnerState.ParticleEmission particleEmission;
    private final boolean isCapableOfSpawning;

    private TrialSpawnerState(
            final String name,
            final int lightLevel,
            final TrialSpawnerState.ParticleEmission particleEmission,
            final double spinningMobSpeed,
            final boolean isCapableOfSpawning
    ) {
        this.name = name;
        this.lightLevel = lightLevel;
        this.particleEmission = particleEmission;
        this.spinningMobSpeed = spinningMobSpeed;
        this.isCapableOfSpawning = isCapableOfSpawning;
    }

    TrialSpawnerState tickAndGetNext(BlockPos pos, TrialSpawner spawner, ServerLevel level) {
        TrialSpawnerData data = spawner.getData();
        TrialSpawnerConfig config = spawner.getConfig();

        return switch (this) {
            case INACTIVE -> data.getOrCreateDisplayEntity(spawner, level, WAITING_FOR_PLAYERS) == null
                    ? this
                    : WAITING_FOR_PLAYERS;

            case WAITING_FOR_PLAYERS -> {
                if (!data.hasMobToSpawn(spawner, level.random)) {
                    yield INACTIVE;
                } else {
                    data.tryDetectPlayers(level, pos, spawner);
                    yield data.detectedPlayers.isEmpty() ? this : ACTIVE;
                }
            }

            case ACTIVE -> {
                if (!data.hasMobToSpawn(spawner, level.random)) {
                    yield INACTIVE;
                } else {
                    int extraPlayers = data.countAdditionalPlayers(pos);
                    data.tryDetectPlayers(level, pos, spawner);

                    if (spawner.isOminous()) {
                        this.spawnOminousOminousItemSpawner(level, pos, spawner);
                    }

                    if (data.hasFinishedSpawningAllMobs(config, extraPlayers)) {
                        if (data.haveAllCurrentMobsDied()) {
                            data.cooldownEndsAt = level.getGameTime() + (long) spawner.getTargetCooldownLength();
                            data.totalMobsSpawned = 0;
                            data.nextMobSpawnsAt = 0L;
                            yield WAITING_FOR_REWARD_EJECTION;
                        }
                    } else if (data.isReadyToSpawnNextMob(level, config, extraPlayers)) {
                        spawner.spawnMob(level, pos).ifPresent(uuid -> {
                            data.currentMobs.add(uuid);
                            data.totalMobsSpawned++;
                            data.nextMobSpawnsAt = level.getGameTime() + (long) config.ticksBetweenSpawn();
                            config.spawnPotentialsDefinition().getRandom(level.getRandom()).ifPresent(wrapper -> {
                                data.nextSpawnData = Optional.of(wrapper.getData());
                                spawner.markUpdated();
                            });
                        });
                    }

                    yield this;
                }
            }

            case WAITING_FOR_REWARD_EJECTION -> {
                if (data.isReadyToOpenShutter(level, DELAY_BEFORE_EJECT_AFTER_KILLING_LAST_MOB, spawner.getTargetCooldownLength())) {
                    level.playSound(null, pos, ModSounds.TRIAL_SPAWNER_OPEN_SHUTTER.get(), SoundSource.BLOCKS);
                    yield EJECTING_REWARD;
                } else {
                    yield this;
                }
            }

            case EJECTING_REWARD -> {
                if (!data.isReadyToEjectItems(level, (float) TIME_BETWEEN_EACH_EJECTION, spawner.getTargetCooldownLength())) {
                    yield this;
                } else if (data.detectedPlayers.isEmpty()) {
                    level.playSound(null, pos, ModSounds.TRIAL_SPAWNER_CLOSE_SHUTTER.get(), SoundSource.BLOCKS);
                    data.ejectingLootTable = Optional.empty();
                    yield COOLDOWN;
                } else {
                    if (data.ejectingLootTable.isEmpty()) {
                        data.ejectingLootTable = config.lootTablesToEject().getRandomValue(level.getRandom());
                    }

                    data.ejectingLootTable.ifPresent(key ->
                            spawner.ejectReward(level, pos, (ResourceLocation)(Object)key)
                    );

                    Iterator<UUID> it = data.detectedPlayers.iterator();
                    if (it.hasNext()) {
                        it.next();
                        it.remove();
                    }

                    yield this;
                }
            }

            case COOLDOWN -> {
                data.tryDetectPlayers(level, pos, spawner);
                if (!data.detectedPlayers.isEmpty()) {
                    data.totalMobsSpawned = 0;
                    data.nextMobSpawnsAt = 0L;
                    yield ACTIVE;
                } else if (data.isCooldownFinished(level)) {
                    data.cooldownEndsAt = 0L;
                    spawner.removeOminous(level, pos);
                    yield WAITING_FOR_PLAYERS;
                } else {
                    yield this;
                }
            }
        };
    }

    private void spawnOminousOminousItemSpawner(ServerLevel level, BlockPos pos, TrialSpawner spawner) {
        TrialSpawnerData data = spawner.getData();
        TrialSpawnerConfig config = spawner.getConfig();

        ItemStack stack = data.getDispensingItems(level, config, pos)
                .getRandomValue(level.random)
                .orElse(ItemStack.EMPTY);

        if (!stack.isEmpty() && this.timeToSpawnItemSpawner(level, data)) {
            calculatePositionToSpawnSpawner(level, pos, spawner, data).ifPresent(spawnPos -> {
                OminousItemSpawner entity = OminousItemSpawner.create(level, stack);
                entity.moveTo(spawnPos);
                level.addFreshEntity(entity);

                float pitch = (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F;
                level.playSound(null, BlockPos.containing(spawnPos),
                        ModSounds.TRIAL_SPAWNER_SPAWN_ITEM_BEGIN.get(), SoundSource.BLOCKS, 1.0F, pitch);

                data.cooldownEndsAt = level.getGameTime() + spawner.getOminousConfig().ticksBetweenItemSpawners();
            });
        }
    }

    private static Optional<Vec3> calculatePositionToSpawnSpawner(
            ServerLevel level,
            BlockPos spawnerPos,
            TrialSpawner spawner,
            TrialSpawnerData data
    ) {
        List<Player> players = data.detectedPlayers.stream()
                .map(level::getPlayerByUUID)
                .filter(Objects::nonNull)
                .filter(p -> !p.isCreative()
                        && !p.isSpectator()
                        && p.isAlive()
                        && p.distanceToSqr(spawnerPos.getCenter())
                        <= (double) Mth.square(spawner.getRequiredPlayerRange()))
                .toList();

        if (players.isEmpty()) {
            return Optional.empty();
        }

        Entity target = selectEntityToSpawnItemAbove(players, data.currentMobs, spawner, spawnerPos, level);
        return target == null ? Optional.empty() : calculatePositionAbove(target, level);
    }

    private static Optional<Vec3> calculatePositionAbove(Entity entity, ServerLevel level) {
        Vec3 base = entity.position();
        double offsetY = entity.getBbHeight() + 2.0F + (float) level.random.nextInt(4);
        Vec3 target = base.add(0.0D, offsetY, 0.0D);

        BlockHitResult hit = level.clip(new ClipContext(
                base,
                target,
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                null
        ));

        Vec3 below = Vec3.atCenterOf(hit.getBlockPos()).add(0.0D, -1.0D, 0.0D);
        BlockPos blockPos = BlockPos.containing(below);

        return !level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty()
                ? Optional.empty()
                : Optional.of(below);
    }

    @Nullable
    private static Entity selectEntityToSpawnItemAbove(
            List<Player> players,
            Set<UUID> currentMobs,
            TrialSpawner spawner,
            BlockPos spawnerPos,
            ServerLevel level
    ) {
        Stream<Entity> mobStream = currentMobs.stream()
                .map(level::getEntity)
                .filter(Objects::nonNull)
                .filter(e -> e.isAlive()
                        && e.distanceToSqr(spawnerPos.getCenter())
                        <= (double) Mth.square(spawner.getRequiredPlayerRange()));

        List<? extends Entity> list = level.random.nextBoolean() ? mobStream.toList() : players;
        if (list.isEmpty()) {
            return null;
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        return Util.getRandom(list, level.random);
    }

    private boolean timeToSpawnItemSpawner(ServerLevel level, TrialSpawnerData data) {
        return level.getGameTime() >= data.cooldownEndsAt;
    }

    public int lightLevel() {
        return this.lightLevel;
    }

    public double spinningMobSpeed() {
        return this.spinningMobSpeed;
    }

    public boolean hasSpinningMob() {
        return this.spinningMobSpeed >= 0.0;
    }

    public boolean isCapableOfSpawning() {
        return this.isCapableOfSpawning;
    }

    public void emitParticles(Level level, BlockPos pos, boolean ominous) {
        this.particleEmission.emit(level, level.getRandom(), pos, ominous);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    static class LightLevel {
        private static final int UNLIT = 0;
        private static final int HALF_LIT = 4;
        private static final int LIT = 8;

        private LightLevel() {
        }
    }

    interface ParticleEmission {
        TrialSpawnerState.ParticleEmission NONE = (level, random, pos, ominous) -> {
        };

        TrialSpawnerState.ParticleEmission SMALL_FLAMES = (level, random, pos, ominous) -> {
            if (random.nextInt(2) == 0) {
                Vec3 center = Vec3.atCenterOf(pos);
                Vec3 vec = randomOffset(center, random, 0.9F);
                addParticle(ominous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME, vec, level);
            }
        };

        TrialSpawnerState.ParticleEmission FLAMES_AND_SMOKE = (level, random, pos, ominous) -> {
            Vec3 center = Vec3.atCenterOf(pos);
            Vec3 vec = randomOffset(center, random, 1.0F);
            addParticle(ParticleTypes.SMOKE, vec, level);
            addParticle(ominous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, vec, level);
        };

        TrialSpawnerState.ParticleEmission SMOKE_INSIDE_AND_TOP_FACE = (level, random, pos, ominous) -> {
            Vec3 center = Vec3.atCenterOf(pos);
            Vec3 vec = randomOffset(center, random, 0.9F);

            if (random.nextInt(3) == 0) {
                addParticle(ParticleTypes.SMOKE, vec, level);
            }

            if (level.getGameTime() % 20L == 0L) {
                Vec3 topCenter = center.add(0.0D, 0.5D, 0.0D);
                int count = level.getRandom().nextInt(4) + 20;

                for (int i = 0; i < count; i++) {
                    addParticle(ParticleTypes.SMOKE, topCenter, level);
                }
            }
        };

        private static Vec3 randomOffset(Vec3 center, RandomSource random, float radius) {
            double dx = (random.nextDouble() - 0.5D) * radius;
            double dy = (random.nextDouble() - 0.5D) * radius;
            double dz = (random.nextDouble() - 0.5D) * radius;
            return center.add(dx, dy, dz);
        }

        private static void addParticle(SimpleParticleType type, Vec3 pos, Level level) {
            level.addParticle(type, pos.x(), pos.y(), pos.z(), 0.0D, 0.0D, 0.0D);
        }

        void emit(Level level, RandomSource random, BlockPos pos, boolean ominous);
    }

    static class SpinningMob {
        private static final double NONE = -1.0;
        private static final double SLOW = 200.0;
        private static final double FAST = 1000.0;

        private SpinningMob() {
        }
    }
}
