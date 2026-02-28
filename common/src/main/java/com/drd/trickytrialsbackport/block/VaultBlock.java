package com.drd.trickytrialsbackport.block;

import com.drd.trickytrialsbackport.block.entity.vault.VaultBlockEntity;
import com.drd.trickytrialsbackport.block.entity.vault.VaultState;
import com.drd.trickytrialsbackport.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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

    public static final EnumProperty<VaultState> STATE =
            EnumProperty.create("vault_state", VaultState.class);

    public static final BooleanProperty OMINOUS =
            BooleanProperty.create("ominous");

    public VaultBlock(Properties props) {
        super(props);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(STATE, VaultState.INACTIVE)
                        .setValue(OMINOUS, Boolean.FALSE)
        );
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        ItemStack stack = player.getItemInHand(hand);

        if (stack.isEmpty() || state.getValue(STATE) != VaultState.ACTIVE) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof VaultBlockEntity vault) {
                VaultBlockEntity.Server.tryInsertKey(
                        (ServerLevel) level,
                        pos,
                        state,
                        vault.getConfig(),
                        vault.getServerData(),
                        vault.getSharedData(),
                        player,
                        stack
                );
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VaultBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, STATE, OMINOUS);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {

        if (level.isClientSide) {
            return createTickerHelper(
                    type,
                    ModBlockEntities.VAULT.get(),
                    (lvl, pos, st, be) ->
                            VaultBlockEntity.Client.tick(lvl, pos, st, be.getClientData(), be.getSharedData())
            );
        } else {
            return createTickerHelper(
                    type,
                    ModBlockEntities.VAULT.get(),
                    (lvl, pos, st, be) ->
                            VaultBlockEntity.Server.tick((ServerLevel) lvl, pos, st,
                                    be.getConfig(), be.getServerData(), be.getSharedData())
            );
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
