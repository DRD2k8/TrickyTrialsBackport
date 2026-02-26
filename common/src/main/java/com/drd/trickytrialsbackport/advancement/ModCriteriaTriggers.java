package com.drd.trickytrialsbackport.advancement;

import com.drd.trickytrialsbackport.advancement.critereon.CrafterRecipeCraftedTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class ModCriteriaTriggers {
    public static CrafterRecipeCraftedTrigger CRAFTER_RECIPE_CRAFTED;

    public static void register() {
        CRAFTER_RECIPE_CRAFTED = CriteriaTriggers.register(new CrafterRecipeCraftedTrigger());
    }
}
