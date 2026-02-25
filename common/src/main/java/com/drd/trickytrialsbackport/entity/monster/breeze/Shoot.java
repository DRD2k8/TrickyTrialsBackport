package com.drd.trickytrialsbackport.entity.monster.breeze;

import com.drd.trickytrialsbackport.entity.projectile.windcharge.BreezeWindCharge;
import com.drd.trickytrialsbackport.entity.projectile.windcharge.WindCharge;
import com.drd.trickytrialsbackport.registry.ModEntities;
import com.drd.trickytrialsbackport.registry.ModMemoryModuleTypes;
import com.drd.trickytrialsbackport.registry.ModSounds;
import com.google.common.collect.ImmutableMap;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.VisibleForTesting;

public class Shoot extends Behavior<Breeze> {
    private static final int ATTACK_RANGE_MIN_SQRT = 4;
    private static final int ATTACK_RANGE_MAX_SQRT = 256;
    private static final int UNCERTAINTY_BASE = 5;
    private static final int UNCERTAINTY_MULTIPLIER = 4;
    private static final float PROJECTILE_MOVEMENT_SCALE = 0.7F;
    private static final int SHOOT_INITIAL_DELAY_TICKS = Math.round(15.0F);
    private static final int SHOOT_RECOVER_DELAY_TICKS = Math.round(4.0F);
    private static final int SHOOT_COOLDOWN_TICKS = Math.round(10.0F);

    @VisibleForTesting
    public Shoot() {
        super(
                ImmutableMap.of(
                        MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                        ModMemoryModuleTypes.BREEZE_SHOOT_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT,
                        ModMemoryModuleTypes.BREEZE_SHOOT_CHARGING.get(), MemoryStatus.VALUE_ABSENT,
                        ModMemoryModuleTypes.BREEZE_SHOOT_RECOVERING.get(), MemoryStatus.VALUE_ABSENT,
                        ModMemoryModuleTypes.BREEZE_SHOOT.get(), MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                        ModMemoryModuleTypes.BREEZE_JUMP_TARGET.get(), MemoryStatus.VALUE_ABSENT
                ),
                SHOOT_INITIAL_DELAY_TICKS + 1 + SHOOT_RECOVER_DELAY_TICKS
        );
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Breeze breeze) {
        // Replace pose check with custom state check
        if (breeze.getBreezeState() != Breeze.STATE_IDLE) {
            return false;
        }

        return breeze.getBrain()
                .getMemory(MemoryModuleType.ATTACK_TARGET)
                .map(target -> isTargetWithinRange(breeze, target))
                .map(inRange -> {
                    if (!inRange) {
                        breeze.getBrain().eraseMemory(ModMemoryModuleTypes.BREEZE_SHOOT.get());
                    }
                    return inRange;
                })
                .orElse(false);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Breeze breeze, long gameTime) {
        return breeze.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)
                && breeze.getBrain().hasMemoryValue(ModMemoryModuleTypes.BREEZE_SHOOT.get());
    }

    @Override
    protected void start(ServerLevel level, Breeze breeze, long gameTime) {
        breeze.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(target -> {
            breeze.setBreezeState(Breeze.STATE_SHOOTING);
        });

        breeze.getBrain().setMemoryWithExpiry(
                ModMemoryModuleTypes.BREEZE_SHOOT_CHARGING.get(),
                Unit.INSTANCE,
                SHOOT_INITIAL_DELAY_TICKS
        );

        breeze.playSound(ModSounds.BREEZE_INHALE.get(), 1.0F, 1.0F);
    }

    @Override
    protected void stop(ServerLevel level, Breeze breeze, long gameTime) {
        if (breeze.getBreezeState() == Breeze.STATE_SHOOTING) {
            breeze.setBreezeState(Breeze.STATE_IDLE);
        }

        breeze.getBrain().setMemoryWithExpiry(
                ModMemoryModuleTypes.BREEZE_SHOOT_COOLDOWN.get(),
                Unit.INSTANCE,
                SHOOT_COOLDOWN_TICKS
        );

        breeze.getBrain().eraseMemory(ModMemoryModuleTypes.BREEZE_SHOOT.get());
    }

    @Override
    protected void tick(ServerLevel level, Breeze breeze, long gameTime) {
        Brain<Breeze> brain = breeze.getBrain();
        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);

        if (target != null) {
            breeze.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());

            boolean charging = brain.getMemory(ModMemoryModuleTypes.BREEZE_SHOOT_CHARGING.get()).isPresent();
            boolean recovering = brain.getMemory(ModMemoryModuleTypes.BREEZE_SHOOT_RECOVERING.get()).isPresent();

            if (!charging && !recovering) {
                brain.setMemoryWithExpiry(
                        ModMemoryModuleTypes.BREEZE_SHOOT_RECOVERING.get(),
                        Unit.INSTANCE,
                        SHOOT_RECOVER_DELAY_TICKS
                );

                if (isFacingTarget(breeze, target)) {
                    double dx = target.getX() - breeze.getX();
                    double dy = target.getY(0.3) - breeze.getY(0.5);
                    double dz = target.getZ() - breeze.getZ();

                    BreezeWindCharge projectile = new BreezeWindCharge(level, breeze);

                    breeze.playSound(ModSounds.BREEZE_SHOOT.get(), 1.5F, 1.0F);

                    projectile.shoot(dx, dy, dz, 0.7F, (float)(5 - level.getDifficulty().getId() * 4));
                    level.addFreshEntity(projectile);
                }
            }
        }
    }

    @VisibleForTesting
    public static boolean isFacingTarget(Breeze breeze, LivingEntity target) {
        Vec3 view = breeze.getViewVector(1.0F);
        Vec3 dir = target.position().subtract(breeze.position()).normalize();
        return view.dot(dir) > 0.5;
    }

    private static boolean isTargetWithinRange(Breeze breeze, LivingEntity target) {
        double dist = breeze.position().distanceToSqr(target.position());
        return dist > ATTACK_RANGE_MIN_SQRT && dist < ATTACK_RANGE_MAX_SQRT;
    }
}
