package com.drd.trickytrialsbackport.enchantment;

import com.drd.trickytrialsbackport.registry.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DensityEnchantment extends Enchantment {
    public DensityEnchantment(Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot[] equipmentSlots) {
        super(rarity, enchantmentCategory, equipmentSlots);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.is(ModItems.MACE.get());
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    public static float calculateDamageAddition(int i, float f) {
        return f * (float) i;
    }
}
