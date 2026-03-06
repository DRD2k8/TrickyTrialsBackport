package com.drd.trickytrialsbackport.fabric.server;

import com.drd.trickytrialsbackport.advancement.ModCriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ServerPlayerHooks {
    public static class FallData {
        public Vec3 startingToFallPosition;
        public Vec3 currentImpulseImpactPos;
        public Entity currentExplosionCause;
    }

    public static class LaunchData {
        public boolean wasLaunchedByWindCharge;
        public double launchStartY;
    }

    public static FallData trackStartFallingPosition(ServerPlayer self, FallData data) {

        if (self.fallDistance > 0.0F && data.startingToFallPosition == null) {
            data.startingToFallPosition = self.position();

            if (data.currentImpulseImpactPos != null) {
                ModCriteriaTriggers.FALL_AFTER_EXPLOSION.trigger(
                        self,
                        data.currentImpulseImpactPos,
                        data.currentExplosionCause
                );
            }
        }

        if (self.onGround() || self.isInWater() || self.isInLava()) {
            data.startingToFallPosition = null;
            data.currentImpulseImpactPos = null;
            data.currentExplosionCause = null;
        }

        return data;
    }

    public static LaunchData detectWindChargeLaunch(ServerPlayer self, LaunchData data) {

        if (!data.wasLaunchedByWindCharge && self.getDeltaMovement().y > 1.0) {
            data.wasLaunchedByWindCharge = true;
            data.launchStartY = self.getY();
        }

        if (data.wasLaunchedByWindCharge && self.fallDistance > 0) {
            double gained = self.getY() - data.launchStartY;

            if (gained >= 7.0) {
                ModCriteriaTriggers.FALL_AFTER_EXPLOSION.trigger(
                        self,
                        new Vec3(0, data.launchStartY, 0),
                        null
                );
            }

            data.wasLaunchedByWindCharge = false;
        }

        return data;
    }
}
