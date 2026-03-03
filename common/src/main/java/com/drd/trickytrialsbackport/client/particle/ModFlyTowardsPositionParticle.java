package com.drd.trickytrialsbackport.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class ModFlyTowardsPositionParticle extends TextureSheetParticle {
    private final double xStart;
    private final double yStart;
    private final double zStart;
    private final boolean isGlowing;

    private final float startAlpha;
    private final float endAlpha;
    private final float fadeInFrac;
    private final float fadeOutFrac;

    ModFlyTowardsPositionParticle(
            ClientLevel level,
            double x, double y, double z,
            double xd, double yd, double zd
    ) {
        this(level, x, y, z, xd, yd, zd, false, 1.0F, 1.0F, 0.0F, 0.0F);
    }

    ModFlyTowardsPositionParticle(
            ClientLevel level,
            double x, double y, double z,
            double xd, double yd, double zd,
            boolean glowing,
            float startAlpha,
            float endAlpha,
            float fadeInFrac,
            float fadeOutFrac
    ) {
        super(level, x, y, z);
        this.isGlowing = glowing;
        this.startAlpha = startAlpha;
        this.endAlpha = endAlpha;
        this.fadeInFrac = fadeInFrac;
        this.fadeOutFrac = fadeOutFrac;

        this.setAlpha(startAlpha);

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
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = 0.9F * f;
        this.gCol = 0.9F * f;
        this.bCol = f;
        this.hasPhysics = false;
        this.lifetime = (int)(Math.random() * 10.0) + 30;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return this.isGlowing ? ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
                : ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
        this.setLocationFromBoundingbox();
    }

    @Override
    public int getLightColor(float partialTicks) {
        if (this.isGlowing) {
            return 240;
        } else {
            int i = super.getLightColor(partialTicks);
            float f = (float)this.age / (float)this.lifetime;
            f *= f;
            f *= f;
            int j = i & 0xFF;
            int k = i >> 16 & 0xFF;
            k += (int)(f * 15.0F * 16.0F);
            if (k > 240) {
                k = 240;
            }

            return j | k << 16;
        }
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float f = (float)this.age / (float)this.lifetime;
            f = 1.0F - f;
            float f1 = 1.0F - f;
            f1 *= f1;
            f1 *= f1;
            this.x = this.xStart + this.xd * (double)f;
            this.y = this.yStart + this.yd * (double)f - (double)(f1 * 1.2F);
            this.z = this.zStart + this.zd * (double)f;
            this.setPos(this.x, this.y, this.z); // update bounding box
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        this.setAlpha(computeAlpha(partialTicks));
        super.render(buffer, camera, partialTicks);
    }

    private float computeAlpha(float partialTicks) {
        if (this.lifetime <= 0) return endAlpha;

        float t = ((float)this.age + partialTicks) / (float)this.lifetime;
        t = Mth.clamp(t, 0.0F, 1.0F);

        if (fadeInFrac > 0.0F && t < fadeInFrac) {
            float k = t / fadeInFrac;
            return Mth.lerp(k, startAlpha, endAlpha);
        }

        if (fadeOutFrac > 0.0F && t > 1.0F - fadeOutFrac) {
            float k = (t - (1.0F - fadeOutFrac)) / fadeOutFrac;
            return Mth.lerp(1.0F - k, startAlpha, endAlpha);
        }

        return endAlpha;
    }

    public static class VaultConnectionProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public VaultConnectionProvider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x, double y, double z,
                double xd, double yd, double zd
        ) {
            ModFlyTowardsPositionParticle p = new ModFlyTowardsPositionParticle(
                    level,
                    x, y, z,
                    xd, yd, zd,
                    true,
                    0.0F, 0.6F,
                    0.25F, 1.0F
            );
            p.scale(1.5F);
            p.pickSprite(this.sprite);
            return p;
        }
    }
}
