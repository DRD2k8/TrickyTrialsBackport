package com.drd.trickytrialsbackport.block.entity.trialspawner;

import com.drd.trickytrialsbackport.registry.ModBlockEntities;
import com.drd.trickytrialsbackport.util.ModBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrialSpawnerBlockEntity extends BlockEntity implements Spawner, TrialSpawner.StateAccessor {
    private TrialSpawner trialSpawner;
    private TrialSpawnerData data;

    public TrialSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRIAL_SPAWNER.get(), pos, state);

        PlayerDetector detector = PlayerDetector.NO_CREATIVE_PLAYERS;
        PlayerDetector.EntitySelector selector = PlayerDetector.EntitySelector.SELECT_FROM_LEVEL;

        this.trialSpawner = new TrialSpawner(this, detector, selector);
        this.data = new TrialSpawnerData();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        TrialSpawnerConfig normal = tag.contains("normal_config")
                ? TrialSpawnerConfig.fromTag(tag.getCompound("normal_config"))
                : TrialSpawnerConfig.DEFAULT;

        TrialSpawnerConfig ominous = tag.contains("ominous_config")
                ? TrialSpawnerConfig.fromTag(tag.getCompound("ominous_config"))
                : TrialSpawnerConfig.DEFAULT;

        TrialSpawnerData data = tag.contains("spawner_data")
                ? new TrialSpawnerData(tag.getCompound("spawner_data"))
                : new TrialSpawnerData();

        this.trialSpawner = new TrialSpawner(
                normal,
                ominous,
                data,
                36000,
                14,
                this,
                PlayerDetector.NO_CREATIVE_PLAYERS,
                PlayerDetector.EntitySelector.SELECT_FROM_LEVEL
        );
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("normal_config", this.trialSpawner.normalConfig.toTag());

        tag.put("ominous_config", this.trialSpawner.ominousConfig.toTag());

        CompoundTag dataTag = new CompoundTag();
        this.trialSpawner.getData().save(dataTag);
        tag.put("spawner_data", dataTag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Override
    public void setEntityId(EntityType<?> type, RandomSource random) {
        this.trialSpawner.getData().setEntityId(this.trialSpawner, random, type);
        this.setChanged();
    }

    public TrialSpawner getTrialSpawner() {
        return this.trialSpawner;
    }

    @Override
    public TrialSpawnerState getState() {
        if (!this.getBlockState().hasProperty(ModBlockStateProperties.TRIAL_SPAWNER_STATE)) {
            return TrialSpawnerState.INACTIVE;
        }
        return this.getBlockState().getValue(ModBlockStateProperties.TRIAL_SPAWNER_STATE);
    }

    @Override
    public void setState(Level level, TrialSpawnerState state) {
        this.setChanged();
        level.setBlockAndUpdate(this.worldPosition,
                this.getBlockState().setValue(ModBlockStateProperties.TRIAL_SPAWNER_STATE, state));
    }

    @Override
    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TrialSpawnerBlockEntity be) {
        be.trialSpawner.tick(level, pos, state);
    }
}
