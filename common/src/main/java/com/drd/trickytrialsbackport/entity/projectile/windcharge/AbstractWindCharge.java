package com.drd.trickytrialsbackport.entity.projectile.windcharge;

import com.drd.trickytrialsbackport.entity.projectile.NoParticleAbstractHurtingProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

public abstract class AbstractWindCharge extends NoParticleAbstractHurtingProjectile implements ItemSupplier {
    public static final WindChargeDamageCalculator EXPLOSION_DAMAGE_CALCULATOR =
            new WindChargeDamageCalculator();

    public AbstractWindCharge(EntityType<? extends AbstractWindCharge> type, Level level) {
        super(type, level);
    }

    public AbstractWindCharge(EntityType<? extends AbstractWindCharge> type, Level level, Entity owner, double xPower, double yPower, double zPower) {
        super(type, owner instanceof LivingEntity living ? living : null, xPower, yPower, zPower, level);
        this.setOwner(owner);
    }

    public AbstractWindCharge(EntityType<? extends AbstractWindCharge> type, double x, double y, double z, double xPower, double yPower, double zPower, Level level) {
        super(type, x, y, z, xPower, yPower, zPower, level);
    }

    @Override
    protected AABB makeBoundingBox() {
        float halfWidth = this.getType().getDimensions().width / 2.0F;
        float height = this.getType().getDimensions().height;
        float yOffset = 0.15F;

        return new AABB(
                this.getX() - halfWidth,
                this.getY() - yOffset,
                this.getZ() - halfWidth,
                this.getX() + halfWidth,
                this.getY() - yOffset + height,
                this.getZ() + halfWidth
        );
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return other instanceof AbstractWindCharge ? false : super.canCollideWith(other);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (target instanceof AbstractWindCharge) {
            return false;
        } else if (target.getType() == EntityType.END_CRYSTAL) {
            return false;
        } else {
            return super.canHitEntity(target);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!this.level().isClientSide) {
            LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
            Entity target = hitResult.getEntity();

            if (owner != null) {
                owner.setLastHurtMob(target);
            }

            target.hurt(this.damageSources().thrown(this, owner), 1.0F);

            this.explode();
        }
    }

    @Override
    public void push(double x, double y, double z) {
    }

    protected abstract void explode();

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (!this.level().isClientSide) {
            this.explode();
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && this.getBlockY() > this.level().getMaxBuildHeight() + 30) {
            this.explode();
            this.discard();
        } else {
            super.tick();
        }
    }

    public static class WindChargeDamageCalculator extends ExplosionDamageCalculator {
        @Override
        public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, FluidState fluid) {
            return Optional.empty();
        }

        public float getKnockbackMultiplier(Entity p_330296_) {
            return 1.0F;
        }
    }
}
