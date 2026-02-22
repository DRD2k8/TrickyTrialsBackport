package com.drd.trickytrialsbackport.item;

import com.drd.trickytrialsbackport.registry.ModItems;
import com.drd.trickytrialsbackport.registry.ModSounds;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MaceItem extends Item {
    private static final int DEFAULT_ATTACK_DAMAGE = 5;
    private static final float DEFAULT_ATTACK_SPEED = -3.4F;

    public static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
    public static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0F;
    public static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 3.5F;
    public static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.7F;

    public MaceItem(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return ImmutableMultimap.of(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                            DEFAULT_ATTACK_DAMAGE, AttributeModifier.Operation.ADDITION),

                    Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                            DEFAULT_ATTACK_SPEED, AttributeModifier.Operation.ADDITION)
            );
        }

        return super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (canSmashAttack(attacker)) {
            Level level = attacker.level();

            attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(1, 0.01F, 1));

            if (attacker.onGround()) {
                if (attacker.fallDistance > SMASH_ATTACK_HEAVY_THRESHOLD) {
                    level.playSound(null, attacker.blockPosition(),
                            ModSounds.MACE_SMASH_GROUND_HEAVY.get(),
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                } else {
                    level.playSound(null, attacker.blockPosition(),
                            ModSounds.MACE_SMASH_GROUND.get(),
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            } else {
                level.playSound(null, attacker.blockPosition(),
                        ModSounds.MACE_SMASH_AIR.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            if (attacker instanceof Player player) {
                float bonus = getSmashDamage(attacker);
                target.hurt(attacker.damageSources().playerAttack(player), bonus);
            }

            knockback(level, attacker, target);

            attacker.resetFallDistance();
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    public static float getSmashDamage(LivingEntity attacker) {
        float d = attacker.fallDistance;

        if (d <= 3.0F) {
            return 4.0F * d;
        } else if (d <= 8.0F) {
            return 12.0F + 2.0F * (d - 3.0F);
        } else {
            return 22.0F + (d - 8.0F);
        }
    }

    private static void knockback(Level level, Entity attacker, Entity center) {
        List<LivingEntity> list = level.getEntitiesOfClass(
                LivingEntity.class,
                center.getBoundingBox().inflate(SMASH_ATTACK_KNOCKBACK_RADIUS),
                e -> e != attacker && e != center && !e.isSpectator()
        );

        for (LivingEntity e : list) {
            Vec3 diff = e.position().subtract(center.position());
            double dist = diff.length();

            double power = (SMASH_ATTACK_KNOCKBACK_RADIUS - dist) * SMASH_ATTACK_KNOCKBACK_POWER;
            if (power > 0) {
                Vec3 push = diff.normalize().scale(power);
                e.push(push.x, 0.7F, push.z);
            }
        }

        level.levelEvent(2013, center.blockPosition(), 750);
    }

    public static boolean canSmashAttack(LivingEntity entity) {
        return entity.fallDistance > SMASH_ATTACK_FALL_THRESHOLD && !entity.isFallFlying();
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(ModItems.BREEZE_ROD.get()) || super.isValidRepairItem(toRepair, repair);
    }
}
