package com.drd.trickytrialsbackport.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class NoParticleAbstractHurtingProjectile extends ThrowableProjectile {
    public double xPower;
    public double yPower;
    public double zPower;

    protected NoParticleAbstractHurtingProjectile(EntityType<? extends NoParticleAbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public NoParticleAbstractHurtingProjectile(EntityType<? extends NoParticleAbstractHurtingProjectile> entityType, double d, double e, double f, double g, double h, double i, Level level) {
        this(entityType, level);
        this.moveTo(d, e, f, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        double j = Math.sqrt(g * g + h * h + i * i);
        if (j != 0.0) {
            this.xPower = g / j * 0.1;
            this.yPower = h / j * 0.1;
            this.zPower = i / j * 0.1;
        }
    }

    public NoParticleAbstractHurtingProjectile(EntityType<? extends NoParticleAbstractHurtingProjectile> entityType, LivingEntity livingEntity, double d, double e, double f, Level level) {
        this(entityType, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), d, e, f, level);
        this.setOwner(livingEntity);
        this.setRot(livingEntity.getYRot(), livingEntity.getXRot());
    }

    protected void defineSynchedData() {
    }

    public boolean shouldRenderAtSqrDistance(double d) {
        double e = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(e)) {
            e = 4.0;
        }

        e *= 64.0;
        return d < e * e;
    }

    public void tick() {
        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            super.tick();
            if (this.shouldBurn()) {
                this.setSecondsOnFire(1);
            }

            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }

            this.checkInsideBlocks();
            Vec3 vec3 = this.getDeltaMovement();
            double d = this.getX() + vec3.x;
            double e = this.getY() + vec3.y;
            double f = this.getZ() + vec3.z;
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            float g = this.getInertia();
            if (this.isInWater()) {
                for(int i = 0; i < 4; ++i) {
                    float h = 0.25F;
                    this.level().addParticle(ParticleTypes.BUBBLE, d - vec3.x * 0.25, e - vec3.y * 0.25, f - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
                }

                g = 0.8F;
            }

            this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale((double)g));
            this.setPos(d, e, f);
        } else {
            this.discard();
        }
    }

    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !entity.noPhysics;
    }

    protected boolean shouldBurn() {
        return true;
    }

    protected float getInertia() {
        return 0.95F;
    }

    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put("power", this.newDoubleList(new double[]{this.xPower, this.yPower, this.zPower}));
    }

    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("power", 9)) {
            ListTag listTag = compoundTag.getList("power", 6);
            if (listTag.size() == 3) {
                this.xPower = listTag.getDouble(0);
                this.yPower = listTag.getDouble(1);
                this.zPower = listTag.getDouble(2);
            }
        }

    }

    public boolean isPickable() {
        return true;
    }

    public float getPickRadius() {
        return 1.0F;
    }

    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        } else {
            this.markHurt();
            Entity entity = damageSource.getEntity();
            if (entity != null) {
                if (!this.level().isClientSide) {
                    Vec3 vec3 = entity.getLookAngle();
                    this.setDeltaMovement(vec3);
                    this.xPower = vec3.x * 0.1;
                    this.yPower = vec3.y * 0.1;
                    this.zPower = vec3.z * 0.1;
                    this.setOwner(entity);
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        int i = entity == null ? 0 : entity.getId();
        return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(), this.getXRot(), this.getYRot(), this.getType(), i, new Vec3(this.xPower, this.yPower, this.zPower), 0.0);
    }

    public void recreateFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket) {
        super.recreateFromPacket(clientboundAddEntityPacket);
        double d = clientboundAddEntityPacket.getXa();
        double e = clientboundAddEntityPacket.getYa();
        double f = clientboundAddEntityPacket.getZa();
        double g = Math.sqrt(d * d + e * e + f * f);
        if (g != 0.0) {
            this.xPower = d / g * 0.1;
            this.yPower = e / g * 0.1;
            this.zPower = f / g * 0.1;
        }
    }
}
