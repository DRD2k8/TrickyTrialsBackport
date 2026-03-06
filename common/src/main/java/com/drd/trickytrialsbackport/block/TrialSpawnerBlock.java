package com.drd.trickytrialsbackport.block;

import com.drd.trickytrialsbackport.block.entity.trialspawner.TrialSpawnerBlockEntity;
import com.drd.trickytrialsbackport.block.entity.trialspawner.TrialSpawnerState;
import com.drd.trickytrialsbackport.registry.ModBlockEntities;
import com.drd.trickytrialsbackport.util.ModBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class TrialSpawnerBlock extends BaseEntityBlock {
    public static final EnumProperty<TrialSpawnerState> STATE = ModBlockStateProperties.TRIAL_SPAWNER_STATE;
    public static final BooleanProperty OMINOUS = ModBlockStateProperties.OMINOUS;

    public TrialSpawnerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(STATE, TrialSpawnerState.INACTIVE)
                        .setValue(OMINOUS, Boolean.FALSE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE, OMINOUS);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrialSpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return createTickerHelper(
                type,
                ModBlockEntities.TRIAL_SPAWNER.get(),
                level.isClientSide
                        ? TrialSpawnerBlockEntity::clientTick
                        : TrialSpawnerBlockEntity::serverTick
        );
    }
}
