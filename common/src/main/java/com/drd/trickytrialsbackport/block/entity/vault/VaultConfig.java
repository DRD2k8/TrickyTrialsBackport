package com.drd.trickytrialsbackport.block.entity.vault;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class VaultConfig {
    public ResourceLocation lootTable = null;
    public ResourceLocation overrideLootTableToDisplay = null;
    public double activationRange = 16.0D;
    public double deactivationRange = 24.0D;
    public ItemStack keyItem = ItemStack.EMPTY;

    public void load(CompoundTag tag) {
        if (tag.contains("loot_table", Tag.TAG_STRING)) {
            lootTable = new ResourceLocation(tag.getString("loot_table"));
        } else {
            lootTable = null;
        }

        if (tag.contains("override_loot_table_to_display", Tag.TAG_STRING)) {
            overrideLootTableToDisplay = new ResourceLocation(tag.getString("override_loot_table_to_display"));
        } else {
            overrideLootTableToDisplay = null;
        }

        if (tag.contains("activation_range", Tag.TAG_DOUBLE)) {
            activationRange = tag.getDouble("activation_range");
        }

        if (tag.contains("deactivation_range", Tag.TAG_DOUBLE)) {
            deactivationRange = tag.getDouble("deactivation_range");
        }

        if (tag.contains("key_item", Tag.TAG_COMPOUND)) {
            keyItem = ItemStack.of(tag.getCompound("key_item"));
        } else {
            keyItem = ItemStack.EMPTY;
        }
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        if (lootTable != null) {
            tag.putString("loot_table", lootTable.toString());
        }

        if (overrideLootTableToDisplay != null) {
            tag.putString("override_loot_table_to_display", overrideLootTableToDisplay.toString());
        }

        tag.putDouble("activation_range", activationRange);
        tag.putDouble("deactivation_range", deactivationRange);

        if (!keyItem.isEmpty()) {
            tag.put("key_item", keyItem.save(new CompoundTag()));
        }

        return tag;
    }

    public double activationRange() {
        return activationRange;
    }

    public double deactivationRange() {
        return deactivationRange;
    }
}
