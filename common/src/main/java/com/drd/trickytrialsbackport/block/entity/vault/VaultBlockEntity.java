package com.drd.trickytrialsbackport.block.entity.vault;

import com.drd.trickytrialsbackport.block.VaultBlock;
import com.drd.trickytrialsbackport.registry.ModBlockEntities;
import com.drd.trickytrialsbackport.registry.ModItems;
import com.drd.trickytrialsbackport.registry.ModParticles;
import com.drd.trickytrialsbackport.util.ModBuiltInLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class VaultBlockEntity extends BlockEntity {
    private final VaultConfig config = new VaultConfig();
    private final VaultServerData serverData = new VaultServerData();
    private final VaultSharedData sharedData = new VaultSharedData();
    private VaultState state = VaultState.INACTIVE;

    public VaultBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VAULT.get(), pos, state);
    }

    public VaultState getState() {
        if (!this.getBlockState().hasProperty(VaultBlock.STATE)) {
            return VaultState.INACTIVE;
        }
        return this.getBlockState().getValue(VaultBlock.STATE);
    }

    public void setState(VaultState newState) {
        if (level == null) return;

        VaultState old = getState();
        if (old == newState) return;

        old.onExit((ServerLevel) level, worldPosition, config, sharedData);

        BlockState bs = getBlockState();

        this.state = newState;
        level.setBlock(worldPosition, bs.setValue(VaultBlock.STATE, newState), 3);

        newState.onEnter((ServerLevel) level, worldPosition, config, sharedData, isOminous());

        setChanged();
    }

    private boolean isOminous() {
        BlockState state = getBlockState();
        return state.hasProperty(VaultBlock.OMINOUS) && state.getValue(VaultBlock.OMINOUS);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VaultBlockEntity be) {
        if (level.isClientSide) return;

        ServerLevel server = (ServerLevel) level;

        if (server.getGameTime() < be.serverData.stateUpdatingResumesAt) {
            return;
        }

        VaultState current = be.getState();

        current.onTick(server, pos, be.config, be.sharedData, be.serverData);

        VaultState next = current.tickAndGetNext(server, pos, be.config, be.serverData, be.sharedData);

        if (next != current) {
            current.onTransition(server, pos, next, be.config, be.sharedData, be.isOminous());
            be.setState(next);
        }

        be.spawnConnectionParticles(server);
    }

    public boolean isCorrectKey(ItemStack stack, boolean ominous) {
        return ominous
                ? stack.is(ModItems.OMINOUS_TRIAL_KEY.get())
                : stack.is(ModItems.TRIAL_KEY.get());
    }

    public boolean tryInsertKey(Player player, ItemStack stack, boolean ominous) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        VaultState current = getState();

        if (current == VaultState.UNLOCKING || current == VaultState.EJECTING) {
            return false;
        }

        generateLootForPlayer(serverLevel, player, ominous);
        serverData.rewardedPlayers.add(player.getUUID());

        VaultState next = VaultState.UNLOCKING;
        current.onTransition(serverLevel, worldPosition, next, config, sharedData, ominous);

        setState(next);
        return true;
    }

    private void generateLootForPlayer(ServerLevel level, Player player, boolean ominous) {
        ResourceLocation tableId = ominous
                ? ModBuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS
                : ModBuiltInLootTables.TRIAL_CHAMBERS_REWARD;

        LootTable table = level.getServer().getLootData().getLootTable(tableId);

        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .create(LootContextParamSets.CHEST);

        List<ItemStack> items = table.getRandomItems(params);
        serverData.setItemsToEject(items);
    }

    private void spawnConnectionParticles(ServerLevel level) {
        if (sharedData.connectedPlayers.isEmpty()) return;

        for (UUID id : sharedData.connectedPlayers) {
            Player player = level.getPlayerByUUID(id);
            if (player == null) continue;

            double px = player.getX();
            double py = player.getY() + player.getEyeHeight();
            double pz = player.getZ();

            double vx = worldPosition.getX() + 0.5;
            double vy = worldPosition.getY() + 1.0;
            double vz = worldPosition.getZ() + 0.5;

            level.sendParticles(
                    ModParticles.VAULT_CONNECTION.get(),
                    vx, vy, vz,
                    1,
                    px - vx,
                    py - vy,
                    pz - vz,
                    0.0
            );
        }
    }

    public static void cycleDisplayItem(ServerLevel level, VaultState state, VaultConfig config, VaultSharedData sharedData, BlockPos pos) {
        ResourceLocation tableId = config.overrideLootTableToDisplay != null
                ? config.overrideLootTableToDisplay
                : config.lootTable;

        if (tableId == null) {
            sharedData.setDisplayItem(ItemStack.EMPTY);
            return;
        }

        LootTable table = level.getServer().getLootData().getLootTable(tableId);

        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .create(LootContextParamSets.CHEST);

        List<ItemStack> items = table.getRandomItems(params);

        if (!items.isEmpty()) {
            sharedData.setDisplayItem(items.get(level.random.nextInt(items.size())));
        }
    }

    public VaultConfig getConfig() {
        return config;
    }

    public VaultServerData getServerData() {
        return serverData;
    }

    public VaultSharedData getSharedData() {
        return sharedData;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("state", Tag.TAG_STRING)) {
            String name = tag.getString("state");

            this.state = Arrays.stream(VaultState.values())
                    .filter(s -> s.getSerializedName().equals(name))
                    .findFirst()
                    .orElse(VaultState.INACTIVE);
        }

        if (tag.contains("config", Tag.TAG_COMPOUND)) {
            this.config.load(tag.getCompound("config"));
        }
        if (tag.contains("server_data", Tag.TAG_COMPOUND)) {
            this.serverData.load(tag.getCompound("server_data"));
        }
        if (tag.contains("shared_data", Tag.TAG_COMPOUND)) {
            this.sharedData.load(tag.getCompound("shared_data"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("config", this.config.save());
        tag.put("server_data", this.serverData.save());
        tag.put("shared_data", this.sharedData.save());
    }
}
