package com.drd.trickytrialsbackport.entity.projectile.windcharge;

import com.drd.trickytrialsbackport.registry.ModEntities;
import com.drd.trickytrialsbackport.registry.ModParticles;
import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class WindCharge extends AbstractWindCharge {
    private static final float RADIUS = 1.2F;
    private static final WindChargePlayerDamageCalculator EXPLOSION_DAMAGE_CALCULATOR =
            new WindChargePlayerDamageCalculator();

    public WindCharge(EntityType<? extends AbstractWindCharge> type, Level level) {
        super(type, level);
    }

    public WindCharge(Player owner, Level level, double x, double y, double z) {
        super(ModEntities.WIND_CHARGE.get(), level, owner, x, y, z);
    }

    public WindCharge(Level level, double x, double y, double z,
                      double xPower, double yPower, double zPower) {
        super(ModEntities.WIND_CHARGE.get(), x, y, z, xPower, yPower, zPower, level);
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);

        if (!this.level().isClientSide) {
            this.explode();
            this.discard();
        }

        if (this.level().isClientSide) {
            this.explode();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);

        if (!this.level().isClientSide) {
            this.explode();
            this.discard();
        }

        if (this.level().isClientSide) {
            this.explode();
        }
    }


    @Override
    protected void explode() {
        Level level = this.level();

        if (!level.isClientSide) {
            level.explode(
                    this,
                    null,
                    EXPLOSION_DAMAGE_CALCULATOR,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    RADIUS,
                    false,
                    Level.ExplosionInteraction.MOB
            );
        }

        if (level.isClientSide) {
            for (int i = 0; i < 20; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 0.5;
                double dy = (level.random.nextDouble() - 0.5) * 0.5;
                double dz = (level.random.nextDouble() - 0.5) * 0.5;

                level.addParticle(
                        ModParticles.GUST_EMITTER_SMALL.get(),
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        dx, dy, dz
                );
            }

            for (int i = 0; i < 10; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 0.8;
                double dy = (level.random.nextDouble() - 0.5) * 0.8;
                double dz = (level.random.nextDouble() - 0.5) * 0.8;

                level.addParticle(
                        ModParticles.GUST_EMITTER_LARGE.get(),
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        dx, dy, dz
                );
            }

            level.playLocalSound(
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    ModSounds.WIND_CHARGE_BURST.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F,
                    false
            );
        }

        this.discard();
    }

    public static final class WindChargePlayerDamageCalculator extends AbstractWindCharge.WindChargeDamageCalculator {
        @Override
        public float getKnockbackMultiplier(Entity entity) {
            return 1.1F;
        }
    }
}
