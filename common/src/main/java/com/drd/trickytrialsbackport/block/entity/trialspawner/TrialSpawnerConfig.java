package com.drd.trickytrialsbackport.block.entity.trialspawner;

import com.drd.trickytrialsbackport.util.ModBuiltInLootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.SpawnData;

import java.util.Optional;

public record TrialSpawnerConfig(int spawnRange, float totalMobs, float simultaneousMobs, float totalMobsAddedPerPlayer,
                                 float simultaneousMobsAddedPerPlayer, int ticksBetweenSpawn,
                                 SimpleWeightedRandomList<SpawnData> spawnPotentialsDefinition,
                                 SimpleWeightedRandomList<ResourceLocation> lootTablesToEject,
                                 ResourceLocation itemsToDropWhenOminous) {
    public static final TrialSpawnerConfig DEFAULT;

    public static TrialSpawnerConfig fromTag(CompoundTag tag) {
        ListTag potentialsTag = tag.getList("spawn_potentials", Tag.TAG_COMPOUND);
        SimpleWeightedRandomList.Builder<SpawnData> potentialsBuilder = SimpleWeightedRandomList.builder();

        for (Tag t : potentialsTag) {
            CompoundTag entry = (CompoundTag) t;

            int weight = entry.getInt("weight");
            CompoundTag dataTag = entry.getCompound("data");

            SpawnData spawnData = new SpawnData(
                    dataTag.getCompound("entity"),
                    Optional.empty()
            );

            potentialsBuilder.add(spawnData, weight);
        }

        SimpleWeightedRandomList<SpawnData> spawnPotentialsDefinition = potentialsBuilder.build();

        ListTag lootTag = tag.getList("loot_tables_to_eject", Tag.TAG_COMPOUND);
        SimpleWeightedRandomList.Builder<ResourceLocation> lootBuilder = SimpleWeightedRandomList.builder();

        for (Tag t : lootTag) {
            CompoundTag entry = (CompoundTag) t;

            int weight = entry.getInt("weight");
            String id = entry.getString("data");

            lootBuilder.add(new ResourceLocation(id), weight);
        }

        SimpleWeightedRandomList<ResourceLocation> lootTablesToEject = lootBuilder.build();

        ResourceLocation itemsToDropWhenOminous =
                tag.contains("items_to_drop_when_ominous")
                        ? new ResourceLocation(tag.getString("items_to_drop_when_ominous"))
                        : null;

        return new TrialSpawnerConfig(
                tag.getInt("spawn_range"),
                tag.getFloat("total_mobs"),
                tag.getFloat("simultaneous_mobs"),
                tag.getFloat("total_mobs_added_per_player"),
                tag.getFloat("simultaneous_mobs_added_per_player"),
                tag.getInt("ticks_between_spawn"),
                spawnPotentialsDefinition,
                lootTablesToEject,
                itemsToDropWhenOminous
        );
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putInt("spawn_range", this.spawnRange());
        tag.putFloat("total_mobs", this.totalMobs());
        tag.putFloat("simultaneous_mobs", this.simultaneousMobs());
        tag.putFloat("total_mobs_added_per_player", this.totalMobsAddedPerPlayer());
        tag.putFloat("simultaneous_mobs_added_per_player", this.simultaneousMobsAddedPerPlayer());
        tag.putInt("ticks_between_spawn", this.ticksBetweenSpawn());

        ListTag potentialsTag = new ListTag();
        for (WeightedEntry.Wrapper<SpawnData> wrapper : this.spawnPotentialsDefinition().unwrap()) {
            CompoundTag entry = new CompoundTag();

            entry.putInt("weight", wrapper.getWeight().asInt());

            entry.put("data", wrapper.getData().entityToSpawn());

            potentialsTag.add(entry);
        }
        tag.put("spawn_potentials", potentialsTag);

        ListTag lootTag = new ListTag();
        for (WeightedEntry.Wrapper<ResourceLocation> wrapper : this.lootTablesToEject().unwrap()) {
            CompoundTag entry = new CompoundTag();

            entry.putInt("weight", wrapper.getWeight().asInt());
            entry.putString("data", wrapper.getData().toString());

            lootTag.add(entry);
        }
        tag.put("loot_tables_to_eject", lootTag);

        if (this.itemsToDropWhenOminous() != null) {
            tag.putString("items_to_drop_when_ominous", this.itemsToDropWhenOminous().toString());
        }

        return tag;
    }

    public int calculateTargetTotalMobs(int players) {
        return (int) Math.floor(this.totalMobs + this.totalMobsAddedPerPlayer * players);
    }

    public int calculateTargetSimultaneousMobs(int players) {
        return (int) Math.floor(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * players);
    }

    public long ticksBetweenItemSpawners() {
        return 160L;
    }

    static {
        DEFAULT = new TrialSpawnerConfig(
                4,
                6.0F,
                2.0F,
                2.0F,
                1.0F,
                40,
                SimpleWeightedRandomList.empty(),
                SimpleWeightedRandomList.<ResourceLocation>builder()
                        .add(ModBuiltInLootTables.SPAWNER_TRIAL_CHAMBER_CONSUMABLES, 1)
                        .add(ModBuiltInLootTables.SPAWNER_TRIAL_CHAMBER_KEY, 1)
                        .build(),
                ModBuiltInLootTables.SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS
        );
    }
}
