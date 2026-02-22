package com.drd.trickytrialsbackport.entity;

import com.drd.trickytrialsbackport.registry.ModEntities;
import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class WindCharge extends ThrowableProjectile {
    private static final float EXPLOSION_RADIUS = 1.2F;
    private static final int NODEFLECT_TICKS = 5;

    private int noDeflectTicks = NODEFLECT_TICKS;

    public WindCharge(EntityType<? extends WindCharge> type, Level level) {
        super(type, level);
    }

    public WindCharge(Level level, LivingEntity owner) {
        super(ModEntities.WIND_CHARGE.get(), owner, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        if (noDeflectTicks > 0) {
            noDeflectTicks--;
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        explode();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        explode();
    }

    private void explode() {
        if (!level().isClientSide) {

            level().explode(
                    this,
                    getX(),
                    getY(),
                    getZ(),
                    EXPLOSION_RADIUS,
                    Level.ExplosionInteraction.NONE
            );

            level().playSound(
                    null,
                    getX(),
                    getY(),
                    getZ(),
                    ModSounds.WIND_CHARGE_BURST.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );

            discard();
        }
    }

    @Override
    protected float getGravity() {
        return 0.01F;
    }
}
