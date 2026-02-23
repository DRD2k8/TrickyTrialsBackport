package com.drd.trickytrialsbackport.item;

import com.drd.trickytrialsbackport.entity.projectile.windcharge.WindCharge;
import com.drd.trickytrialsbackport.registry.ModEntities;
import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WindChargeItem extends Item {
    public static final float SHOOT_POWER = 1.5F;

    public WindChargeItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(this, 10);

        if (!level.isClientSide) {
            WindCharge projectile = new WindCharge(ModEntities.WIND_CHARGE.get(), level);
            projectile.setOwner(player);
            projectile.setPos(player.getX(), player.getEyeY(), player.getZ());
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, SHOOT_POWER, 1.0F);
            level.addFreshEntity(projectile);
        }

        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                ModSounds.WIND_CHARGE_THROW.get(),
                SoundSource.PLAYERS,
                0.5F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        player.awardStat(Stats.ITEM_USED.get(this));
        stack.shrink(1);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
