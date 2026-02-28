package com.drd.trickytrialsbackport.block.entity.vault;

import com.drd.trickytrialsbackport.block.VaultBlock;
import com.drd.trickytrialsbackport.registry.ModBlockEntities;
import com.drd.trickytrialsbackport.registry.ModSounds;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VaultBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final VaultServerData serverData = new VaultServerData();
    private final VaultSharedData sharedData = new VaultSharedData();
    private final VaultClientData clientData = new VaultClientData();

    private VaultConfig config = VaultConfig.DEFAULT;

    public VaultBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VAULT.get(), pos, state);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("shared_data", VaultSharedData.CODEC.encodeStart(NbtOps.INSTANCE, this.sharedData)
                .resultOrPartial(LOGGER::error).orElse(new CompoundTag()));
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("config", VaultConfig.CODEC.encodeStart(NbtOps.INSTANCE, this.config)
                .resultOrPartial(LOGGER::error).orElse(new CompoundTag()));

        tag.put("shared_data", VaultSharedData.CODEC.encodeStart(NbtOps.INSTANCE, this.sharedData)
                .resultOrPartial(LOGGER::error).orElse(new CompoundTag()));

        tag.put("server_data", VaultServerData.CODEC.encodeStart(NbtOps.INSTANCE, this.serverData)
                .resultOrPartial(LOGGER::error).orElse(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("server_data")) {
            VaultServerData.CODEC.parse(NbtOps.INSTANCE, tag.get("server_data"))
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(this.serverData::set);
        }

        if (tag.contains("config")) {
            VaultConfig.CODEC.parse(NbtOps.INSTANCE, tag.get("config"))
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(cfg -> this.config = cfg);
        }

        if (tag.contains("shared_data")) {
            VaultSharedData.CODEC.parse(NbtOps.INSTANCE, tag.get("shared_data"))
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(this.sharedData::set);
        }
    }

    @Nullable
    public VaultServerData getServerData() {
        return this.level != null && !this.level.isClientSide ? this.serverData : null;
    }

    public VaultSharedData getSharedData() {
        return this.sharedData;
    }

    public VaultClientData getClientData() {
        return this.clientData;
    }

    public VaultConfig getConfig() {
        return this.config;
    }

    public void setConfig(VaultConfig cfg) {
        this.config = cfg;
    }

    public static final class Client {
        private static final int PARTICLE_TICK_RATE = 20;
        private static final float IDLE_PARTICLE_CHANCE = 0.5F;
        private static final float AMBIENT_SOUND_CHANCE = 0.02F;
        private static final int ACTIVATION_PARTICLE_COUNT = 20;
        private static final int DEACTIVATION_PARTICLE_COUNT = 20;

        public static void tick(Level level, BlockPos pos, BlockState state,
                                VaultClientData client, VaultSharedData shared) {

            client.updateDisplayItemSpin();

            if (level.getGameTime() % 20L == 0L) {
                emitConnectionParticlesForNearbyPlayers(level, pos, state, shared);
            }

            ParticleOptions flame = state.getValue(VaultBlock.OMINOUS)
                    ? ParticleTypes.SOUL_FIRE_FLAME
                    : ParticleTypes.FLAME;

            emitIdleParticles(level, pos, shared, flame);
            playIdleSounds(level, pos, shared);
        }

        private static void emitIdleParticles(Level level, BlockPos pos,
                                              VaultSharedData shared, ParticleOptions flame) {
            RandomSource r = level.getRandom();
            if (r.nextFloat() <= 0.5F) {
                Vec3 v = randomPosInsideCage(pos, r);
                level.addParticle(ParticleTypes.SMOKE, v.x(), v.y(), v.z(), 0, 0, 0);

                if (shared.hasDisplayItem()) {
                    level.addParticle(flame, v.x(), v.y(), v.z(), 0, 0, 0);
                }
            }
        }

        private static void playIdleSounds(Level level, BlockPos pos, VaultSharedData shared) {
            if (!shared.hasDisplayItem()) return;

            RandomSource r = level.getRandom();
            if (r.nextFloat() <= 0.02F) {
                level.playLocalSound(pos, ModSounds.VAULT_AMBIENT.get(), SoundSource.BLOCKS,
                        r.nextFloat() * 0.25F + 0.75F,
                        r.nextFloat() + 0.5F, false);
            }
        }

        private static void emitConnectionParticlesForNearbyPlayers(Level level, BlockPos pos,
                                                                    BlockState state, VaultSharedData shared) {
            Set<UUID> players = shared.getConnectedPlayers();
            if (players.isEmpty()) return;

            Vec3 keyhole = keyholePos(pos, state.getValue(VaultBlock.FACING));

            for (UUID id : players) {
                Player p = level.getPlayerByUUID(id);
                if (p != null && p.blockPosition().distSqr(pos) <= shared.connectedParticlesRange() * shared.connectedParticlesRange()) {
                    emitConnectionParticlesForPlayer(level, keyhole, p);
                }
            }
        }

        private static void emitConnectionParticlesForPlayer(Level level, Vec3 origin, Player p) {
            RandomSource r = level.random;
            Vec3 dir = origin.vectorTo(p.position().add(0, p.getBbHeight() * 0.5, 0));

            int count = Mth.nextInt(r, 2, 5);
            for (int i = 0; i < count; i++) {
                Vec3 off = dir.offsetRandom(r, 1.0F);
                level.addParticle(ParticleTypes.END_ROD, origin.x(), origin.y(), origin.z(),
                        off.x(), off.y(), off.z());
            }
        }

        private static Vec3 randomPosInsideCage(BlockPos pos, RandomSource r) {
            return Vec3.atLowerCornerOf(pos).add(
                    Mth.nextDouble(r, 0.1, 0.9),
                    Mth.nextDouble(r, 0.25, 0.75),
                    Mth.nextDouble(r, 0.1, 0.9)
            );
        }

        private static Vec3 keyholePos(BlockPos pos, Direction facing) {
            return Vec3.atBottomCenterOf(pos)
                    .add(facing.getStepX() * 0.5, 1.75, facing.getStepZ() * 0.5);
        }
    }

    public static final class Server {
        private static final int UNLOCKING_DELAY_TICKS = 14;
        private static final int DISPLAY_CYCLE_TICK_RATE = 20;
        private static final int INSERT_FAIL_SOUND_BUFFER_TICKS = 15;

        public static void tick(ServerLevel level, BlockPos pos, BlockState state,
                                VaultConfig config, VaultServerData server,
                                VaultSharedData shared) {
            VaultState vaultState = state.getValue(VaultBlock.STATE);

            if (shouldCycleDisplayItem(level.getGameTime(), vaultState)) {
                cycleDisplayItemFromLootTable(level, vaultState, config, shared, pos);
            }

            BlockState newState = state;

            if (level.getGameTime() >= server.stateUpdatingResumesAt()) {
                VaultState next = vaultState.tickAndGetNext(level, pos, config, server, shared);
                newState = state.setValue(VaultBlock.STATE, next);

                if (!newState.equals(state)) {
                    setVaultState(level, pos, state, newState, config, shared);
                }
            }

            if (server.isDirty || shared.isDirty) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be != null) be.setChanged();

                if (shared.isDirty) {
                    level.sendBlockUpdated(pos, state, newState, 2);
                }

                server.isDirty = false;
                shared.isDirty = false;
            }
        }

        public static void tryInsertKey(ServerLevel level, BlockPos pos, BlockState state,
                                        VaultConfig config, VaultServerData server,
                                        VaultSharedData shared, Player player,
                                        ItemStack stack) {
            VaultState vaultState = state.getValue(VaultBlock.STATE);

            if (!canEjectReward(config, vaultState)) {
                return;
            }

            if (!isValidToInsert(config, stack)) {
                playInsertFailSound(level, server, pos);
                return;
            }

            if (server.hasRewardedPlayer(player)) {
                playInsertFailSound(level, server, pos);
                return;
            }

            List<ItemStack> items = resolveItemsToEject(level, config, pos, player);
            if (items.isEmpty()) return;

            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            if (!player.isCreative()) {
                stack.shrink(config.keyItem().getCount());
            }

            unlock(level, state, pos, config, server, shared, items);
            server.addToRewardedPlayers(player);
            shared.updateConnectedPlayersWithinRange(level, pos, server, config, config.deactivationRange());
        }

        private static void setVaultState(ServerLevel level, BlockPos pos,
                                          BlockState oldState, BlockState newState,
                                          VaultConfig config, VaultSharedData shared) {

            VaultState old = oldState.getValue(VaultBlock.STATE);
            VaultState next = newState.getValue(VaultBlock.STATE);

            level.setBlock(pos, newState, 3);
            old.onTransition(level, pos, next, config, shared, newState.getValue(VaultBlock.OMINOUS));
        }

        static void cycleDisplayItemFromLootTable(ServerLevel level, VaultState state,
                                                  VaultConfig config, VaultSharedData shared,
                                                  BlockPos pos) {
            if (!canEjectReward(config, state)) {
                shared.setDisplayItem(ItemStack.EMPTY);
                return;
            }

            ResourceLocation table = config.overrideLootTableToDisplay()
                    .orElse(config.lootTable());

            ItemStack item = getRandomDisplayItemFromLootTable(level, pos, table);
            shared.setDisplayItem(item);
        }

        private static ItemStack getRandomDisplayItemFromLootTable(ServerLevel level,
                                                                   BlockPos pos,
                                                                   ResourceLocation table) {

            LootTable loot = level.getServer().getLootData().getLootTable(table);

            LootParams params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .create(LootContextParamSets.CHEST);

            List<ItemStack> items = loot.getRandomItems(params);
            return items.isEmpty() ? ItemStack.EMPTY : items.get(level.random.nextInt(items.size()));
        }

        private static void unlock(ServerLevel level, BlockState state, BlockPos pos,
                                   VaultConfig config, VaultServerData server,
                                   VaultSharedData shared, List<ItemStack> items) {
            server.setItemsToEject(items);
            shared.setDisplayItem(server.getNextItemToEject());
            server.pauseStateUpdatingUntil(level.getGameTime() + UNLOCKING_DELAY_TICKS);

            BlockState newState = state.setValue(VaultBlock.STATE, VaultState.UNLOCKING);
            setVaultState(level, pos, state, newState, config, shared);
        }

        private static List<ItemStack> resolveItemsToEject(ServerLevel level,
                                                           VaultConfig config,
                                                           BlockPos pos,
                                                           Player player) {
            LootTable loot = level.getServer().getLootData().getLootTable(config.lootTable());

            LootParams params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .withLuck(player.getLuck())
                    .create(LootContextParamSets.CHEST);

            return loot.getRandomItems(params);
        }

        private static boolean canEjectReward(VaultConfig config, VaultState state) {
            return config.lootTable() != BuiltInLootTables.EMPTY
                    && !config.keyItem().isEmpty()
                    && state != VaultState.INACTIVE;
        }

        private static boolean isValidToInsert(VaultConfig config, ItemStack stack) {
            return ItemStack.isSameItemSameTags(stack, config.keyItem())
                    && stack.getCount() >= config.keyItem().getCount();
        }

        private static boolean shouldCycleDisplayItem(long time, VaultState state) {
            return time % DISPLAY_CYCLE_TICK_RATE == 0 && state == VaultState.ACTIVE;
        }

        private static void playInsertFailSound(ServerLevel level, VaultServerData server, BlockPos pos) {
            if (level.getGameTime() >= server.getLastInsertFailTimestamp() + INSERT_FAIL_SOUND_BUFFER_TICKS) {
                level.playSound(null, pos, ModSounds.VAULT_INSERT_ITEM_FAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                server.setLastInsertFailTimestamp(level.getGameTime());
            }
        }
    }
}
