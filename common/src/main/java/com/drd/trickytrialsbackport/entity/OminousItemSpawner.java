package com.drd.trickytrialsbackport.entity;

import com.drd.trickytrialsbackport.registry.ModEntities;
import com.drd.trickytrialsbackport.registry.ModParticles;
import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

public class OminousItemSpawner extends Entity {
    private static final int SPAWN_ITEM_DELAY_MIN = 60;
    private static final int SPAWN_ITEM_DELAY_MAX = 120;

    private static final String TAG_SPAWN_ITEM_AFTER_TICKS = "spawn_item_after_ticks";
    private static final String TAG_ITEM = "item";

    private static final EntityDataAccessor<ItemStack> DATA_ITEM =
            SynchedEntityData.defineId(OminousItemSpawner.class, EntityDataSerializers.ITEM_STACK);

    public static final int TICKS_BEFORE_ABOUT_TO_SPAWN_SOUND = 36;

    private long spawnItemAfterTicks;

    public OminousItemSpawner(EntityType<? extends OminousItemSpawner> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static OminousItemSpawner create(Level level, ItemStack stack) {
        OminousItemSpawner spawner = new OminousItemSpawner(ModEntities.OMINOUS_ITEM_SPAWNER.get(), level);

        int delay = SPAWN_ITEM_DELAY_MIN + level.random.nextInt(SPAWN_ITEM_DELAY_MAX - SPAWN_ITEM_DELAY_MIN + 1);
        spawner.spawnItemAfterTicks = delay;

        spawner.setItem(stack);
        return spawner;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            tickClient();
        } else {
            tickServer();
        }
    }

    private void tickServer() {
        if (this.tickCount == this.spawnItemAfterTicks - TICKS_BEFORE_ABOUT_TO_SPAWN_SOUND) {
            this.level().playSound(null, this.blockPosition(),
                    ModSounds.TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM.get(), SoundSource.NEUTRAL);
        }

        if (this.tickCount >= this.spawnItemAfterTicks) {
            spawnItem();
            this.kill();
        }
    }

    private void tickClient() {
        if (this.level().getGameTime() % 5L == 0L) {
            addParticles();
        }
    }

    private void spawnItem() {
        Level level = this.level();
        ItemStack stack = this.getItem();

        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            Entity entity;

            if (item instanceof SnowballItem) {
                Snowball snowball = new Snowball(level, this.getControllingPassenger());
                snowball.setItem(stack);
                snowball.shoot(0, -1, 0, 1.1F, 6.0F);
                entity = snowball;
            } else if (item instanceof EggItem) {
                ThrownEgg egg = new ThrownEgg(level, this.getControllingPassenger());
                egg.setItem(stack);
                egg.shoot(0, -1, 0, 1.1F, 6.0F);
                entity = egg;
            } else if (item instanceof EnderpearlItem) {
                ThrownEnderpearl pearl = new ThrownEnderpearl(level, this.getControllingPassenger());
                pearl.setItem(stack);
                pearl.shoot(0, -1, 0, 1.1F, 6.0F);
                entity = pearl;

            } else {
                entity = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), stack);
            }

            level.addFreshEntity(entity);
            level.levelEvent(3021, this.blockPosition(), 1);
            level.gameEvent(entity, GameEvent.ENTITY_PLACE, this.position());

            this.setItem(ItemStack.EMPTY);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains(TAG_ITEM, 10)) {
            this.setItem(ItemStack.of(tag.getCompound(TAG_ITEM)));
        }

        this.spawnItemAfterTicks = tag.getLong(TAG_SPAWN_ITEM_AFTER_TICKS);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (!this.getItem().isEmpty()) {
            tag.put(TAG_ITEM, this.getItem().save(new CompoundTag()));
        }

        tag.putLong(TAG_SPAWN_ITEM_AFTER_TICKS, this.spawnItemAfterTicks);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
    }

    @Override
    protected boolean couldAcceptPassenger() {
        return false;
    }

    @Override
    protected void addPassenger(Entity passenger) {
        throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    public void addParticles() {
        Vec3 origin = this.position();
        int count = 1 + this.random.nextInt(3);

        for (int i = 0; i < count; i++) {
            double dx = 0.4 * (this.random.nextGaussian() - this.random.nextGaussian());
            double dy = 0.4 * (this.random.nextGaussian() - this.random.nextGaussian());
            double dz = 0.4 * (this.random.nextGaussian() - this.random.nextGaussian());

            Vec3 target = new Vec3(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
            Vec3 motion = target.subtract(origin);

            this.level().addParticle(
                    ModParticles.OMINOUS_SPAWNING.get(),
                    origin.x, origin.y, origin.z,
                    motion.x, motion.y, motion.z
            );
        }
    }

    public ItemStack getItem() {
        return this.entityData.get(DATA_ITEM);
    }

    private void setItem(ItemStack stack) {
        this.entityData.set(DATA_ITEM, stack);
    }
}
