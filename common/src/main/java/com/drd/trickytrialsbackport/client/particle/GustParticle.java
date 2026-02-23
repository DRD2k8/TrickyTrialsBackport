package com.drd.trickytrialsbackport.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class GustParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected GustParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;

        this.setSpriteFromAge(sprites);

        this.lifetime = 12 + this.random.nextInt(4);

        this.quadSize = 1.0F;
        this.setSize(1.0F, 1.0F);

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getLightColor(float partialTicks) {
        return 15728880;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x, double y, double z,
                double vx, double vy, double vz
        ) {
            return new GustParticle(level, x, y, z, this.sprites);
        }
    }

    public static class SmallProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public SmallProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x, double y, double z,
                double vx, double vy, double vz
        ) {
            Particle particle = new GustParticle(level, x, y, z, this.sprites);
            particle.scale(0.15F);
            return particle;
        }
    }
}
