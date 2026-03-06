package com.drd.trickytrialsbackport.enchantment;

import com.drd.trickytrialsbackport.registry.ModItems;
import com.drd.trickytrialsbackport.registry.ModParticles;
import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WindBurstEnchantment extends Enchantment {
    public WindBurstEnchantment(Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot[] equipmentSlots) {
        super(rarity, enchantmentCategory, equipmentSlots);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.is(ModItems.MACE.get());
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        if (!(attacker.level() instanceof ServerLevel server)) return;

        float knockbackPower = 0.25F + 0.25F * level;

        Explosion explosion = server.explode(
                null,
                null,
                new NoBlockDamageCalculator(),
                attacker.getX(),
                attacker.getY(),
                attacker.getZ(),
                3.5F,
                false,
                Level.ExplosionInteraction.MOB
        );

        explosion.clearToBlow();

        applyWindBurstKnockback(server, attacker, knockbackPower);

        server.sendParticles(ModParticles.GUST_EMITTER_SMALL.get(), attacker.getX(), attacker.getY(), attacker.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
        server.sendParticles(ModParticles.GUST_EMITTER_LARGE.get(), attacker.getX(), attacker.getY(), attacker.getZ(), 5, 0.5, 0.5, 0.5, 0.1);
        server.playSound(null, attacker.blockPosition(), ModSounds.BREEZE_WIND_CHARGE_BURST.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private void applyWindBurstKnockback(ServerLevel level, LivingEntity attacker, float power) {
        double radius = 3.5;
        AABB box = new AABB(
                attacker.getX() - radius, attacker.getY() - radius, attacker.getZ() - radius,
                attacker.getX() + radius, attacker.getY() + radius, attacker.getZ() + radius
        );

        for (Entity e : level.getEntities(attacker, box)) {
            if (e instanceof LivingEntity living && e != attacker) {
                Vec3 diff = living.position().subtract(attacker.position());
                double dist = diff.length();

                if (dist > 0.001) {
                    Vec3 push = diff.normalize().scale(power);
                    living.push(push.x, 0.5F, push.z);
                }
            }
        }
    }

    public static class NoBlockDamageCalculator extends ExplosionDamageCalculator {
        @Override
        public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float power) {
            return false;
        }
    }
}
