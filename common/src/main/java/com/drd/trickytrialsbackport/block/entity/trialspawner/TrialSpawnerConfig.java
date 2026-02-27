package com.drd.trickytrialsbackport.block.entity.trialspawner;

import com.drd.trickytrialsbackport.util.ModLootTables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;

public record TrialSpawnerConfig(int spawnRange, float totalMobs, float simultaneousMobs, float totalMobsAddedPerPlayer, float simultaneousMobsAddedPerPlayer, int ticksBetweenSpawn, int requiredPlayerRange, SimpleWeightedRandomList<SpawnData> spawnPotentialsDefinition, SimpleWeightedRandomList<ResourceKey<LootTable>> lootTablesToEject, ResourceKey<LootTable> itemsToDropWhenOminous) {
    public static final ResourceKey<Registry<LootTable>> LOOT_TABLE_REGISTRY =
            ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "loot_table"));

    public static final TrialSpawnerConfig DEFAULT;
    public static final Codec<TrialSpawnerConfig> CODEC;

    public int calculateTargetTotalMobs(int players) {
        return (int)Math.floor(totalMobs + totalMobsAddedPerPlayer * players);
    }

    public int calculateTargetSimultaneousMobs(int players) {
        return (int)Math.floor(simultaneousMobs + simultaneousMobsAddedPerPlayer * players);
    }

    public long ticksBetweenItemSpawners() {
        return 160L;
    }

    public static TrialSpawnerConfig fromNbt(CompoundTag tag) {
        int spawnRange = tag.getInt("spawn_range");
        float totalMobs = tag.getFloat("total_mobs");
        float simultaneousMobs = tag.getFloat("simultaneous_mobs");
        float totalMobsAddedPerPlayer = tag.getFloat("total_mobs_added_per_player");
        float simultaneousMobsAddedPerPlayer = tag.getFloat("simultaneous_mobs_added_per_player");
        int ticksBetweenSpawn = tag.getInt("ticks_between_spawn");
        int requiredPlayerRange = tag.getInt("required_player_range");

        ListTag potentialsList = tag.getList("spawn_potentials", 10);
        SimpleWeightedRandomList.Builder<SpawnData> potentialsBuilder = SimpleWeightedRandomList.builder();

        for (Tag t : potentialsList) {
            CompoundTag e = (CompoundTag) t;

            CompoundTag entityTag = e.getCompound("entity");

            Optional<SpawnData.CustomSpawnRules> rules =
                    e.contains("custom_spawn_rules")
                            ? SpawnData.CustomSpawnRules.CODEC.parse(NbtOps.INSTANCE, e.get("custom_spawn_rules")).result()
                            : Optional.empty();

            SpawnData data = new SpawnData(entityTag, rules);

            int weight = e.getInt("weight");
            potentialsBuilder.add(data, weight);
        }

        SimpleWeightedRandomList<SpawnData> spawnPotentials = potentialsBuilder.build();

        ListTag lootList = tag.getList("loot_tables_to_eject", 10);
        SimpleWeightedRandomList.Builder<ResourceKey<LootTable>> lootBuilder = SimpleWeightedRandomList.builder();

        ResourceKey<Registry<LootTable>> lootRegistry =
                ResourceKey.createRegistryKey(new ResourceLocation("minecraft:loot_table"));

        for (Tag t : lootList) {
            CompoundTag e = (CompoundTag) t;
            String id = e.getString("id");
            int weight = e.getInt("weight");

            ResourceKey<LootTable> key =
                    ResourceKey.create(lootRegistry, new ResourceLocation(id));

            lootBuilder.add(key, weight);
        }

        SimpleWeightedRandomList<ResourceKey<LootTable>> lootTablesToEject = lootBuilder.build();

        ResourceKey<LootTable> itemsToDropWhenOminous =
                ResourceKey.create(
                        lootRegistry,
                        new ResourceLocation(tag.getString("ominous_loot_table"))
                );

        return new TrialSpawnerConfig(
                spawnRange,
                totalMobs,
                simultaneousMobs,
                totalMobsAddedPerPlayer,
                simultaneousMobsAddedPerPlayer,
                ticksBetweenSpawn,
                requiredPlayerRange,
                spawnPotentials,
                lootTablesToEject,
                itemsToDropWhenOminous
        );
    }

    public void save(CompoundTag tag) {
        tag.putInt("ticks_between_spawn", this.ticksBetweenSpawn());
        tag.putInt("required_player_range", this.requiredPlayerRange());
    }

    static {
        DEFAULT = new TrialSpawnerConfig(
                4,
                6.0F,
                2.0F,
                2.0F,
                1.0F,
                40,
                16,
                SimpleWeightedRandomList.empty(),
                SimpleWeightedRandomList.<ResourceKey<LootTable>>builder()
                        .add(ModLootTables.TRIAL_SPAWNER_CONSUMABLES, 1)
                        .add(ModLootTables.TRIAL_SPAWNER_KEY, 1)
                        .build(),
                ModLootTables.TRIAL_SPAWNER_ITEMS_OMINOUS
        );

        CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.intRange(1, 128)
                                .optionalFieldOf("spawn_range", DEFAULT.spawnRange)
                                .forGetter(TrialSpawnerConfig::spawnRange),

                        Codec.floatRange(0.0F, Float.MAX_VALUE)
                                .optionalFieldOf("total_mobs", DEFAULT.totalMobs)
                                .forGetter(TrialSpawnerConfig::totalMobs),

                        Codec.floatRange(0.0F, Float.MAX_VALUE)
                                .optionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs)
                                .forGetter(TrialSpawnerConfig::simultaneousMobs),

                        Codec.floatRange(0.0F, Float.MAX_VALUE)
                                .optionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer)
                                .forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer),

                        Codec.floatRange(0.0F, Float.MAX_VALUE)
                                .optionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer)
                                .forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer),

                        Codec.intRange(0, Integer.MAX_VALUE)
                                .optionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn)
                                .forGetter(TrialSpawnerConfig::ticksBetweenSpawn),

                        Codec.intRange(0, 128)
                                .optionalFieldOf("required_player_range", DEFAULT.requiredPlayerRange)
                                .forGetter(TrialSpawnerConfig::requiredPlayerRange),

                        SpawnData.LIST_CODEC
                                .optionalFieldOf("spawn_potentials", SimpleWeightedRandomList.empty())
                                .forGetter(TrialSpawnerConfig::spawnPotentialsDefinition),

                        SimpleWeightedRandomList.wrappedCodec(ResourceKey.codec(LOOT_TABLE_REGISTRY))
                                .optionalFieldOf("loot_tables_to_eject", DEFAULT.lootTablesToEject)
                                .forGetter(TrialSpawnerConfig::lootTablesToEject),

                        ResourceKey.codec(LOOT_TABLE_REGISTRY)
                                .optionalFieldOf("items_to_drop_when_ominous", DEFAULT.itemsToDropWhenOminous)
                                .forGetter(TrialSpawnerConfig::itemsToDropWhenOminous)
                ).apply(instance, TrialSpawnerConfig::new)
        );
    }
}
