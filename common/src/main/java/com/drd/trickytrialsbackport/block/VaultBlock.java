package com.drd.trickytrialsbackport.block;

import com.drd.trickytrialsbackport.block.entity.vault.VaultBlockEntity;
import com.drd.trickytrialsbackport.block.entity.vault.VaultState;
import com.drd.trickytrialsbackport.registry.ModBlockEntities;
import com.drd.trickytrialsbackport.registry.ModSounds;
import com.drd.trickytrialsbackport.util.ModBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class VaultBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OMINOUS = ModBlockStateProperties.OMINOUS;
    public static final EnumProperty<VaultState> STATE = ModBlockStateProperties.VAULT_STATE;

    public VaultBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(OMINOUS, Boolean.FALSE)
                        .setValue(STATE, VaultState.INACTIVE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OMINOUS, STATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(OMINOUS, Boolean.FALSE)
                .setValue(STATE, VaultState.INACTIVE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VaultBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return level.isClientSide
                ? null
                : createTickerHelper(
                type,
                ModBlockEntities.VAULT.get(),
                VaultBlockEntity::serverTick
        );
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);

        if (!(level.getBlockEntity(pos) instanceof VaultBlockEntity be)) {
            return InteractionResult.PASS;
        }

        boolean ominous = state.getValue(OMINOUS);

        boolean isCorrectKey = be.isCorrectKey(held, ominous);

        if (!isCorrectKey) {
            level.playSound(null, pos, ModSounds.VAULT_INSERT_ITEM_FAIL.get(), SoundSource.BLOCKS, 1f, 1f);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (be.tryInsertKey(player, held, ominous)) {
            level.playSound(null, pos, ModSounds.VAULT_INSERT_ITEM.get(), SoundSource.BLOCKS, 1f, 1f);

            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        level.playSound(null, pos, ModSounds.VAULT_INSERT_ITEM_FAIL.get(), SoundSource.BLOCKS, 1f, 1f);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
