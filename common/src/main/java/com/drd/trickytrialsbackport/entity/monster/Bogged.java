package com.drd.trickytrialsbackport.entity.monster;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class Bogged extends ModAbstractSkeleton implements Shearable {
    public static final EntityDataAccessor<Boolean> DATA_SHEARED = SynchedEntityData.defineId(Bogged.class, EntityDataSerializers.BOOLEAN);

    public Bogged(EntityType<? extends Bogged> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ModAbstractSkeleton.createAttributes().add(Attributes.MAX_HEALTH, 16.0);
    }

    public boolean isSheared() {
        return this.entityData.get(DATA_SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.entityData.set(DATA_SHEARED, sheared);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHEARED, false);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BOGGED_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.BOGGED_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BOGGED_DEATH.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSounds.BOGGED_STEP.get();
    }

    @Override
    protected AbstractArrow getArrow(ItemStack arrowItem, float f) {
        AbstractArrow abstractarrow = super.getArrow(arrowItem, f);
        if (abstractarrow instanceof Arrow arrow) {
            arrow.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
        }

        return abstractarrow;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof ShearsItem && this.readyForShearing()) {
            this.shear(SoundSource.PLAYERS);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void shear(SoundSource source) {
        this.level().playSound(null, this, ModSounds.BOGGED_SHEAR.get(), source, 1.0F, 1.0F);
        this.setSheared(true);

        if (!this.level().isClientSide) {
            LootTable loot = this.level().getServer().getLootData()
                    .getLootTable(new ResourceLocation(TrickyTrialsBackport.NAMESPACE, "shearing/bogged"));

            LootParams params = new LootParams.Builder((ServerLevel) this.level())
                    .withParameter(LootContextParams.THIS_ENTITY, this)
                    .withParameter(LootContextParams.ORIGIN, this.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, this.level().damageSources().generic())
                    .create(LootContextParamSets.ENTITY);

            for (ItemStack stack : loot.getRandomItems(params)) {
                ItemEntity item = new ItemEntity(
                        this.level(),
                        this.getX(),
                        this.getY() + 1.0,
                        this.getZ(),
                        stack
                );
                this.level().addFreshEntity(item);
            }
        }
    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && !this.isSheared();
    }
}
