package com.drd.trickytrialsbackport.mixin;

import com.drd.trickytrialsbackport.advancement.ModCriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Unique
    private Vec3 ttb$currentImpulseImpactPos;

    @Unique
    private Entity ttb$currentExplosionCause;

    @Unique
    private Vec3 ttb$startingToFallPosition;

    @Unique
    private boolean ttb$wasLaunchedByWindCharge = false;

    @Unique
    private double ttb$launchStartY = 0;

    @Unique
    public void ttb$setCurrentImpulseImpactPos(Vec3 pos) {
        this.ttb$currentImpulseImpactPos = pos;
    }

    @Unique
    public void ttb$setCurrentExplosionCause(Entity cause) {
        this.ttb$currentExplosionCause = cause;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void ttb$trackStartFallingPosition(CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer)(Object)this;

        if (self.fallDistance > 0.0F && this.ttb$startingToFallPosition == null) {
            this.ttb$startingToFallPosition = self.position();

            if (this.ttb$currentImpulseImpactPos != null) {
                ModCriteriaTriggers.FALL_AFTER_EXPLOSION.trigger(
                        self,
                        this.ttb$currentImpulseImpactPos,
                        this.ttb$currentExplosionCause
                );
            }
        }

        if (self.onGround() || self.isInWater() || self.isInLava()) {
            this.ttb$startingToFallPosition = null;
            this.ttb$currentImpulseImpactPos = null;
            this.ttb$currentExplosionCause = null;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void ttb$detectWindChargeLaunch(CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer)(Object)this;

        if (!ttb$wasLaunchedByWindCharge && self.getDeltaMovement().y > 1.0) {
            ttb$wasLaunchedByWindCharge = true;
            ttb$launchStartY = self.getY();
        }

        if (ttb$wasLaunchedByWindCharge && self.fallDistance > 0) {
            double gained = self.getY() - ttb$launchStartY;

            if (gained >= 7.0) {
                ModCriteriaTriggers.FALL_AFTER_EXPLOSION.trigger(
                        self,
                        new Vec3(0, ttb$launchStartY, 0),
                        null
                );
            }

            ttb$wasLaunchedByWindCharge = false;
        }
    }
}
