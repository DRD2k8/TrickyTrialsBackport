package com.drd.trickytrialsbackport.client.particle;

import com.drd.trickytrialsbackport.registry.ModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;

public class GustSeedParticle extends NoRenderParticle {
    private final double scale;
    private final int tickDelayInBetween;

    protected GustSeedParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double scale,
            int lifetime,
            int tickDelayInBetween
    ) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.scale = scale;
        this.lifetime = lifetime;
        this.tickDelayInBetween = tickDelayInBetween;
    }

    @Override
    public void tick() {
        if (this.age % (this.tickDelayInBetween + 1) == 0) {
            for (int i = 0; i < 3; i++) {
                double px = this.x + (this.random.nextDouble() - this.random.nextDouble()) * this.scale;
                double py = this.y + (this.random.nextDouble() - this.random.nextDouble()) * this.scale;
                double pz = this.z + (this.random.nextDouble() - this.random.nextDouble()) * this.scale;

                this.level.addParticle(
                        ModParticles.GUST.get(),
                        px, py, pz,
                        (double) this.age / (double) this.lifetime,
                        0.0,
                        0.0
                );
            }
        }

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final double scale;
        private final int lifetime;
        private final int tickDelayInBetween;

        public Provider(double scale, int lifetime, int tickDelayInBetween) {
            this.scale = scale;
            this.lifetime = lifetime;
            this.tickDelayInBetween = tickDelayInBetween;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double dx,
                double dy,
                double dz
        ) {
            return new GustSeedParticle(
                    level,
                    x, y, z,
                    this.scale,
                    this.lifetime,
                    this.tickDelayInBetween
            );
        }
    }
}
