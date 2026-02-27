package com.drd.trickytrialsbackport.block.entity.trialspawner;

import com.drd.trickytrialsbackport.block.TrialSpawnerBlock;
import com.drd.trickytrialsbackport.registry.ModBlockEntities;
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

    public TrialSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRIAL_SPAWNER.get(), pos, state);

        this.trialSpawner = new TrialSpawner(this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("normal_config")) {
            CompoundTag merged = tag.getCompound("normal_config").copy();
            merged.merge(tag.getCompound("ominous_config"));
            tag.put("config", merged);
        }

        if (tag.contains("config")) {
            this.trialSpawner.load(tag.getCompound("config"));
        }

        if (this.level != null) {
            markUpdated();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        CompoundTag config = new CompoundTag();
        this.trialSpawner.save(config);
        tag.put("config", config);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void setEntityId(EntityType<?> type, RandomSource random) {
        this.trialSpawner.getData().setEntityId(this.trialSpawner, random, type);
        setChanged();
    }

    public TrialSpawner getTrialSpawner() {
        return this.trialSpawner;
    }

    @Override
    public TrialSpawnerState getState() {
        BlockState state = getBlockState();
        if (!state.hasProperty(TrialSpawnerBlock.STATE)) {
            return TrialSpawnerState.INACTIVE;
        }
        return state.getValue(TrialSpawnerBlock.STATE);
    }

    @Override
    public void setState(Level level, TrialSpawnerState newState) {
        setChanged();
        level.setBlockAndUpdate(
                worldPosition,
                getBlockState().setValue(TrialSpawnerBlock.STATE, newState)
        );
    }

    @Override
    public void markUpdated() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }
}
