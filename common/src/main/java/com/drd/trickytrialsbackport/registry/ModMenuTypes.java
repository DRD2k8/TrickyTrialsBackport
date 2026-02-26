package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.gui.CrafterMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static Supplier<MenuType<CrafterMenu>> CRAFTER_3x3;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();
        CRAFTER_3x3 = helper.registerAuto(Registries.MENU, "crafter_3x3", () -> new MenuType<>((id, inv) -> new CrafterMenu(id, inv), FeatureFlags.VANILLA_SET));
    }
}
