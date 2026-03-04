package com.drd.trickytrialsbackport.block.entity.vault;

import com.drd.trickytrialsbackport.util.ModBuiltInLootTables;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VaultServerData {
    public final Set<UUID> rewardedPlayers = new HashSet<>();
    public long stateUpdatingResumesAt = 0L;
    public final List<ItemStack> itemsToEject = new ArrayList<>();
    public int totalEjectionsNeeded = 0;
    private ItemStack currentEjectingItem = ItemStack.EMPTY;
    private int ejectionTicks = 0;
    private static final int EJECTION_DURATION = 20;
    public int previewTicks = 0;
    private boolean ominous;
    private List<ItemStack> previewPool = new ArrayList<>();
    private boolean usedOnce = false;

    public void load(CompoundTag tag) {
        rewardedPlayers.clear();
        itemsToEject.clear();

        if (tag.contains("rewarded_players", Tag.TAG_LIST)) {
            ListTag list = tag.getList("rewarded_players", Tag.TAG_INT_ARRAY);
            for (Tag t : list) {
                if (t instanceof IntArrayTag arr) {
                    rewardedPlayers.add(NbtUtils.loadUUID(arr));
                }
            }
        }

        if (tag.contains("state_updating_resumes_at", Tag.TAG_LONG)) {
            stateUpdatingResumesAt = tag.getLong("state_updating_resumes_at");
        }

        if (tag.contains("items_to_eject", Tag.TAG_LIST)) {
            ListTag list = tag.getList("items_to_eject", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                itemsToEject.add(ItemStack.of(list.getCompound(i)));
            }
        }

        if (tag.contains("total_ejections_needed", Tag.TAG_INT)) {
            totalEjectionsNeeded = tag.getInt("total_ejections_needed");
        }
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        ListTag rewarded = new ListTag();
        for (UUID id : rewardedPlayers) {
            rewarded.add(NbtUtils.createUUID(id));
        }
        tag.put("rewarded_players", rewarded);

        tag.putLong("state_updating_resumes_at", stateUpdatingResumesAt);

        ListTag ejectList = new ListTag();
        for (ItemStack stack : itemsToEject) {
            ejectList.add(stack.save(new CompoundTag()));
        }
        tag.put("items_to_eject", ejectList);

        tag.putInt("total_ejections_needed", totalEjectionsNeeded);

        return tag;
    }

    public boolean isOminous() {
        return ominous;
    }

    public void setOminous(boolean ominous) {
        this.ominous = ominous;
    }

    public List<ItemStack> generatePreviewLoot(ServerLevel level, boolean ominous) {
        ResourceLocation tableId = ominous
                ? ModBuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS
                : ModBuiltInLootTables.TRIAL_CHAMBERS_REWARD;

        LootTable table = level.getServer().getLootData().getLootTable(tableId);

        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.ZERO)
                .create(LootContextParamSets.CHEST);

        List<ItemStack> preview = new ArrayList<>();

        int rolls = ominous ? 7 : 5;

        for (int i = 0; i < rolls; i++) {
            preview.addAll(table.getRandomItems(params));
        }

        preview.removeIf(ItemStack::isEmpty);

        return preview;
    }

    public List<ItemStack> getPreviewPool() {
        return previewPool;
    }

    public void setPreviewPool(List<ItemStack> items) {
        previewPool = items;
    }

    public void pauseStateUpdatingUntil(long gameTime) {
        this.stateUpdatingResumesAt = gameTime;
    }

    public List<ItemStack> getItemsToEject() {
        return itemsToEject;
    }

    public ItemStack popNextPreviewItem() {
        if (this.previewPool.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return this.previewPool.remove(0);
    }

    @Nullable
    public ItemStack popNextItemToEject() {
        if (this.itemsToEject.isEmpty()) {
            this.currentEjectingItem = ItemStack.EMPTY;
            return null;
        }

        this.currentEjectingItem = this.itemsToEject.remove(0);
        this.ejectionTicks = 0;
        return this.currentEjectingItem;
    }

    public ItemStack getNextItemToEject() {
        return itemsToEject.isEmpty() ? ItemStack.EMPTY : itemsToEject.get(0);
    }

    public void markEjectionFinished() {
        itemsToEject.clear();
        totalEjectionsNeeded = 0;
    }

    public float ejectionProgress() {
        if (totalEjectionsNeeded <= 0) return 0f;

        int done = totalEjectionsNeeded - itemsToEject.size();
        return (float) done / (float) totalEjectionsNeeded;
    }

    public void setItemsToEject(List<ItemStack> items) {
        this.itemsToEject.clear();
        for (ItemStack stack : items) {
            this.itemsToEject.add(stack.copy());
        }
        this.totalEjectionsNeeded = this.itemsToEject.size();
    }

    public ItemStack getCurrentEjectingItem() {
        return this.currentEjectingItem;
    }

    public void tickEjection() {
        if (!this.currentEjectingItem.isEmpty()) {
            this.ejectionTicks++;
            if (this.ejectionTicks >= EJECTION_DURATION) {
                this.currentEjectingItem = ItemStack.EMPTY;
            }
        }
    }

    public float ejectionProgress(float partialTicks) {
        if (this.currentEjectingItem.isEmpty()) {
            return 0f;
        }

        return Math.min(1f, (this.ejectionTicks + partialTicks) / (float) EJECTION_DURATION);
    }

    public boolean hasBeenUsed() {
        return usedOnce;
    }

    public void markUsed() {
        usedOnce = true;
    }
}
