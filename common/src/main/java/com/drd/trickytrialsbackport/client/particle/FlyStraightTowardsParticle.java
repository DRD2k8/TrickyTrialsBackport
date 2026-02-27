package com.drd.trickytrialsbackport.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class FlyStraightTowardsParticle extends TextureSheetParticle {
    private final double xStart;
    private final double yStart;
    private final double zStart;
    private final int startColor;
    private final int endColor;

    FlyStraightTowardsParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xd,
            double yd,
            double zd,
            int startColor,
            int endColor
    ) {
        super(level, x, y, z);

        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.xStart = x;
        this.yStart = y;
        this.zStart = z;

        this.xo = x + xd;
        this.yo = y + yd;
        this.zo = z + zd;

        this.x = this.xo;
        this.y = this.yo;
        this.z = this.zo;

        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
        this.hasPhysics = false;

        this.lifetime = (int)(Math.random() * 5.0) + 25;

        this.startColor = startColor;
        this.endColor = endColor;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double dx, double dy, double dz) {
    }

    @Override
    public int getLightColor(float partialTicks) {
        return 240;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float f = (float)this.age / (float)this.lifetime;
        float f1 = 1.0F - f;

        this.x = this.xStart + this.xd * f1;
        this.y = this.yStart + this.yd * f1;
        this.z = this.zStart + this.zd * f1;

        int color = FastColor.ARGB32.lerp(f, this.startColor, this.endColor);

        this.setColor(
                FastColor.ARGB32.red(color) / 255.0F,
                FastColor.ARGB32.green(color) / 255.0F,
                FastColor.ARGB32.blue(color) / 255.0F
        );

        this.setAlpha(FastColor.ARGB32.alpha(color) / 255.0F);
    }

    public static class OminousSpawnProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public OminousSpawnProvider(SpriteSet sprites) {
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
            FlyStraightTowardsParticle p = new FlyStraightTowardsParticle(
                    level, x, y, z, xd, yd, zd,
                    -12210434,  // start color
                    -1          // end color
            );

            p.scale(Mth.randomBetween(level.getRandom(), 3.0F, 5.0F));
            p.pickSprite(this.sprites);

            return p;
        }
    }
}
