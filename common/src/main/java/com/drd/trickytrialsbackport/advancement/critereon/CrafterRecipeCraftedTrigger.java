package com.drd.trickytrialsbackport.advancement.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class CrafterRecipeCraftedTrigger extends SimpleCriterionTrigger<CrafterRecipeCraftedTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation("crafter_recipe_crafted");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
        ResourceLocation recipeId = json.has("recipe_id")
                ? new ResourceLocation(json.get("recipe_id").getAsString())
                : null;

        return new Instance(player, recipeId);
    }

    public void trigger(ServerPlayer player, ResourceLocation craftedRecipeId) {
        this.trigger(player, instance -> instance.matches(craftedRecipeId));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final ResourceLocation recipeId;

        public Instance(ContextAwarePredicate player, @Nullable ResourceLocation recipeId) {
            super(CrafterRecipeCraftedTrigger.ID, player);
            this.recipeId = recipeId;
        }

        public boolean matches(ResourceLocation crafted) {
            return recipeId == null || recipeId.equals(crafted);
        }
    }
}
