package com.drd.trickytrialsbackport.block.entity.vault;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class VaultServerData {
    public static final String TAG_NAME = "server_data";

    public static final Codec<VaultServerData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    // rewarded_players: Set<UUID> via List<UUID> codec
                    Codec.list(UUIDUtil.CODEC)
                            .optionalFieldOf("rewarded_players", List.of())
                            .xmap(list -> {
                                Set<UUID> set = new ObjectLinkedOpenHashSet<UUID>();
                                set.addAll(list);
                                return set;
                            }, set -> new ArrayList<>(set))
                            .forGetter(d -> d.rewardedPlayers),

                    // state_updating_resumes_at: long
                    Codec.LONG
                            .optionalFieldOf("state_updating_resumes_at", 0L)
                            .forGetter(d -> d.stateUpdatingResumesAt),

                    // items_to_eject: List<ItemStack>
                    ItemStack.CODEC
                            .listOf()
                            .optionalFieldOf("items_to_eject", List.of())
                            .forGetter(d -> d.itemsToEject),

                    // total_ejections_needed: int
                    Codec.INT
                            .optionalFieldOf("total_ejections_needed", 0)
                            .forGetter(d -> d.totalEjectionsNeeded)
            ).apply(instance, VaultServerData::new)
    );

    private static final int MAX_REWARD_PLAYERS = 128;

    final Set<UUID> rewardedPlayers = new ObjectLinkedOpenHashSet<>();
    long stateUpdatingResumesAt;
    final List<ItemStack> itemsToEject = new ObjectArrayList<>();
    long lastInsertFailTimestamp;
    int totalEjectionsNeeded;
    boolean isDirty;

    VaultServerData(Set<UUID> rewardedPlayers,
                    long stateUpdatingResumesAt,
                    List<ItemStack> itemsToEject,
                    int totalEjectionsNeeded) {
        this.rewardedPlayers.addAll(rewardedPlayers);
        this.stateUpdatingResumesAt = stateUpdatingResumesAt;
        this.itemsToEject.addAll(itemsToEject);
        this.totalEjectionsNeeded = totalEjectionsNeeded;
    }

    public VaultServerData() {
    }

    void setLastInsertFailTimestamp(long time) {
        this.lastInsertFailTimestamp = time;
    }

    long getLastInsertFailTimestamp() {
        return this.lastInsertFailTimestamp;
    }

    Set<UUID> getRewardedPlayers() {
        return this.rewardedPlayers;
    }

    boolean hasRewardedPlayer(Player player) {
        return this.rewardedPlayers.contains(player.getUUID());
    }

    @VisibleForTesting
    public void addToRewardedPlayers(Player player) {
        this.rewardedPlayers.add(player.getUUID());
        if (this.rewardedPlayers.size() > MAX_REWARD_PLAYERS) {
            Iterator<UUID> it = this.rewardedPlayers.iterator();
            if (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
        this.markChanged();
    }

    long stateUpdatingResumesAt() {
        return this.stateUpdatingResumesAt;
    }

    void pauseStateUpdatingUntil(long time) {
        this.stateUpdatingResumesAt = time;
        this.markChanged();
    }

    List<ItemStack> getItemsToEject() {
        return this.itemsToEject;
    }

    void markEjectionFinished() {
        this.totalEjectionsNeeded = 0;
        this.markChanged();
    }

    void setItemsToEject(List<ItemStack> items) {
        this.itemsToEject.clear();
        this.itemsToEject.addAll(items);
        this.totalEjectionsNeeded = this.itemsToEject.size();
        this.markChanged();
    }

    ItemStack getNextItemToEject() {
        if (this.itemsToEject.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = this.itemsToEject.get(this.itemsToEject.size() - 1);
        return stack == null ? ItemStack.EMPTY : stack;
    }

    ItemStack popNextItemToEject() {
        if (this.itemsToEject.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.markChanged();
        ItemStack stack = this.itemsToEject.remove(this.itemsToEject.size() - 1);
        return stack == null ? ItemStack.EMPTY : stack;
    }

    void set(VaultServerData other) {
        this.stateUpdatingResumesAt = other.stateUpdatingResumesAt();
        this.itemsToEject.clear();
        this.itemsToEject.addAll(other.itemsToEject);
        this.rewardedPlayers.clear();
        this.rewardedPlayers.addAll(other.rewardedPlayers);
    }

    private void markChanged() {
        this.isDirty = true;
    }

    public float ejectionProgress() {
        if (this.totalEjectionsNeeded == 1) {
            return 1.0F;
        }
        return 1.0F - Mth.inverseLerp(
                (float) this.getItemsToEject().size(),
                1.0F,
                (float) this.totalEjectionsNeeded
        );
    }
}
