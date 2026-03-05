package com.drd.trickytrialsbackport.item;

import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OminousBottleItem extends Item {
    private static final int DRINK_DURATION = 32;
    public static final int EFFECT_DURATION = 120000;
    public static final int MIN_AMPLIFIER = 0;
    public static final int MAX_AMPLIFIER = 4;

    public static final String AMPLIFIER_TAG = "OminousAmplifier";

    public OminousBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!level.isClientSide) {
            level.playSound(null, entity.blockPosition(), ModSounds.OMINOUS_BOTTLE_DISPOSE.get(), entity.getSoundSource(), 1.0F, 1.0F);

            int amplifier = stack.getOrCreateTag().getInt(AMPLIFIER_TAG);

            entity.removeEffect(MobEffects.BAD_OMEN);
            entity.addEffect(new MobEffectInstance(
                    MobEffects.BAD_OMEN,
                    EFFECT_DURATION,
                    amplifier,
                    false,
                    false,
                    true
            ));
        }

        if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return DRINK_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int amplifier = stack.getOrCreateTag().getInt(AMPLIFIER_TAG);

        MobEffectInstance effect = new MobEffectInstance(
                MobEffects.BAD_OMEN,
                EFFECT_DURATION,
                amplifier,
                false,
                false,
                true
        );

        PotionUtils.addPotionTooltip(List.of(effect), tooltip, 1.0F);
    }
}
