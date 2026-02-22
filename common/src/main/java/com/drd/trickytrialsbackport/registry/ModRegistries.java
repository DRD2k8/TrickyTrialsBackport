package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModRegistries {
    public static final DeferredRegister<BannerPattern> BANNER_PATTERNS = createDeferredRegister(Registries.BANNER_PATTERN);
    public static final DeferredRegister<Block> BLOCKS = createDeferredRegister(Registries.BLOCK);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = createDeferredRegister(Registries.BLOCK_ENTITY_TYPE);
    public static final DeferredRegister<MobEffect> EFFECTS = createDeferredRegister(Registries.MOB_EFFECT);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = createDeferredRegister(Registries.ENCHANTMENT);
    public static final DeferredRegister<EntityType<?>> ENTITIES = createDeferredRegister(Registries.ENTITY_TYPE);
    public static final DeferredRegister<Item> ITEMS = createDeferredRegister(Registries.ITEM);
    public static final DeferredRegister<PaintingVariant> PAINTINGS = createDeferredRegister(Registries.PAINTING_VARIANT);
    public static final DeferredRegister<SoundEvent> SOUNDS = createDeferredRegister(Registries.SOUND_EVENT);

    public static <T> DeferredRegister<T> createDeferredRegister(ResourceKey<Registry<T>> key) {
        return DeferredRegister.create(TrickyTrialsBackport.NAMESPACE, key);
    }
}
