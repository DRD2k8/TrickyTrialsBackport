package com.drd.trickytrialsbackport.entity.monster.breeze;

import com.drd.trickytrialsbackport.registry.ModSounds;
import com.drd.trickytrialsbackport.util.ModTags;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class Breeze extends Monster {
    public static final int STATE_IDLE = 0;
    public static final int STATE_INHALING = 1;
    public static final int STATE_SHOOTING = 2;
    public static final int STATE_SLIDING = 3;
    public static final int STATE_LONG_JUMPING = 4;
    public static final int STATE_EMERGING = 5;

    public static final EntityDataAccessor<Integer> BREEZE_STATE =
            SynchedEntityData.defineId(Breeze.class, EntityDataSerializers.INT);

    public AnimationState idle = new AnimationState();
    public AnimationState slide = new AnimationState();
    public AnimationState longJump = new AnimationState();
    public AnimationState shoot = new AnimationState();
    public AnimationState inhale = new AnimationState();

    private int jumpTrailStartedTick = 0;

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.6)
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    public Breeze(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.TRAPDOOR, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.moveControl = new MoveControl(this);
        this.navigation = new FlyingPathNavigation(this, level);
        BreezeAi.makeBrain(this.getBrain());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BREEZE_STATE, STATE_IDLE);
    }

    public int getBreezeState() {
        return this.entityData.get(BREEZE_STATE);
    }

    public void setBreezeState(int state) {
        this.entityData.set(BREEZE_STATE, state);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return BreezeAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<Breeze> getBrain() {
        return (Brain<Breeze>) super.getBrain();
    }

    @Override
    protected Brain.Provider<Breeze> brainProvider() {
        return Brain.provider(BreezeAi.MEMORY_TYPES, BreezeAi.SENSOR_TYPES);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("breezeBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (this.level().isClientSide() && key.equals(BREEZE_STATE)) {
            this.resetAnimations();
            int state = this.getBreezeState();

            switch (state) {
                case STATE_SHOOTING -> this.shoot.startIfStopped(this.tickCount);
                case STATE_INHALING -> this.inhale.startIfStopped(this.tickCount);
                case STATE_SLIDING -> this.slide.startIfStopped(this.tickCount);
                case STATE_LONG_JUMPING -> this.longJump.startIfStopped(this.tickCount);
                default -> this.idle.startIfStopped(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(key);
    }

    private void resetAnimations() {
        this.shoot.stop();
        this.idle.stop();
        this.inhale.stop();
        this.longJump.stop();
        this.slide.stop();
    }

    @Override
    public void tick() {
        switch (this.getBreezeState()) {
            case STATE_SHOOTING, STATE_INHALING, STATE_IDLE ->
                    this.resetJumpTrail().emitGroundParticles(1 + this.getRandom().nextInt(1));

            case STATE_SLIDING ->
                    this.emitGroundParticles(20);

            case STATE_LONG_JUMPING ->
                    this.emitJumpTrailParticles();
        }

        super.tick();
    }

    public Breeze resetJumpTrail() {
        this.jumpTrailStartedTick = 0;
        return this;
    }

    public void emitJumpTrailParticles() {
        if (++this.jumpTrailStartedTick <= 5) {
            BlockState block = this.level().getBlockState(this.blockPosition().below());
            Vec3 motion = this.getDeltaMovement();
            Vec3 pos = this.position().add(motion).add(0.0, 0.1, 0.0);

            for (int i = 0; i < 3; ++i) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block),
                        pos.x, pos.y, pos.z, 0, 0, 0);
            }
        }
    }

    public void emitGroundParticles(int amount) {
        Vec3 center = this.getBoundingBox().getCenter();
        Vec3 pos = new Vec3(center.x, this.position().y, center.z);
        BlockState block = this.level().getBlockState(this.blockPosition().below());

        if (block.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < amount; ++i) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block),
                        pos.x, pos.y, pos.z, 0, 0, 0);
            }
        }
    }

    public boolean withinInnerCircleRange(Vec3 targetPos) {
        return this.position().distanceToSqr(targetPos) <= (4.0F * 4.0F);
    }

    public boolean withinMiddleCircleRange(Vec3 targetPos) {
        double dist = this.position().distanceToSqr(targetPos);
        return dist > (4.0F * 4.0F) && dist <= (8.0F * 8.0F);
    }

    public boolean withinOuterCircleRange(Vec3 targetPos) {
        double dist = this.position().distanceToSqr(targetPos);
        return dist > (8.0F * 8.0F) && dist <= (20.0F * 20.0F);
    }

    @Override
    public void playAmbientSound() {
        this.level().playLocalSound(
                this.getX(), this.getY(), this.getZ(),
                this.getAmbientSound(),
                this.getSoundSource(),
                1.0F, 1.0F,
                false
        );
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BREEZE_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BREEZE_HURT.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.onGround()
                ? ModSounds.BREEZE_IDLE_GROUND.get()
                : ModSounds.BREEZE_IDLE_AIR.get();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(ModTags.DamageTypes.BREEZE_IMMUNE_TO)
                || source.getEntity() instanceof Breeze
                || super.isInvulnerableTo(source);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        if (distance > 3.0F) {
            this.playSound(ModSounds.BREEZE_LAND.get(), 1.0F, 1.0F);
        }
        return super.causeFallDamage(distance, damageMultiplier, source);
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }
}
