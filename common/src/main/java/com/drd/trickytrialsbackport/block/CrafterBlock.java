package com.drd.trickytrialsbackport.block;

import com.drd.trickytrialsbackport.block.entity.CrafterBlockEntity;
import com.drd.trickytrialsbackport.registry.ModBlockEntities;
import com.drd.trickytrialsbackport.util.ModBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CrafterBlock extends BaseEntityBlock {
    public static final BooleanProperty CRAFTING;
    public static final BooleanProperty TRIGGERED;
    private static final EnumProperty<FrontAndTop> ORIENTATION;
    private static final int MAX_CRAFTING_TICKS = 6;
    private static final int CRAFTING_TICK_DELAY = 4;

    public CrafterBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(ORIENTATION, FrontAndTop.NORTH_UP)
                        .setValue(TRIGGERED, false)
                        .setValue(CRAFTING, false)
        );
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CrafterBlockEntity crafter) {
            return crafter.getRedstoneSignal();
        }
        return 0;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean powered = level.hasNeighborSignal(pos);
        boolean triggered = state.getValue(TRIGGERED);
        BlockEntity be = level.getBlockEntity(pos);

        if (powered && !triggered) {
            level.scheduleTick(pos, this, CRAFTING_TICK_DELAY);
            level.setBlock(pos, state.setValue(TRIGGERED, true), Block.UPDATE_CLIENTS);
            this.setBlockEntityTriggered(be, true);
        } else if (!powered && triggered) {
            level.setBlock(pos, state.setValue(TRIGGERED, false).setValue(CRAFTING, false), Block.UPDATE_CLIENTS);
            this.setBlockEntityTriggered(be, false);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof CrafterBlockEntity crafter)) return;

        boolean crafted = crafter.tryCraft(level);

        if (crafted) {
            level.setBlock(pos, state.setValue(CRAFTING, true), Block.UPDATE_CLIENTS);
            crafter.setCraftingTicksRemaining(10); // matches vanilla
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.CRAFTER.get(), CrafterBlockEntity::serverTick);
    }

    private void setBlockEntityTriggered(@Nullable BlockEntity be, boolean triggered) {
        if (be instanceof CrafterBlockEntity crafter) {
            crafter.setTriggered(triggered);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        CrafterBlockEntity crafter = new CrafterBlockEntity(pos, state);
        crafter.setTriggered(state.hasProperty(TRIGGERED) && state.getValue(TRIGGERED));
        return crafter;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction front = ctx.getNearestLookingDirection().getOpposite();
        Direction top;
        switch (front) {
            case DOWN:
                top = ctx.getHorizontalDirection().getOpposite();
                break;
            case UP:
                top = ctx.getHorizontalDirection();
                break;
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                top = Direction.UP;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        boolean powered = ctx.getLevel().hasNeighborSignal(ctx.getClickedPos());
        return this.defaultBlockState()
                .setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(front, top))
                .setValue(TRIGGERED, powered)
                .setValue(CRAFTING, false);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CrafterBlockEntity crafter) {
                crafter.setCustomName(stack.getHoverName());
            }
        }

        if (state.getValue(TRIGGERED)) {
            level.scheduleTick(pos, this, CRAFTING_TICK_DELAY);
        }
    }

    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!oldState.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof Container container) {
                Containers.dropContents(level, pos, container);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(oldState, level, pos, newState, isMoving);
        } else {
            super.onRemove(oldState, level, pos, newState, isMoving);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CrafterBlockEntity crafter) {
                player.openMenu(crafter);
                crafter.setLastInteractingPlayer(player);
                crafter.tryCraft((ServerLevel) level);
            }
            return InteractionResult.CONSUME;
        }
    }

    protected void dispenseFrom(BlockState state, ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CrafterBlockEntity crafter) {
            Optional<CraftingRecipe> optRecipe = getPotentialResults(level, crafter);
            if (optRecipe.isEmpty()) {
                level.levelEvent(1050, pos, 0);
            } else {
                crafter.setCraftingTicksRemaining(MAX_CRAFTING_TICKS);
                level.setBlock(pos, state.setValue(CRAFTING, true), Block.UPDATE_CLIENTS);

                CraftingRecipe recipe = optRecipe.get();
                ItemStack result = recipe.assemble(crafter, level.registryAccess());

                this.dispenseItem(level, pos, crafter, result, state);

                recipe.getRemainingItems(crafter).forEach(remaining -> {
                    this.dispenseItem(level, pos, crafter, remaining, state);
                });

                crafter.getItems().forEach(stack -> {
                    if (!stack.isEmpty()) {
                        stack.shrink(1);
                    }
                });

                crafter.setChanged();
                level.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(state));
            }
        }
    }

    public static Optional<CraftingRecipe> getPotentialResults(Level level, CraftingContainer container) {
        return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, level);
    }

    private void dispenseItem(Level level, BlockPos pos, CrafterBlockEntity crafter, ItemStack stack, BlockState state) {
        if (stack.isEmpty()) {
            return;
        }

        Direction front = state.getValue(ORIENTATION).front();
        BlockPos outPos = pos.relative(front);
        Container target = HopperBlockEntity.getContainerAt(level, outPos);
        ItemStack remaining = stack.copy();

        if (target == null || (!(target instanceof CrafterBlockEntity) && stack.getCount() <= target.getMaxStackSize())) {
            if (target != null) {
                while (!remaining.isEmpty()) {
                    int before = remaining.getCount();
                    remaining = HopperBlockEntity.addItem(crafter, target, remaining, front.getOpposite());
                    if (before == remaining.getCount()) {
                        break;
                    }
                }
            }
        } else {
            while (!remaining.isEmpty()) {
                ItemStack single = remaining.copyWithCount(1);
                ItemStack leftover = HopperBlockEntity.addItem(crafter, target, single, front.getOpposite());
                if (!leftover.isEmpty()) {
                    break;
                }
                remaining.shrink(1);
            }
        }

        if (!remaining.isEmpty()) {
            Vec3 spawnPos = Vec3.atCenterOf(pos).relative(front, 0.7);
            DefaultDispenseItemBehavior.spawnItem(level, remaining, 6, front, spawnPos);
            level.levelEvent(1049, pos, 0);
            level.levelEvent(2010, pos, front.get3DDataValue());
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ORIENTATION, rotation.rotation().rotate(state.getValue(ORIENTATION)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ORIENTATION, mirror.rotation().rotate(state.getValue(ORIENTATION)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION, TRIGGERED, CRAFTING);
    }

    static {
        CRAFTING = ModBlockStateProperties.CRAFTING;
        TRIGGERED = BlockStateProperties.TRIGGERED;
        ORIENTATION = BlockStateProperties.ORIENTATION;
    }
}
