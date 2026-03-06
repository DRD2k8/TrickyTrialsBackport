package com.drd.trickytrialsbackport.enchantment;

import com.drd.trickytrialsbackport.registry.ModItems;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BreachEnchantment extends Enchantment {
    public BreachEnchantment(Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot[] equipmentSlots) {
        super(rarity, enchantmentCategory, equipmentSlots);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.is(ModItems.MACE.get());
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    public static float calculateArmorBreach(int level, float toughnessEffectiveness) {
        return Mth.clamp(toughnessEffectiveness - 0.15F * level, 0.0F, 1.0F);
    }
}
