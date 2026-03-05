package com.drd.trickytrialsbackport.block.entity.trialspawner;

import com.drd.trickytrialsbackport.block.TrialSpawnerBlock;
import com.drd.trickytrialsbackport.registry.ModParticles;
import com.drd.trickytrialsbackport.registry.ModSounds;
import com.drd.trickytrialsbackport.util.ModBlockStateProperties;
import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public final class TrialSpawner {
    public static final String NORMAL_CONFIG_TAG_NAME = "normal_config";
    public static final String OMINOUS_CONFIG_TAG_NAME = "ominous_config";
    public static final int DETECT_PLAYER_SPAWN_BUFFER = 40;
    private static final int DEFAULT_TARGET_COOLDOWN_LENGTH = 600;
    private static final int DEFAULT_PLAYER_SCAN_RANGE = 14;
    private static final int MAX_MOB_TRACKING_DISTANCE = 47;
    private static final int MAX_MOB_TRACKING_DISTANCE_SQR = Mth.square(47);
    private static final float SPAWNING_AMBIENT_SOUND_CHANCE = 0.02F;

    public final TrialSpawnerConfig normalConfig;
    public final TrialSpawnerConfig ominousConfig;
    private final TrialSpawnerData data;
    private final int requiredPlayerRange;
    private final int targetCooldownLength;
    private final TrialSpawner.StateAccessor stateAccessor;
    private PlayerDetector playerDetector;
    private final PlayerDetector.EntitySelector entitySelector;
    private boolean overridePeacefulAndMobSpawnRule;
    private boolean isOminous;

    public TrialSpawner(TrialSpawner.StateAccessor accessor,
                        PlayerDetector detector,
                        PlayerDetector.EntitySelector selector) {
        this(
                TrialSpawnerConfig.DEFAULT,
                TrialSpawnerConfig.DEFAULT,
                new TrialSpawnerData(),
                DEFAULT_TARGET_COOLDOWN_LENGTH,
                DEFAULT_PLAYER_SCAN_RANGE,
                accessor,
                detector,
                selector
        );
    }

    public TrialSpawner(
            TrialSpawnerConfig normal,
            TrialSpawnerConfig ominous,
            TrialSpawnerData data,
            int targetCooldownLength,
            int requiredPlayerRange,
            TrialSpawner.StateAccessor accessor,
            PlayerDetector detector,
            PlayerDetector.EntitySelector selector
    ) {
        this.normalConfig = normal;
        this.ominousConfig = ominous;
        this.data = data;
        this.targetCooldownLength = targetCooldownLength;
        this.requiredPlayerRange = requiredPlayerRange;
        this.stateAccessor = accessor;
        this.playerDetector = detector;
        this.entitySelector = selector;
    }

    public TrialSpawnerConfig getConfig() {
        return this.isOminous ? this.ominousConfig : this.normalConfig;
    }

    @VisibleForTesting
    public TrialSpawnerConfig getNormalConfig() {
        return this.normalConfig;
    }

    @VisibleForTesting
    public TrialSpawnerConfig getOminousConfig() {
        return this.ominousConfig;
    }

    private TrialSpawnerConfig getOminousConfigForSerialization() {
        return !this.ominousConfig.equals(this.normalConfig) ? this.ominousConfig : TrialSpawnerConfig.DEFAULT;
    }

    public void applyOminous(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, level.getBlockState(pos).setValue(TrialSpawnerBlock.OMINOUS, true), 3);
        level.levelEvent(3020, pos, 1);
        this.isOminous = true;
        this.data.resetAfterBecomingOminous(this, level);
    }

    public void removeOminous(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, level.getBlockState(pos).setValue(TrialSpawnerBlock.OMINOUS, false), 3);
        this.isOminous = false;
    }

    public boolean isOminous() {
        return this.isOminous;
    }

    public TrialSpawnerData getData() {
        return this.data;
    }

    // Always 30 seconds (600 ticks)
    public int getTargetCooldownLength() {
        return this.targetCooldownLength;
    }

    public int getRequiredPlayerRange() {
        return this.requiredPlayerRange;
    }

    public TrialSpawnerState getState() {
        return this.stateAccessor.getState();
    }

    public void setState(Level level, TrialSpawnerState state) {
        this.stateAccessor.setState(level, state);
    }

    public void markUpdated() {
        this.stateAccessor.markUpdated();
    }

    public PlayerDetector getPlayerDetector() {
        return this.playerDetector;
    }

    public PlayerDetector.EntitySelector getEntitySelector() {
        return this.entitySelector;
    }

    public boolean canSpawnInLevel(Level level) {
        if (this.overridePeacefulAndMobSpawnRule) {
            return true;
        } else {
            return level.getDifficulty() != Difficulty.PEACEFUL
                    && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
        }
    }

    public Optional<UUID> spawnMob(ServerLevel level, BlockPos spawnerPos) {
        RandomSource random = level.getRandom();
        SpawnData spawnData = this.data.getOrCreateNextSpawnData(this, random);
        CompoundTag entityTag = spawnData.entityToSpawn();
        ListTag posList = entityTag.getList("Pos", 6);

        Optional<EntityType<?>> type = EntityType.by(entityTag);
        if (type.isEmpty()) {
            return Optional.empty();
        } else {
            int size = posList.size();
            double x = size >= 1
                    ? posList.getDouble(0)
                    : (double) spawnerPos.getX() + (random.nextDouble() - random.nextDouble()) * (double) this.getConfig().spawnRange() + 0.5;
            double y = size >= 2 ? posList.getDouble(1) : (double) (spawnerPos.getY() + random.nextInt(3) - 1);
            double z = size >= 3
                    ? posList.getDouble(2)
                    : (double) spawnerPos.getZ() + (random.nextDouble() - random.nextDouble()) * (double) this.getConfig().spawnRange() + 0.5;

            if (!level.noCollision(type.get().getAABB(x, y, z))) {
                return Optional.empty();
            } else {
                Vec3 spawnPos = new Vec3(x, y, z);

                if (!inLineOfSight(level, spawnerPos.getCenter(), spawnPos)) {
                    return Optional.empty();
                } else {
                    BlockPos blockPos = BlockPos.containing(spawnPos);

                    if (!SpawnPlacements.checkSpawnRules(type.get(), level, MobSpawnType.SPAWNER, blockPos, random)) {
                        return Optional.empty();
                    } else {
                        Entity entity = EntityType.loadEntityRecursive(entityTag, level, e -> {
                            e.moveTo(x, y, z, random.nextFloat() * 360.0F, 0.0F);
                            return e;
                        });

                        if (entity == null) {
                            return Optional.empty();
                        } else {
                            if (entity instanceof Mob mob) {
                                if (!mob.checkSpawnObstruction(level)) {
                                    return Optional.empty();
                                }

                                boolean simple = spawnData.getEntityToSpawn().size() == 1
                                        && spawnData.getEntityToSpawn().contains("id", 8);

                                if (simple) {
                                    mob.finalizeSpawn(
                                            level,
                                            level.getCurrentDifficultyAt(mob.blockPosition()),
                                            MobSpawnType.SPAWNER,
                                            null,
                                            null
                                    );
                                }

                                mob.setPersistenceRequired();
                            }

                            if (!level.tryAddFreshEntityWithPassengers(entity)) {
                                return Optional.empty();
                            } else {
                                TrialSpawner.FlameParticle flame = this.isOminous
                                        ? TrialSpawner.FlameParticle.OMINOUS
                                        : TrialSpawner.FlameParticle.NORMAL;

                                level.levelEvent(3011, spawnerPos, flame.encode());
                                level.levelEvent(3012, blockPos, flame.encode());
                                level.gameEvent(entity, GameEvent.ENTITY_PLACE, blockPos);

                                return Optional.of(entity.getUUID());
                            }
                        }
                    }
                }
            }
        }
    }

    public void ejectReward(ServerLevel level, BlockPos pos, ResourceLocation lootTableId) {
        LootTable table = level.getServer().getLootData().getLootTable(lootTableId);
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        ObjectArrayList<ItemStack> items = table.getRandomItems(params);
        if (!items.isEmpty()) {
            for (ItemStack stack : items) {
                DefaultDispenseItemBehavior.spawnItem(
                        level,
                        stack,
                        2,
                        Direction.UP,
                        Vec3.atBottomCenterOf(pos).relative(Direction.UP, 1.2)
                );
            }
            level.levelEvent(3014, pos, 0);
        }
    }

    public void tickClient(Level level, BlockPos pos, boolean ominous) {
        if (!this.canSpawnInLevel(level)) {
            this.data.oSpin = this.data.spin;
        } else {
            TrialSpawnerState state = this.getState();
            state.emitParticles(level, pos, ominous);
            if (state.hasSpinningMob()) {
                double remaining = (double) Math.max(0L, this.data.nextMobSpawnsAt - level.getGameTime());
                this.data.oSpin = this.data.spin;
                this.data.spin = (this.data.spin + state.spinningMobSpeed() / (remaining + 200.0)) % 360.0;
            }

            if (state.isCapableOfSpawning()) {
                RandomSource random = level.getRandom();
                if (random.nextFloat() <= SPAWNING_AMBIENT_SOUND_CHANCE) {
                    SoundEvent sound = ominous ? ModSounds.TRIAL_SPAWNER_AMBIENT_OMINOUS.get() : ModSounds.TRIAL_SPAWNER_AMBIENT.get();
                    level.playLocalSound(
                            pos,
                            sound,
                            SoundSource.BLOCKS,
                            random.nextFloat() * 0.25F + 0.75F,
                            random.nextFloat() + 0.5F,
                            false
                    );
                }
            }
        }
    }

    public void tickServer(ServerLevel level, BlockPos pos, boolean ominous) {
        this.isOminous = ominous;
        TrialSpawnerState state = this.getState();
        if (!this.canSpawnInLevel(level)) {
            if (state.isCapableOfSpawning()) {
                this.data.reset();
                this.setState(level, TrialSpawnerState.INACTIVE);
            }
        } else {
            // Clean up tracked mobs (dead / gone / too far)
            if (this.data.currentMobs.removeIf(uuid -> shouldMobBeUntracked(level, pos, uuid))) {
                this.data.nextMobSpawnsAt = level.getGameTime() + (long) this.getConfig().ticksBetweenSpawn();
            }

            TrialSpawnerState next = state.tickAndGetNext(pos, this, level);
            if (next != state) {
                this.setState(level, next);
            }
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean ominous = state.getValue(ModBlockStateProperties.OMINOUS);

        if (level.isClientSide) {
            this.tickClient(level, pos, ominous);
        } else {
            this.tickServer((ServerLevel) level, pos, ominous);
        }
    }

    private static boolean shouldMobBeUntracked(ServerLevel level, BlockPos pos, UUID id) {
        Entity entity = level.getEntity(id);
        if (entity == null || !entity.isAlive()) {
            return true;
        }
        if (!entity.level().dimension().equals(level.dimension())) {
            return true;
        }
        return entity.blockPosition().distSqr(pos) > (double) MAX_MOB_TRACKING_DISTANCE_SQR;
    }

    private static boolean inLineOfSight(Level level, Vec3 targetCenter, Vec3 from) {
        BlockHitResult hit = level.clip(
                new ClipContext(
                        from,
                        targetCenter,
                        ClipContext.Block.VISUAL,
                        ClipContext.Fluid.NONE,
                        null
                )
        );
        return hit.getBlockPos().equals(BlockPos.containing(targetCenter))
                || hit.getType() == HitResult.Type.MISS;
    }

    public static void addSpawnParticles(Level level, BlockPos pos, RandomSource random, SimpleParticleType particle) {
        for (int i = 0; i < 20; i++) {
            double x = (double) pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double y = (double) pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double z = (double) pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
            level.addParticle(particle, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    public static void addBecomeOminousParticles(Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 20; i++) {
            double x = (double) pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double y = (double) pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double z = (double) pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double vx = random.nextGaussian() * 0.02;
            double vy = random.nextGaussian() * 0.02;
            double vz = random.nextGaussian() * 0.02;
            level.addParticle(ModParticles.TRIAL_OMEN.get(), x, y, z, vx, vy, vz);
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, vx, vy, vz);
        }
    }

    public static void addDetectPlayerParticles(Level level, BlockPos pos, RandomSource random, int players, ParticleOptions particle) {
        for (int i = 0; i < 30 + Math.min(players, 10) * 5; i++) {
            double dx = (double) (2.0F * random.nextFloat() - 1.0F) * 0.65;
            double dz = (double) (2.0F * random.nextFloat() - 1.0F) * 0.65;
            double x = (double) pos.getX() + 0.5 + dx;
            double y = (double) pos.getY() + 0.1 + (double) random.nextFloat() * 0.8;
            double z = (double) pos.getZ() + 0.5 + dz;
            level.addParticle(particle, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    public static void addEjectItemParticles(Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 20; i++) {
            double x = (double) pos.getX() + 0.4 + random.nextDouble() * 0.2;
            double y = (double) pos.getY() + 0.4 + random.nextDouble() * 0.2;
            double z = (double) pos.getZ() + 0.4 + random.nextDouble() * 0.2;
            double vx = random.nextGaussian() * 0.02;
            double vy = random.nextGaussian() * 0.02;
            double vz = random.nextGaussian() * 0.02;
            level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, vx, vy, vz * 0.25);
            level.addParticle(ParticleTypes.SMOKE, x, y, z, vx, vy, vz);
        }
    }

    @VisibleForTesting
    public void setPlayerDetector(PlayerDetector detector) {
        this.playerDetector = detector;
    }

    @VisibleForTesting
    public void overridePeacefulAndMobSpawnRule() {
        this.overridePeacefulAndMobSpawnRule = true;
    }

    public static enum FlameParticle {
        NORMAL(ParticleTypes.FLAME),
        OMINOUS(ParticleTypes.SOUL_FIRE_FLAME);

        public final SimpleParticleType particleType;

        private FlameParticle(final SimpleParticleType type) {
            this.particleType = type;
        }

        public static TrialSpawner.FlameParticle decode(int id) {
            TrialSpawner.FlameParticle[] values = values();
            return id <= values.length && id >= 0 ? values[id] : NORMAL;
        }

        public int encode() {
            return this.ordinal();
        }
    }

    public interface StateAccessor {
        void setState(Level level, TrialSpawnerState state);
        TrialSpawnerState getState();
        void markUpdated();
    }
}
