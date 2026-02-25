package com.drd.trickytrialsbackport.entity.ai.behavior;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class LongJumpUtil {
    public LongJumpUtil() {
    }

    public static Optional<Vec3> calculateJumpVectorForAngle(Mob p_312589_, Vec3 p_311721_, float p_310433_, int p_310545_, boolean p_310611_) {
        Vec3 $$5 = p_312589_.position();
        Vec3 $$6 = (new Vec3(p_311721_.x - $$5.x, 0.0, p_311721_.z - $$5.z)).normalize().scale(0.5);
        Vec3 $$7 = p_311721_.subtract($$6);
        Vec3 $$8 = $$7.subtract($$5);
        float $$9 = (float)p_310545_ * 3.1415927F / 180.0F;
        double $$10 = Math.atan2($$8.z, $$8.x);
        double $$11 = $$8.subtract(0.0, $$8.y, 0.0).lengthSqr();
        double $$12 = Math.sqrt($$11);
        double $$13 = $$8.y;
        double $$14 = 0.08;
        double $$15 = Math.sin((double)(2.0F * $$9));
        double $$16 = Math.pow(Math.cos((double)$$9), 2.0);
        double $$17 = Math.sin((double)$$9);
        double $$18 = Math.cos((double)$$9);
        double $$19 = Math.sin($$10);
        double $$20 = Math.cos($$10);
        double $$21 = $$11 * 0.08 / ($$12 * $$15 - 2.0 * $$13 * $$16);
        if ($$21 < 0.0) {
            return Optional.empty();
        } else {
            double $$22 = Math.sqrt($$21);
            if ($$22 > (double)p_310433_) {
                return Optional.empty();
            } else {
                double $$23 = $$22 * $$18;
                double $$24 = $$22 * $$17;
                if (p_310611_) {
                    int $$25 = Mth.ceil($$12 / $$23) * 2;
                    double $$26 = 0.0;
                    Vec3 $$27 = null;
                    EntityDimensions $$28 = p_312589_.getDimensions(Pose.LONG_JUMPING);

                    for(int $$29 = 0; $$29 < $$25 - 1; ++$$29) {
                        $$26 += $$12 / (double)$$25;
                        double $$30 = $$17 / $$18 * $$26 - Math.pow($$26, 2.0) * 0.08 / (2.0 * $$21 * Math.pow($$18, 2.0));
                        double $$31 = $$26 * $$20;
                        double $$32 = $$26 * $$19;
                        Vec3 $$33 = new Vec3($$5.x + $$31, $$5.y + $$30, $$5.z + $$32);
                        if ($$27 != null && !isClearTransition(p_312589_, $$28, $$27, $$33)) {
                            return Optional.empty();
                        }

                        $$27 = $$33;
                    }
                }

                return Optional.of((new Vec3($$23 * $$20, $$24, $$23 * $$19)).scale(0.949999988079071));
            }
        }
    }

    private static boolean isClearTransition(Mob p_310914_, EntityDimensions p_310152_, Vec3 p_313099_, Vec3 p_311144_) {
        Vec3 $$4 = p_311144_.subtract(p_313099_);
        double $$5 = (double)Math.min(p_310152_.width, p_310152_.height);
        int $$6 = Mth.ceil($$4.length() / $$5);
        Vec3 $$7 = $$4.normalize();
        Vec3 $$8 = p_313099_;

        for(int $$9 = 0; $$9 < $$6; ++$$9) {
            $$8 = $$9 == $$6 - 1 ? p_311144_ : $$8.add($$7.scale($$5 * 0.8999999761581421));
            if (!p_310914_.level().noCollision(p_310914_, p_310152_.makeBoundingBox($$8))) {
                return false;
            }
        }

        return true;
    }
}
