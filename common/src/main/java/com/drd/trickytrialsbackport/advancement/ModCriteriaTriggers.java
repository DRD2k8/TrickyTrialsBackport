package com.drd.trickytrialsbackport.advancement;

import com.drd.trickytrialsbackport.advancement.critereon.CrafterRecipeCraftedTrigger;
import com.drd.trickytrialsbackport.advancement.critereon.FallAfterExplosionTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class ModCriteriaTriggers {
    public static CrafterRecipeCraftedTrigger CRAFTER_RECIPE_CRAFTED;
    public static FallAfterExplosionTrigger FALL_AFTER_EXPLOSION;

    public static void register() {
        CRAFTER_RECIPE_CRAFTED = CriteriaTriggers.register(new CrafterRecipeCraftedTrigger());
        FALL_AFTER_EXPLOSION = CriteriaTriggers.register(new FallAfterExplosionTrigger());
    }
}
