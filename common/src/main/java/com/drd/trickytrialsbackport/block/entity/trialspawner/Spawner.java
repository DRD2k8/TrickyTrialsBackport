package com.drd.trickytrialsbackport.block.entity.trialspawner;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Spawner {
    void setEntityId(EntityType<?> type, RandomSource random);

    static void appendHoverText(ItemStack stack, List<Component> tooltip, String key) {
        Component display = getSpawnEntityDisplayName(stack, key);

        if (display != null) {
            tooltip.add(display);
        } else {
            tooltip.add(CommonComponents.EMPTY);
            tooltip.add(Component.translatable("block.minecraft.spawner.desc1").withStyle(ChatFormatting.GRAY));
            tooltip.add(CommonComponents.space()
                    .append(Component.translatable("block.minecraft.spawner.desc2").withStyle(ChatFormatting.BLUE)));
        }
    }

    @Nullable
    static Component getSpawnEntityDisplayName(ItemStack stack, String key) {
        CompoundTag beTag = BlockItem.getBlockEntityData(stack);
        if (beTag == null) {
            return null;
        }

        ResourceLocation id = getEntityKey(beTag, key);
        if (id == null) {
            return null;
        }

        EntityType<?> type = EntityType.byString(id.toString()).orElse(null);
        if (type == null) {
            return null;
        }

        return Component.translatable(type.getDescriptionId()).withStyle(ChatFormatting.GRAY);
    }

    @Nullable
    private static ResourceLocation getEntityKey(CompoundTag tag, String key) {
        if (!tag.contains(key, Tag.TAG_COMPOUND)) {
            return null;
        }

        CompoundTag spawnData = tag.getCompound(key);
        if (!spawnData.contains("entity", Tag.TAG_COMPOUND)) {
            return null;
        }

        String id = spawnData.getCompound("entity").getString("id");
        return ResourceLocation.tryParse(id);
    }
}

