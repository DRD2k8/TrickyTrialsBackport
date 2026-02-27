package com.drd.trickytrialsbackport.block.entity.trialspawner;

import com.drd.trickytrialsbackport.registry.ModEffects;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class TrialSpawnerData {
    public static final String TAG_SPAWN_DATA = "spawn_data";
    private static final String TAG_NEXT_MOB_SPAWNS_AT = "next_mob_spawns_at";

    private static final int DELAY_BETWEEN_PLAYER_SCANS = 20;
    private static final int TRIAL_OMEN_PER_BAD_OMEN_LEVEL = 18000;

    protected final Set<UUID> detectedPlayers = new HashSet<>();
    protected final Set<UUID> currentMobs = new HashSet<>();

    protected long cooldownEndsAt;
    protected long nextMobSpawnsAt;
    protected int totalMobsSpawned;

    protected Optional<SpawnData> nextSpawnData = Optional.empty();
    protected Optional<ResourceKey<LootTable>> ejectingLootTable = Optional.empty();

    @Nullable
    protected Entity displayEntity;

    @Nullable
    private SimpleWeightedRandomList<ItemStack> dispensing;

    protected double spin;
    protected double oSpin;

    public TrialSpawnerData() {
        this(Collections.emptySet(), Collections.emptySet(), 0L, 0L, 0, Optional.empty(), Optional.empty());
    }

    public TrialSpawnerData(
            Set<UUID> detectedPlayers,
            Set<UUID> currentMobs,
            long cooldownEndsAt,
            long nextMobSpawnsAt,
            int totalMobsSpawned,
            Optional<SpawnData> nextSpawnData,
            Optional<ResourceKey<LootTable>> ejectingLootTable
    ) {
        this.detectedPlayers.addAll(detectedPlayers);
        this.currentMobs.addAll(currentMobs);
        this.cooldownEndsAt = cooldownEndsAt;
        this.nextMobSpawnsAt = nextMobSpawnsAt;
        this.totalMobsSpawned = totalMobsSpawned;
        this.nextSpawnData = nextSpawnData;
        this.ejectingLootTable = ejectingLootTable;
    }

    public void reset() {
        this.detectedPlayers.clear();
        this.totalMobsSpawned = 0;
        this.nextMobSpawnsAt = 0L;
        this.cooldownEndsAt = 0L;
        this.currentMobs.clear();
    }

    public void load(CompoundTag tag) {
        if (tag.contains("total_mobs_spawned")) {
            this.totalMobsSpawned = tag.getInt("total_mobs_spawned");
        }
        if (tag.contains("next_mob_spawns_at")) {
            this.nextMobSpawnsAt = tag.getLong("next_mob_spawns_at");
        }
        if (tag.contains("cooldown_ends_at")) {
            this.cooldownEndsAt = tag.getLong("cooldown_ends_at");
        }
    }

    public void save(CompoundTag tag) {
        tag.putInt("total_mobs_spawned", this.totalMobsSpawned);
        tag.putLong("next_mob_spawns_at", this.nextMobSpawnsAt);
        tag.putLong("cooldown_ends_at", this.cooldownEndsAt);
    }

    public boolean hasMobToSpawn(TrialSpawner spawner, RandomSource random) {
        boolean hasId = this.getOrCreateNextSpawnData(spawner, random)
                .getEntityToSpawn()
                .contains("id", 8);
        return hasId || !spawner.getConfig().spawnPotentialsDefinition().isEmpty();
    }

    public boolean hasFinishedSpawningAllMobs(TrialSpawnerConfig config, int players) {
        return this.totalMobsSpawned >= config.calculateTargetTotalMobs(players);
    }

    public boolean haveAllCurrentMobsDied() {
        return this.currentMobs.isEmpty();
    }

    public boolean isReadyToSpawnNextMob(ServerLevel level, TrialSpawnerConfig config, int players) {
        return level.getGameTime() >= this.nextMobSpawnsAt &&
                this.currentMobs.size() < config.calculateTargetSimultaneousMobs(players);
    }

    public int countAdditionalPlayers(BlockPos pos) {
        if (this.detectedPlayers.isEmpty()) {
            Util.logAndPauseIfInIde("Trial Spawner at " + pos + " has no detected players");
        }
        return Math.max(0, this.detectedPlayers.size() - 1);
    }

    public void tryDetectPlayers(ServerLevel level, BlockPos pos, TrialSpawner spawner) {
        if ((pos.asLong() + level.getGameTime()) % DELAY_BETWEEN_PLAYER_SCANS != 0L) {
            return;
        }

        List<Player> players = level.getEntitiesOfClass(
                Player.class,
                new AABB(pos).inflate(spawner.getRequiredPlayerRange())
        );

        List<UUID> detected = players.stream().map(Player::getUUID).toList();
        boolean ominousTrigger = false;

        for (Player p : players) {
            MobEffectInstance badOmen = p.getEffect(MobEffects.BAD_OMEN);
            if (badOmen != null) {
                this.transformBadOmenIntoTrialOmen(p, badOmen);
                ominousTrigger = true;
            }
        }

        if (ominousTrigger && !spawner.isOminous()) {
            level.levelEvent(3020, pos, 0);
            spawner.applyOminous(level, pos);
        }

        if (this.detectedPlayers.addAll(detected)) {
            this.nextMobSpawnsAt = Math.max(level.getGameTime() + 40L, this.nextMobSpawnsAt);

            if (!ominousTrigger) {
                int event = spawner.isOminous() ? 3019 : 3013;
                level.levelEvent(event, pos, this.detectedPlayers.size());
            }
        }
    }

    public void resetAfterBecomingOminous(TrialSpawner spawner, ServerLevel level) {
        this.currentMobs.stream()
                .map(level::getEntity)
                .filter(Objects::nonNull)
                .forEach(e -> {
                    level.levelEvent(3012, e.blockPosition(), TrialSpawner.FlameParticle.NORMAL.encode());
                    e.remove(Entity.RemovalReason.DISCARDED);
                });

        if (!spawner.getOminousConfig().spawnPotentialsDefinition().isEmpty()) {
            this.nextSpawnData = Optional.empty();
        }

        this.totalMobsSpawned = 0;
        this.currentMobs.clear();

        this.nextMobSpawnsAt = level.getGameTime() + spawner.getOminousConfig().ticksBetweenSpawn();
        spawner.markUpdated();

        this.cooldownEndsAt = level.getGameTime() + spawner.getOminousConfig().ticksBetweenItemSpawners();
    }

    private void transformBadOmenIntoTrialOmen(Player player, MobEffectInstance effect) {
        int level = effect.getAmplifier() + 1;
        int duration = TRIAL_OMEN_PER_BAD_OMEN_LEVEL * level;

        player.removeEffect(MobEffects.BAD_OMEN);
        player.addEffect(new MobEffectInstance(ModEffects.TRIAL_OMEN.get(), duration, 0));
    }

    public boolean isReadyToOpenShutter(ServerLevel level, float offset, int delay) {
        long t = this.cooldownEndsAt - delay;
        return level.getGameTime() >= t + offset;
    }

    public boolean isReadyToEjectItems(ServerLevel level, float interval, int delay) {
        long t = this.cooldownEndsAt - delay;
        return ((level.getGameTime() - t) % (long)interval) == 0L;
    }

    public boolean isCooldownFinished(ServerLevel level) {
        return level.getGameTime() >= this.cooldownEndsAt;
    }

    public void setEntityId(TrialSpawner spawner, RandomSource random, EntityType<?> type) {
        this.getOrCreateNextSpawnData(spawner, random)
                .getEntityToSpawn()
                .putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
    }

    protected SpawnData getOrCreateNextSpawnData(TrialSpawner spawner, RandomSource random) {
        if (this.nextSpawnData.isPresent()) {
            return this.nextSpawnData.get();
        }

        SimpleWeightedRandomList<SpawnData> potentials = spawner.getConfig().spawnPotentialsDefinition();

        Optional<SpawnData> chosen = potentials.isEmpty()
                ? Optional.empty()
                : potentials.getRandom(random).map(WeightedEntry.Wrapper::getData);

        this.nextSpawnData = Optional.of(chosen.orElseGet(SpawnData::new));
        spawner.markUpdated();

        return this.nextSpawnData.get();
    }

    @Nullable
    public Entity getOrCreateDisplayEntity(TrialSpawner spawner, Level level, TrialSpawnerState state) {
        if (spawner.canSpawnInLevel(level) && state.hasSpinningMob()) {
            if (this.displayEntity == null) {
                CompoundTag tag = this.getOrCreateNextSpawnData(spawner, level.getRandom()).getEntityToSpawn();
                if (tag.contains("id", 8)) {
                    this.displayEntity = EntityType.loadEntityRecursive(tag, level, Function.identity());
                }
            }
            return this.displayEntity;
        }
        return null;
    }

    public CompoundTag getUpdateTag(TrialSpawnerState state) {
        CompoundTag tag = new CompoundTag();

        if (state == TrialSpawnerState.ACTIVE) {
            tag.putLong(TAG_NEXT_MOB_SPAWNS_AT, this.nextMobSpawnsAt);
        }

        this.nextSpawnData.ifPresent(data -> {
            SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, data)
                    .result()
                    .ifPresent(nbt -> tag.put(TAG_SPAWN_DATA, nbt));
        });

        return tag;
    }

    public double getSpin() {
        return this.spin;
    }

    public double getOSpin() {
        return this.oSpin;
    }

    SimpleWeightedRandomList<ItemStack> getDispensingItems(ServerLevel level, TrialSpawnerConfig config, BlockPos pos) {
        if (this.dispensing != null) {
            return this.dispensing;
        }

        LootTable table = level.getServer()
                .getLootData()
                .getLootTable(config.itemsToDropWhenOminous().location());

        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .create(LootContextParamSets.EMPTY);

        ObjectArrayList<ItemStack> items = table.getRandomItems(params);

        if (items.isEmpty()) {
            return SimpleWeightedRandomList.empty();
        }

        SimpleWeightedRandomList.Builder<ItemStack> builder = SimpleWeightedRandomList.builder();

        for (ItemStack stack : items) {
            builder.add(stack.copyWithCount(1), stack.getCount());
        }

        this.dispensing = builder.build();
        return this.dispensing;
    }

    private static long lowResolutionPosition(ServerLevel level, BlockPos pos) {
        BlockPos coarse = new BlockPos(
                Mth.floor(pos.getX() / 30.0F),
                Mth.floor(pos.getY() / 20.0F),
                Mth.floor(pos.getZ() / 30.0F)
        );
        return level.getSeed() + coarse.asLong();
    }
}
