package com.drd.trickytrialsbackport.block.entity.trialspawner;

import com.drd.trickytrialsbackport.block.TrialSpawnerBlock;
import com.drd.trickytrialsbackport.registry.ModParticles;
import com.drd.trickytrialsbackport.registry.ModSounds;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public final class TrialSpawner {
    public static final String NORMAL_CONFIG_TAG_NAME = "normal_config";
    public static final String OMINOUS_CONFIG_TAG_NAME = "ominous_config";

    public static final int DETECT_PLAYER_SPAWN_BUFFER = 40;
    private static final int DEFAULT_TARGET_COOLDOWN_LENGTH = 36000;
    private static final int DEFAULT_PLAYER_SCAN_RANGE = 14;

    private static final int MAX_MOB_TRACKING_DISTANCE = 47;
    private static final int MAX_MOB_TRACKING_DISTANCE_SQR = Mth.square(47);

    private static final float SPAWNING_AMBIENT_SOUND_CHANCE = 0.02F;

    private final TrialSpawnerConfig normalConfig;
    private final TrialSpawnerConfig ominousConfig;
    private final TrialSpawnerData data;
    private TrialSpawnerConfig config = TrialSpawnerConfig.DEFAULT;

    private final int requiredPlayerRange;
    private final int targetCooldownLength;

    private final TrialSpawner.StateAccessor stateAccessor;

    private boolean overridePeacefulAndMobSpawnRule;
    private boolean isOminous;

    public TrialSpawner(TrialSpawner.StateAccessor accessor) {
        this(
                TrialSpawnerConfig.DEFAULT,
                TrialSpawnerConfig.DEFAULT,
                new TrialSpawnerData(),
                DEFAULT_TARGET_COOLDOWN_LENGTH,
                DEFAULT_PLAYER_SCAN_RANGE,
                accessor
        );
    }

    public TrialSpawner(
            TrialSpawnerConfig normal,
            TrialSpawnerConfig ominous,
            TrialSpawnerData data,
            int cooldown,
            int playerRange,
            TrialSpawner.StateAccessor accessor
    ) {
        this.normalConfig = normal;
        this.ominousConfig = ominous;
        this.data = data;
        this.targetCooldownLength = cooldown;
        this.requiredPlayerRange = playerRange;
        this.stateAccessor = accessor;
    }

    public void load(CompoundTag tag) {
        if (tag.contains("data", 10)) {
            this.data.load(tag.getCompound("data"));
        }

        if (tag.contains("config", 10)) {
            this.config = TrialSpawnerConfig.fromNbt(tag.getCompound("config"));
        }

        this.isOminous = tag.getBoolean("ominous");
    }

    public void save(CompoundTag tag) {
        CompoundTag dataTag = new CompoundTag();
        this.data.save(dataTag);
        tag.put("data", dataTag);

        CompoundTag configTag = new CompoundTag();
        this.config.save(configTag);
        tag.put("config", configTag);

        tag.putBoolean("ominous", this.isOminous);
    }

    public TrialSpawnerConfig getConfig() {
        return this.isOminous ? this.ominousConfig : this.normalConfig;
    }

    public TrialSpawnerConfig getNormalConfig() {
        return this.normalConfig;
    }

    public TrialSpawnerConfig getOminousConfig() {
        return this.ominousConfig;
    }

    public TrialSpawnerData getData() {
        return this.data;
    }

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

    public void applyOminous(ServerLevel level, BlockPos pos) {
        level.setBlock(
                pos,
                level.getBlockState(pos).setValue(TrialSpawnerBlock.OMINOUS, true),
                3
        );
        level.levelEvent(3020, pos, 1);
        this.isOminous = true;
        this.data.resetAfterBecomingOminous(this, level);
    }

    public void removeOminous(ServerLevel level, BlockPos pos) {
        level.setBlock(
                pos,
                level.getBlockState(pos).setValue(TrialSpawnerBlock.OMINOUS, false),
                3
        );
        this.isOminous = false;
    }

    public boolean isOminous() {
        return this.isOminous;
    }

    public void setOminous(boolean value) {
        this.isOminous = value;
    }

    public boolean canSpawnInLevel(Level level) {
        if (this.overridePeacefulAndMobSpawnRule) {
            return true;
        }
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }
        return level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
    }

    public Optional<UUID> spawnMob(ServerLevel level, BlockPos pos) {
        RandomSource random = level.getRandom();
        SpawnData spawnData = this.data.getOrCreateNextSpawnData(this, random);
        CompoundTag entityTag = spawnData.entityToSpawn();
        ListTag posList = entityTag.getList("Pos", 6);

        String id = entityTag.getString("id");
        Optional<EntityType<?>> typeOpt = EntityType.byString(id);
        if (typeOpt.isEmpty()) {
            return Optional.empty();
        }
        EntityType<?> type = typeOpt.get();

        int size = posList.size();
        double x = size >= 1
                ? posList.getDouble(0)
                : pos.getX() + (random.nextDouble() - random.nextDouble()) * this.getConfig().spawnRange() + 0.5;
        double y = size >= 2
                ? posList.getDouble(1)
                : pos.getY() + random.nextInt(3) - 1;
        double z = size >= 3
                ? posList.getDouble(2)
                : pos.getZ() + (random.nextDouble() - random.nextDouble()) * this.getConfig().spawnRange() + 0.5;

        AABB box = type.getDimensions().makeBoundingBox(x, y, z);
        if (!level.noCollision(box)) {
            return Optional.empty();
        }

        Vec3 spawnPos = new Vec3(x, y, z);
        if (!inLineOfSight(level, pos.getCenter(), spawnPos)) {
            return Optional.empty();
        }

        BlockPos spawnBlockPos = BlockPos.containing(spawnPos);

        if (!SpawnPlacements.checkSpawnRules(type, level, MobSpawnType.SPAWNER, spawnBlockPos, random)) {
            return Optional.empty();
        }

        Entity entity = EntityType.loadEntityRecursive(entityTag, level, e -> {
            e.moveTo(x, y, z, random.nextFloat() * 360.0F, 0.0F);
            return e;
        });
        if (entity == null) {
            return Optional.empty();
        }

        if (entity instanceof Mob mob) {
            if (!mob.checkSpawnObstruction(level)) {
                return Optional.empty();
            }

            boolean simpleId = spawnData.getEntityToSpawn().size() == 1
                    && spawnData.getEntityToSpawn().contains("id", 8);
            if (simpleId) {
                if (!mob.checkSpawnObstruction(level)) {
                    return Optional.empty();
                }
            }

            mob.setPersistenceRequired();
        }

        if (!level.tryAddFreshEntityWithPassengers(entity)) {
            return Optional.empty();
        }

        TrialSpawner.FlameParticle flame = this.isOminous
                ? TrialSpawner.FlameParticle.OMINOUS
                : TrialSpawner.FlameParticle.NORMAL;

        level.levelEvent(3011, pos, flame.encode());
        level.levelEvent(3012, spawnBlockPos, flame.encode());
        level.gameEvent(entity, GameEvent.ENTITY_PLACE, spawnBlockPos);

        return Optional.of(entity.getUUID());
    }

    public void ejectReward(ServerLevel level, BlockPos pos, ResourceLocation tableKey) {
        LootTable table = level.getServer().getLootData().getLootTable(tableKey);
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
            return;
        }

        TrialSpawnerState state = this.getState();
        state.emitParticles(level, pos, ominous);

        if (state.hasSpinningMob()) {
            double remaining = Math.max(0L, this.data.nextMobSpawnsAt - level.getGameTime());
            this.data.oSpin = this.data.spin;
            this.data.spin = (this.data.spin + state.spinningMobSpeed() / (remaining + 200.0)) % 360.0;
        }

        if (state.isCapableOfSpawning()) {
            RandomSource random = level.getRandom();
            if (random.nextFloat() <= SPAWNING_AMBIENT_SOUND_CHANCE) {
                SoundEvent sound = ominous
                        ? ModSounds.TRIAL_SPAWNER_AMBIENT_OMINOUS.get()
                        : ModSounds.TRIAL_SPAWNER_AMBIENT.get();
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

    public void tickServer(ServerLevel level, BlockPos pos, boolean ominous) {
        this.isOminous = ominous;
        TrialSpawnerState state = this.getState();

        if (!this.canSpawnInLevel(level)) {
            if (state.isCapableOfSpawning()) {
                this.data.reset();
                this.setState(level, TrialSpawnerState.INACTIVE);
            }
            return;
        }

        if (this.data.currentMobs.removeIf(uuid -> shouldMobBeUntracked(level, pos, uuid))) {
            this.data.nextMobSpawnsAt = level.getGameTime() + this.getConfig().ticksBetweenSpawn();
        }

        TrialSpawnerState next = state.tickAndGetNext(pos, this, level);
        if (next != state) {
            this.setState(level, next);
        }
    }

    private static boolean shouldMobBeUntracked(ServerLevel level, BlockPos spawnerPos, UUID uuid) {
        Entity entity = level.getEntity(uuid);
        return entity == null
                || !entity.isAlive()
                || !entity.level().dimension().equals(level.dimension())
                || entity.blockPosition().distSqr(spawnerPos) > (double) MAX_MOB_TRACKING_DISTANCE_SQR;
    }

    private static boolean inLineOfSight(Level level, Vec3 from, Vec3 to) {
        BlockHitResult hit = level.clip(
                new ClipContext(
                        to,
                        from,
                        ClipContext.Block.VISUAL,
                        ClipContext.Fluid.NONE,
                        null
                )
        );
        return hit.getBlockPos().equals(BlockPos.containing(from)) || hit.getType() == HitResult.Type.MISS;
    }

    public static void addSpawnParticles(Level level, BlockPos pos, RandomSource random, SimpleParticleType particle) {
        for (int i = 0; i < 20; i++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
            level.addParticle(particle, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    public static void addBecomeOminousParticles(Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 20; i++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double vx = random.nextGaussian() * 0.02;
            double vy = random.nextGaussian() * 0.02;
            double vz = random.nextGaussian() * 0.02;
            level.addParticle(ModParticles.TRIAL_OMEN.get(), x, y, z, vx, vy, vz);
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, vx, vy, vz);
        }
    }

    public static void addDetectPlayerParticles(Level level, BlockPos pos, RandomSource random, int count, ParticleOptions particle) {
        for (int i = 0; i < 30 + Math.min(count, 10) * 5; i++) {
            double dx = (2.0F * random.nextFloat() - 1.0F) * 0.65;
            double dz = (2.0F * random.nextFloat() - 1.0F) * 0.65;
            double x = pos.getX() + 0.5 + dx;
            double y = pos.getY() + 0.1 + random.nextFloat() * 0.8;
            double z = pos.getZ() + 0.5 + dz;
            level.addParticle(particle, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    public static void addEjectItemParticles(Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 20; i++) {
            double x = pos.getX() + 0.4 + random.nextDouble() * 0.2;
            double y = pos.getY() + 0.4 + random.nextDouble() * 0.2;
            double z = pos.getZ() + 0.4 + random.nextDouble() * 0.2;
            double vx = random.nextGaussian() * 0.02;
            double vy = random.nextGaussian() * 0.02;
            double vz = random.nextGaussian() * 0.02;
            level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, vx, vy, vz * 0.25);
            level.addParticle(ParticleTypes.SMOKE, x, y, z, vx, vy, vz);
        }
    }

    @Deprecated(forRemoval = true)
    @VisibleForTesting
    public void overridePeacefulAndMobSpawnRule() {
        this.overridePeacefulAndMobSpawnRule = true;
    }

    public enum FlameParticle {
        NORMAL(ParticleTypes.FLAME),
        OMINOUS(ParticleTypes.SOUL_FIRE_FLAME);

        public final SimpleParticleType particleType;

        FlameParticle(SimpleParticleType type) {
            this.particleType = type;
        }

        public static TrialSpawner.FlameParticle decode(int value) {
            TrialSpawner.FlameParticle[] values = values();
            return value <= values.length && value >= 0 ? values[value] : NORMAL;
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
