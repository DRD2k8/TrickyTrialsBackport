package com.drd.trickytrialsbackport.entity.projectile.windcharge;

import com.drd.trickytrialsbackport.entity.monster.breeze.Breeze;
import com.drd.trickytrialsbackport.registry.ModEntities;
import com.drd.trickytrialsbackport.registry.ModParticles;
import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class BreezeWindCharge extends ThrowableProjectile {
    private static final float EXPLOSION_RADIUS = 1.2F;
    private static final int NODEFLECT_TICKS = 5;

    private int noDeflectTicks = NODEFLECT_TICKS;
    private int noFallTicks = 0;
    private boolean appliedRecoil = false;
    private boolean exploded = false;

    public BreezeWindCharge(EntityType<? extends BreezeWindCharge> type, Level level) {
        super(type, level);
    }

    public BreezeWindCharge(Level level, Breeze owner) {
        super(ModEntities.BREEZE_WIND_CHARGE.get(), owner, level);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();

        if (noDeflectTicks > 0) {
            noDeflectTicks--;
        }

        if (!appliedRecoil) {
            applyRecoil();
            appliedRecoil = true;
        }

        if (noFallTicks > 0 && getOwner() instanceof LivingEntity living) {
            noFallTicks--;
            living.fallDistance = 0.0F;
        }

        if (exploded && getOwner() instanceof LivingEntity living) {
            living.fallDistance = 0.0F;

            if (living.onGround() || living.isInWater() || living.isInLava()) {
                discard();
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hit = result.getEntity();
        Entity owner = getOwner();

        if (owner != null && hit == owner) return;

        if (!(owner instanceof Breeze)) {
            discard();
            return;
        }

        if (hit instanceof LivingEntity living) {
            living.hurt(damageSources().mobProjectile(this, (LivingEntity) owner), 4.0F);
        }

        explode();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        explode();
    }

    private void explode() {
        if (!level().isClientSide) {
            ServerLevel server = (ServerLevel) level();

            server.sendParticles(
                    ModParticles.GUST_EMITTER_SMALL.get(),
                    getX(), getY(), getZ(),
                    1, 0, 0, 0, 0
            );

            if (random.nextFloat() < 0.25F) {
                server.sendParticles(
                        ModParticles.GUST_EMITTER_LARGE.get(),
                        getX(), getY(), getZ(),
                        1, 0, 0, 0, 0
                );
            }

            server.playSound(
                    null,
                    getX(), getY(), getZ(),
                    ModSounds.BREEZE_WIND_CHARGE_BURST.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );

            exploded = true;
            setDeltaMovement(Vec3.ZERO);
            setNoGravity(true);
        }
    }

    private void applyRecoil() {
        Entity owner = getOwner();
        if (!(owner instanceof LivingEntity living)) return;

        Vec3 look = living.getLookAngle();

        double backward = 0.5;
        double upward = 0.9;

        if (this.random.nextFloat() < 0.05F) {
            upward = 1.6;
        }

        living.push(-look.x * backward, 0, -look.z * backward);

        Vec3 motion = living.getDeltaMovement();
        if (motion.y < upward) {
            living.setDeltaMovement(motion.x, upward, motion.z);
        }

        this.noFallTicks = 32;
        living.fallDistance = 0.0F;
        living.hurtMarked = true;
    }

    @Override
    protected float getGravity() {
        return 0.01F;
    }
}
