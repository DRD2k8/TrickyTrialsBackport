package com.drd.trickytrialsbackport.entity.monster.breeze;

import com.drd.trickytrialsbackport.registry.ModMemoryModuleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class Slide extends Behavior<Breeze> {
    public Slide() {
        super(Map.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, ModMemoryModuleTypes.BREEZE_JUMP_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT, ModMemoryModuleTypes.BREEZE_SHOOT.get(), MemoryStatus.VALUE_ABSENT));
    }

    protected boolean checkExtraStartConditions(ServerLevel p_312721_, Breeze p_311782_) {
        return p_311782_.onGround() && !p_311782_.isInWater() && p_311782_.getPose() == Pose.STANDING;
    }

    protected void start(ServerLevel p_312079_, Breeze p_310251_, long p_310596_) {
        LivingEntity $$3 = (LivingEntity)p_310251_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        if ($$3 != null) {
            boolean $$4 = p_310251_.withinOuterCircleRange($$3.position());
            boolean $$5 = p_310251_.withinMiddleCircleRange($$3.position());
            boolean $$6 = p_310251_.withinInnerCircleRange($$3.position());
            Vec3 $$7 = null;
            if ($$4) {
                $$7 = randomPointInMiddleCircle(p_310251_, $$3);
            } else if ($$6) {
                Vec3 $$8 = DefaultRandomPos.getPosAway(p_310251_, 5, 5, $$3.position());
                if ($$8 != null && $$3.distanceToSqr($$8.x, $$8.y, $$8.z) > $$3.distanceToSqr(p_310251_)) {
                    $$7 = $$8;
                }
            } else if ($$5) {
                $$7 = LandRandomPos.getPos(p_310251_, 5, 3);
            }

            if ($$7 != null) {
                p_310251_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(BlockPos.containing($$7), 0.6F, 1));
            }

        }
    }

    protected void stop(ServerLevel p_309742_, Breeze p_310528_, long p_312496_) {
        p_310528_.getBrain().setMemoryWithExpiry(ModMemoryModuleTypes.BREEZE_JUMP_COOLDOWN.get(), Unit.INSTANCE, 20L);
    }

    private static Vec3 randomPointInMiddleCircle(Breeze p_310635_, LivingEntity p_312574_) {
        Vec3 $$2 = p_312574_.position().subtract(p_310635_.position());
        double $$3 = $$2.length() - Mth.lerp(p_310635_.getRandom().nextDouble(), 8.0, 4.0);
        Vec3 $$4 = $$2.normalize().multiply($$3, $$3, $$3);
        return p_310635_.position().add($$4);
    }
}
