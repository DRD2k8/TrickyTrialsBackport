package com.drd.trickytrialsbackport.entity.monster.breeze;

import com.drd.trickytrialsbackport.entity.ai.behavior.LongJumpUtil;
import com.drd.trickytrialsbackport.registry.ModMemoryModuleTypes;
import com.drd.trickytrialsbackport.registry.ModSounds;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LongJump extends Behavior<Breeze> {
    private static final int REQUIRED_AIR_BLOCKS_ABOVE = 4;
    private static final double MAX_LINE_OF_SIGHT_TEST_RANGE = 50.0;
    private static final int JUMP_COOLDOWN_TICKS = 10;
    private static final int JUMP_COOLDOWN_WHEN_HURT_TICKS = 2;
    private static final int INHALING_DURATION_TICKS = Math.round(10.0F);
    private static final float MAX_JUMP_VELOCITY = 1.4F;

    private static final ObjectArrayList<Integer> ALLOWED_ANGLES =
            new ObjectArrayList<>(Lists.newArrayList(40, 55, 60, 75, 80));

    @VisibleForTesting
    public LongJump() {
        super(
                Map.of(
                        MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                        ModMemoryModuleTypes.BREEZE_JUMP_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT,
                        ModMemoryModuleTypes.BREEZE_JUMP_INHALING.get(), MemoryStatus.REGISTERED,
                        ModMemoryModuleTypes.BREEZE_JUMP_TARGET.get(), MemoryStatus.REGISTERED,
                        ModMemoryModuleTypes.BREEZE_SHOOT.get(), MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT
                ),
                200
        );
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Breeze breeze) {
        if (!breeze.onGround() && !breeze.isInWater()) {
            return false;
        }

        if (breeze.getBrain().checkMemory(ModMemoryModuleTypes.BREEZE_JUMP_TARGET.get(), MemoryStatus.VALUE_PRESENT)) {
            return true;
        }

        LivingEntity target = breeze.getBrain()
                .getMemory(MemoryModuleType.ATTACK_TARGET)
                .orElse(null);

        if (target == null) return false;

        if (outOfAggroRange(breeze, target)) {
            breeze.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            return false;
        }

        if (tooCloseForJump(breeze, target)) return false;

        if (!canJumpFromCurrentPosition(level, breeze)) return false;

        BlockPos jumpPos = snapToSurface(breeze, randomPointBehindTarget(target, breeze.getRandom()));
        if (jumpPos == null) return false;

        if (!hasLineOfSight(breeze, jumpPos.getCenter()) &&
                !hasLineOfSight(breeze, jumpPos.above(4).getCenter())) {
            return false;
        }

        breeze.getBrain().setMemory(ModMemoryModuleTypes.BREEZE_JUMP_TARGET.get(), jumpPos);
        return true;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Breeze breeze, long time) {
        return breeze.getBreezeState() != Breeze.STATE_IDLE &&
                !breeze.getBrain().hasMemoryValue(ModMemoryModuleTypes.BREEZE_JUMP_COOLDOWN.get());
    }

    @Override
    protected void start(ServerLevel level, Breeze breeze, long time) {
        if (breeze.getBrain().checkMemory(ModMemoryModuleTypes.BREEZE_JUMP_INHALING.get(), MemoryStatus.VALUE_ABSENT)) {
            breeze.getBrain().setMemoryWithExpiry(
                    ModMemoryModuleTypes.BREEZE_JUMP_INHALING.get(),
                    Unit.INSTANCE,
                    INHALING_DURATION_TICKS
            );
        }

        // BEGIN INHALING
        breeze.setBreezeState(Breeze.STATE_INHALING);

        breeze.getBrain().getMemory(ModMemoryModuleTypes.BREEZE_JUMP_TARGET.get())
                .ifPresent(pos -> breeze.lookAt(EntityAnchorArgument.Anchor.EYES, pos.getCenter()));
    }

    @Override
    protected void tick(ServerLevel level, Breeze breeze, long time) {

        if (finishedInhaling(breeze)) {

            Vec3 jumpVec = breeze.getBrain()
                    .getMemory(ModMemoryModuleTypes.BREEZE_JUMP_TARGET.get())
                    .flatMap(pos -> calculateOptimalJumpVector(
                            breeze,
                            breeze.getRandom(),
                            Vec3.atBottomCenterOf(pos)
                    ))
                    .orElse(null);

            if (jumpVec == null) {
                breeze.setBreezeState(Breeze.STATE_IDLE);
                return;
            }

            breeze.playSound(ModSounds.BREEZE_JUMP.get(), 1.0F, 1.0F);

            breeze.setBreezeState(Breeze.STATE_LONG_JUMPING);
            breeze.setYRot(breeze.yBodyRot);
            breeze.setDiscardFriction(true);
            breeze.setDeltaMovement(jumpVec);

        } else if (finishedJumping(breeze)) {
            breeze.playSound(ModSounds.BREEZE_LAND.get(), 1.0F, 1.0F);

            breeze.setBreezeState(Breeze.STATE_IDLE);
            breeze.setDiscardFriction(false);

            boolean hurt = breeze.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);

            breeze.getBrain().setMemoryWithExpiry(
                    ModMemoryModuleTypes.BREEZE_JUMP_COOLDOWN.get(),
                    Unit.INSTANCE,
                    hurt ? JUMP_COOLDOWN_WHEN_HURT_TICKS : JUMP_COOLDOWN_TICKS
            );

            breeze.getBrain().setMemoryWithExpiry(
                    ModMemoryModuleTypes.BREEZE_SHOOT.get(),
                    Unit.INSTANCE,
                    100L
            );
        }
    }

    @Override
    protected void stop(ServerLevel level, Breeze breeze, long time) {
        if (breeze.getBreezeState() == Breeze.STATE_LONG_JUMPING ||
                breeze.getBreezeState() == Breeze.STATE_INHALING) {
            breeze.setBreezeState(Breeze.STATE_IDLE);
        }

        breeze.getBrain().eraseMemory(ModMemoryModuleTypes.BREEZE_JUMP_TARGET.get());
        breeze.getBrain().eraseMemory(ModMemoryModuleTypes.BREEZE_JUMP_INHALING.get());
    }

    private static boolean finishedInhaling(Breeze breeze) {
        return breeze.getBrain()
                .getMemory(ModMemoryModuleTypes.BREEZE_JUMP_INHALING.get())
                .isEmpty()
                && breeze.getBreezeState() == Breeze.STATE_INHALING;
    }

    private static boolean finishedJumping(Breeze breeze) {
        return breeze.getBreezeState() == Breeze.STATE_LONG_JUMPING &&
                breeze.onGround();
    }

    private static boolean outOfAggroRange(Breeze breeze, LivingEntity target) {
        return !target.closerThan(breeze, 24.0);
    }

    private static boolean tooCloseForJump(Breeze breeze, LivingEntity target) {
        return target.distanceTo(breeze) - 4.0F <= 0.0F;
    }

    private static boolean canJumpFromCurrentPosition(ServerLevel level, Breeze breeze) {
        BlockPos pos = breeze.blockPosition();

        for (int i = 1; i <= 4; ++i) {
            BlockPos check = pos.relative(Direction.UP, i);
            if (!level.getBlockState(check).isAir() &&
                    !level.getFluidState(check).is(FluidTags.WATER)) {
                return false;
            }
        }

        return true;
    }

    private static Optional<Vec3> calculateOptimalJumpVector(
            Breeze breeze,
            RandomSource random,
            Vec3 target
    ) {
        List<Integer> angles = Util.shuffledCopy(ALLOWED_ANGLES, random);

        for (int angle : angles) {
            Optional<Vec3> vec = LongJumpUtil.calculateJumpVectorForAngle(
                    breeze,
                    target,
                    MAX_JUMP_VELOCITY,
                    angle,
                    false
            );

            if (vec.isPresent()) return vec;
        }

        return Optional.empty();
    }

    private static Vec3 randomPointBehindTarget(LivingEntity target, RandomSource random) {
        float yaw = target.yHeadRot + 180.0F + (float) random.nextGaussian() * 45.0F;
        float dist = Mth.lerp(random.nextFloat(), 4.0F, 8.0F);
        Vec3 offset = Vec3.directionFromRotation(0.0F, yaw).scale(dist);
        return target.position().add(offset);
    }

    @Nullable
    private static BlockPos snapToSurface(LivingEntity entity, Vec3 pos) {
        ClipContext down = new ClipContext(pos, pos.relative(Direction.DOWN, 10.0),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);

        HitResult hit = entity.level().clip(down);

        if (hit.getType() == HitResult.Type.BLOCK) {
            return BlockPos.containing(hit.getLocation()).above();
        }

        ClipContext up = new ClipContext(pos, pos.relative(Direction.UP, 10.0),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);

        HitResult hit2 = entity.level().clip(up);

        return hit2.getType() == HitResult.Type.BLOCK
                ? BlockPos.containing(hit2.getLocation()).above()
                : null;
    }

    @VisibleForTesting
    public static boolean hasLineOfSight(Breeze breeze, Vec3 pos) {
        Vec3 start = new Vec3(breeze.getX(), breeze.getY(), breeze.getZ());
        if (pos.distanceTo(start) > MAX_LINE_OF_SIGHT_TEST_RANGE) return false;

        return breeze.level().clip(
                new ClipContext(start, pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, breeze)
        ).getType() == HitResult.Type.MISS;
    }
}

