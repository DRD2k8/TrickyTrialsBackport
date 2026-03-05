package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.item.*;
import com.drd.trickytrialsbackport.mixin.RecordItemInvoker;
import com.drd.trickytrialsbackport.util.ModFoods;
import com.drd.trickytrialsbackport.util.ModTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class ModItems {
    public static Supplier<Item> BOGGED_SPAWN_EGG;
    public static Supplier<Item> BOLT_ARMOR_TRIM_SMITHING_TEMPLATE;
    public static Supplier<Item> BREEZE_ROD;
    public static Supplier<Item> BREEZE_SPAWN_EGG;
    public static Supplier<Item> FLOW_ARMOR_TRIM_SMITHING_TEMPLATE;
    public static Supplier<Item> FLOW_BANNER_PATTERN;
    public static Supplier<Item> FLOW_POTTERY_SHERD;
    public static Supplier<Item> GUSTER_BANNER_PATTERN;
    public static Supplier<Item> GUSTER_POTTERY_SHERD;
    public static Supplier<Item> MACE;
    public static Supplier<Item> MUSIC_DISC_CREATOR;
    public static Supplier<Item> MUSIC_DISC_CREATOR_MUSIC_BOX;
    public static Supplier<Item> MUSIC_DISC_PRECIPICE;
    public static Supplier<Item> OMINOUS_BOTTLE;
    public static Supplier<Item> OMINOUS_TRIAL_KEY;
    public static Supplier<Item> TRIAL_KEY;
    public static Supplier<Item> TRIAL_SPAWNER;
    public static Supplier<Item> SCRAPE_POTTERY_SHERD;
    public static Supplier<Item> WIND_CHARGE;

    public static void register() {
        RegistryHelper helper = RegistryHelper.getInstance();

        BOGGED_SPAWN_EGG = helper.registerAuto(Registries.ITEM, "bogged_spawn_egg", () -> ModItemHelper.createSpawnEggItem(ModEntities.BOGGED, 9084018, 3231003));
        BOLT_ARMOR_TRIM_SMITHING_TEMPLATE = helper.registerAuto(Registries.ITEM, "bolt_armor_trim_smithing_template", () -> new TrimmedSmithingTemplateItem(new Item.Properties(), "bolt"));
        BREEZE_ROD = helper.registerAuto(Registries.ITEM, "breeze_rod", () -> new Item(new Item.Properties()));
        BREEZE_SPAWN_EGG = helper.registerAuto(Registries.ITEM, "breeze_spawn_egg", () -> ModItemHelper.createSpawnEggItem(ModEntities.BREEZE, 11506911, 9529055));
        FLOW_ARMOR_TRIM_SMITHING_TEMPLATE = helper.registerAuto(Registries.ITEM, "flow_armor_trim_smithing_template", () -> new TrimmedSmithingTemplateItem(new Item.Properties(), "flow"));
        FLOW_BANNER_PATTERN = helper.registerAuto(Registries.ITEM, "flow_banner_pattern", () -> new BannerPatternItem(ModTags.BannerPatterns.FLOW, new Item.Properties()));
        FLOW_POTTERY_SHERD = helper.registerAuto(Registries.ITEM, "flow_pottery_sherd", () -> new Item(new Item.Properties()));
        GUSTER_BANNER_PATTERN = helper.registerAuto(Registries.ITEM, "guster_banner_pattern", () -> new BannerPatternItem(ModTags.BannerPatterns.GUSTER, new Item.Properties()));
        GUSTER_POTTERY_SHERD = helper.registerAuto(Registries.ITEM, "guster_pottery_sherd", () -> new Item(new Item.Properties()));
        MACE = helper.registerAuto(Registries.ITEM, "mace", () -> new MaceItem(new Item.Properties().rarity(Rarity.EPIC).durability(500)));
        MUSIC_DISC_CREATOR = helper.registerAuto(Registries.ITEM, "music_disc_creator", () -> RecordItemInvoker.create(12, ModSounds.MUSIC_DISC_CREATOR.get(), new Item.Properties().rarity(Rarity.RARE), 176));
        MUSIC_DISC_CREATOR_MUSIC_BOX = helper.registerAuto(Registries.ITEM, "music_disc_creator_music_box", () -> RecordItemInvoker.create(11, ModSounds.MUSIC_DISC_CREATOR_MUSIC_BOX.get(), new Item.Properties().rarity(Rarity.UNCOMMON), 73));
        MUSIC_DISC_PRECIPICE = helper.registerAuto(Registries.ITEM, "music_disc_precipice", () -> RecordItemInvoker.create(13, ModSounds.MUSIC_DISC_PRECIPICE.get(), new Item.Properties().rarity(Rarity.UNCOMMON), 299));
        OMINOUS_BOTTLE = helper.registerAuto(Registries.ITEM, "ominous_bottle", () -> new OminousBottleItem(new Item.Properties().food(ModFoods.OMINOUS_BOTTLE)));
        OMINOUS_TRIAL_KEY = helper.registerAuto(Registries.ITEM, "ominous_trial_key", () -> new Item(new Item.Properties()));
        SCRAPE_POTTERY_SHERD = helper.registerAuto(Registries.ITEM, "scrape_pottery_sherd", () -> new Item(new Item.Properties()));
        TRIAL_KEY = helper.registerAuto(Registries.ITEM, "trial_key", () -> new Item(new Item.Properties()));
        TRIAL_SPAWNER = helper.registerAuto(Registries.ITEM, "trial_spawner", () -> new TrialSpawnerItem(ModBlocks.TRIAL_SPAWNER.get(), new Item.Properties()));
        WIND_CHARGE = helper.registerAuto(Registries.ITEM, "wind_charge", () -> new WindChargeItem(new Item.Properties()));
    }
}
