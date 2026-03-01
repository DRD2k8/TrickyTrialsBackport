package com.drd.trickytrialsbackport.block.entity.trialspawner;

import com.drd.trickytrialsbackport.registry.ModEffects;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
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
    protected Optional<SpawnData> nextSpawnData;
    protected Optional<ResourceLocation> ejectingLootTable;

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
            Optional<ResourceLocation> ejectingLootTable
    ) {
        this.detectedPlayers.addAll(detectedPlayers);
        this.currentMobs.addAll(currentMobs);
        this.cooldownEndsAt = cooldownEndsAt;
        this.nextMobSpawnsAt = nextMobSpawnsAt;
        this.totalMobsSpawned = totalMobsSpawned;
        this.nextSpawnData = nextSpawnData;
        this.ejectingLootTable = ejectingLootTable;
    }

    public TrialSpawnerData(CompoundTag tag) {
        this.totalMobsSpawned = tag.getInt("total_mobs_spawned");
        this.nextMobSpawnsAt = tag.getLong("next_mob_spawns_at");
        this.cooldownEndsAt = tag.getLong("cooldown_ends_at");

        this.detectedPlayers.clear();
        ListTag players = tag.getList("detected_players", Tag.TAG_INT_ARRAY);
        for (Tag t : players) {
            this.detectedPlayers.add(NbtUtils.loadUUID((IntArrayTag) t));
        }

        this.currentMobs.clear();
        ListTag mobs = tag.getList("current_mobs", Tag.TAG_INT_ARRAY);
        for (Tag t : mobs) {
            this.currentMobs.add(NbtUtils.loadUUID((IntArrayTag) t));
        }

        if (tag.contains("next_spawn_data")) {
            this.nextSpawnData = SpawnData.CODEC
                    .parse(NbtOps.INSTANCE, tag.get("next_spawn_data"))
                    .result();
        } else {
            this.nextSpawnData = Optional.empty();
        }
    }

    public void save(CompoundTag tag) {
        tag.putInt("total_mobs_spawned", this.totalMobsSpawned);
        tag.putLong("next_mob_spawns_at", this.nextMobSpawnsAt);
        tag.putLong("cooldown_ends_at", this.cooldownEndsAt);

        ListTag players = new ListTag();
        for (UUID id : this.detectedPlayers) {
            players.add(NbtUtils.createUUID(id));
        }
        tag.put("detected_players", players);

        ListTag mobs = new ListTag();
        for (UUID id : this.currentMobs) {
            mobs.add(NbtUtils.createUUID(id));
        }
        tag.put("current_mobs", mobs);

        this.nextSpawnData.ifPresent(data -> {
            SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, data)
                    .result()
                    .ifPresent(nbt -> tag.put("next_spawn_data", nbt));
        });
    }

    public void reset() {
        this.detectedPlayers.clear();
        this.totalMobsSpawned = 0;
        this.nextMobSpawnsAt = 0L;
        this.cooldownEndsAt = 0L;
        this.currentMobs.clear();
    }

    public boolean hasMobToSpawn(TrialSpawner spawner, RandomSource random) {
        boolean flag = this.getOrCreateNextSpawnData(spawner, random).getEntityToSpawn().contains("id", 8);
        return flag || !spawner.getConfig().spawnPotentialsDefinition().isEmpty();
    }

    public boolean hasFinishedSpawningAllMobs(TrialSpawnerConfig config, int additionalPlayers) {
        return this.totalMobsSpawned >= config.calculateTargetTotalMobs(additionalPlayers);
    }

    public boolean haveAllCurrentMobsDied() {
        return this.currentMobs.isEmpty();
    }

    public boolean isReadyToSpawnNextMob(ServerLevel level, TrialSpawnerConfig config, int additionalPlayers) {
        return level.getGameTime() >= this.nextMobSpawnsAt
                && this.currentMobs.size() < config.calculateTargetSimultaneousMobs(additionalPlayers);
    }

    public int countAdditionalPlayers(BlockPos pos) {
        if (this.detectedPlayers.isEmpty()) {
            Util.logAndPauseIfInIde("Trial Spawner at " + pos + " has no detected players");
        }

        return Math.max(0, this.detectedPlayers.size() - 1);
    }

    public void tryDetectPlayers(ServerLevel level, BlockPos pos, TrialSpawner spawner) {
        boolean skip = (pos.asLong() + level.getGameTime()) % 20L != 0L;
        if (skip) {
            return;
        }

        if (spawner.getState().equals(TrialSpawnerState.COOLDOWN) && spawner.isOminous()) {
            return;
        }

        List<UUID> detected = spawner.getPlayerDetector()
                .detect(level, spawner.getEntitySelector(), pos, (double) spawner.getRequiredPlayerRange(), true);

        Player ominousPlayer = null;

        for (UUID uuid : detected) {
            Player player = level.getPlayerByUUID(uuid);
            if (player != null) {
                if (player.hasEffect(MobEffects.BAD_OMEN)) {
                    this.transformBadOmenIntoTrialOmen(player, player.getEffect(MobEffects.BAD_OMEN));
                    ominousPlayer = player;
                } else if (player.hasEffect(ModEffects.TRIAL_OMEN.get())) {
                    ominousPlayer = player;
                }
            }
        }

        boolean becomingOminous = !spawner.isOminous() && ominousPlayer != null;
        if (!spawner.getState().equals(TrialSpawnerState.COOLDOWN) || becomingOminous) {
            if (becomingOminous) {
                level.levelEvent(3020, BlockPos.containing(ominousPlayer.getEyePosition()), 0);
                spawner.applyOminous(level, pos);
            }

            boolean firstDetection = spawner.getData().detectedPlayers.isEmpty();
            List<UUID> finalList = firstDetection
                    ? detected
                    : spawner.getPlayerDetector()
                    .detect(level, spawner.getEntitySelector(), pos, (double) spawner.getRequiredPlayerRange(), false);

            if (this.detectedPlayers.addAll(finalList)) {
                this.nextMobSpawnsAt = Math.max(level.getGameTime() + 40L, this.nextMobSpawnsAt);
                if (!becomingOminous) {
                    int eventId = spawner.isOminous() ? 3019 : 3013;
                    level.levelEvent(eventId, pos, this.detectedPlayers.size());
                }
            }
        }
    }

    public void resetAfterBecomingOminous(TrialSpawner spawner, ServerLevel level) {
        this.currentMobs.stream().map(level::getEntity).forEach(entity -> {
            if (entity != null) {
                level.levelEvent(3012, entity.blockPosition(), TrialSpawner.FlameParticle.NORMAL.encode());
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        });

        if (!spawner.getOminousConfig().spawnPotentialsDefinition().isEmpty()) {
            this.nextSpawnData = Optional.empty();
        }

        this.totalMobsSpawned = 0;
        this.currentMobs.clear();
        this.nextMobSpawnsAt = level.getGameTime() + (long) spawner.getOminousConfig().ticksBetweenSpawn();
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
        long start = this.cooldownEndsAt - (long) delay;
        return (float) level.getGameTime() >= (float) start + offset;
    }

    public boolean isReadyToEjectItems(ServerLevel level, float interval, int delay) {
        long start = this.cooldownEndsAt - (long) delay;
        return (float) (level.getGameTime() - start) % interval == 0.0F;
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
        } else {
            SimpleWeightedRandomList<SpawnData> potentials = spawner.getConfig().spawnPotentialsDefinition();
            Optional<SpawnData> chosen = potentials.isEmpty()
                    ? this.nextSpawnData
                    : potentials.getRandom(random).map(WeightedEntry.Wrapper::getData);

            this.nextSpawnData = Optional.of(chosen.orElseGet(SpawnData::new));
            spawner.markUpdated();
            return this.nextSpawnData.get();
        }
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
        } else {
            return null;
        }
    }

    public CompoundTag getUpdateTag(TrialSpawnerState state) {
        CompoundTag tag = new CompoundTag();

        if (state == TrialSpawnerState.ACTIVE) {
            tag.putLong("next_mob_spawns_at", this.nextMobSpawnsAt);
        }

        this.nextSpawnData.ifPresent(spawnData -> {
            tag.put("spawn_data", spawnData.getEntityToSpawn().copy());
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
        } else {
            LootTable lootTable = level.getServer().getLootData().getLootTable(config.itemsToDropWhenOminous());
            LootParams lootParams = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);

            ObjectArrayList<ItemStack> items = lootTable.getRandomItems(lootParams);
            if (items.isEmpty()) {
                return SimpleWeightedRandomList.empty();
            } else {
                SimpleWeightedRandomList.Builder<ItemStack> builder = new SimpleWeightedRandomList.Builder<>();

                for (ItemStack stack : items) {
                    builder.add(stack.copyWithCount(1), stack.getCount());
                }

                this.dispensing = builder.build();
                return this.dispensing;
            }
        }
    }

    private static long lowResolutionPosition(ServerLevel level, BlockPos pos) {
        BlockPos coarse = new BlockPos(
                Mth.floor((float) pos.getX() / 30.0F),
                Mth.floor((float) pos.getY() / 20.0F),
                Mth.floor((float) pos.getZ() / 30.0F)
        );
        return level.getSeed() + coarse.asLong();
    }
}
