package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.enchantment.BreachEnchantment;
import com.drd.trickytrialsbackport.enchantment.DensityEnchantment;
import com.drd.trickytrialsbackport.enchantment.WindBurstEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;

public class ModEnchantments {
    public static Supplier<Enchantment> BREACH;
    public static Supplier<Enchantment> DENSITY;
    public static Supplier<Enchantment> WIND_BURST;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        BREACH = helper.registerAuto(Registries.ENCHANTMENT, "breach", () -> new BreachEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}));
        DENSITY = helper.registerAuto(Registries.ENCHANTMENT, "density", () -> new DensityEnchantment(Enchantment.Rarity.COMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        WIND_BURST = helper.registerAuto(Registries.ENCHANTMENT, "wind_burst", () -> new WindBurstEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
    }
}
