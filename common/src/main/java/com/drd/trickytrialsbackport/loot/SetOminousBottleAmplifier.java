package com.drd.trickytrialsbackport.loot;

import com.drd.trickytrialsbackport.registry.ModLootFunctionTypes;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetOminousBottleAmplifier extends LootItemConditionalFunction {
    private final NumberProvider amplifier;

    public SetOminousBottleAmplifier(LootItemCondition[] conditions, NumberProvider amplifier) {
        super(conditions);
        this.amplifier = amplifier;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext ctx) {
        int amp = amplifier.getInt(ctx);
        stack.getOrCreateTag().putInt("amplifier", amp);
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLootFunctionTypes.SET_OMINOUS_BOTTLE_AMPLIFIER.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetOminousBottleAmplifier> {
        @Override
        public void serialize(JsonObject json, SetOminousBottleAmplifier value, JsonSerializationContext ctx) {
            json.add("amplifier", ctx.serialize(value.amplifier));
        }

        @Override
        public SetOminousBottleAmplifier deserialize(JsonObject json, JsonDeserializationContext ctx, LootItemCondition[] conditions) {
            NumberProvider amp = ctx.deserialize(json.get("amplifier"), NumberProvider.class);
            return new SetOminousBottleAmplifier(conditions, amp);
        }
    }
}
