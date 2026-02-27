package com.drd.trickytrialsbackport.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class TrialSpawnerDetectionParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private static final int BASE_LIFETIME = 8;

    protected TrialSpawnerDetectionParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xd,
            double yd,
            double zd,
            float scale,
            SpriteSet sprites
    ) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.sprites = sprites;

        this.friction = 0.96F;
        this.gravity = -0.1F;

        this.xd *= 0.0;
        this.yd *= 0.9;
        this.zd *= 0.0;

        this.xd += xd;
        this.yd += yd;
        this.zd += zd;

        this.quadSize *= 0.75F * scale;

        this.lifetime = (int)(BASE_LIFETIME / Mth.randomBetween(this.random, 0.5F, 1.0F) * scale);
        this.lifetime = Math.max(this.lifetime, 1);

        this.setSpriteFromAge(sprites);
        this.hasPhysics = true;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTicks) {
        return 240;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return this.quadSize *
                Mth.clamp(((float)this.age + partialTicks) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
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
                double x,
                double y,
                double z,
                double xd,
                double yd,
                double zd
        ) {
            return new TrialSpawnerDetectionParticle(level, x, y, z, xd, yd, zd, 1.5F, this.sprites);
        }
    }
}
