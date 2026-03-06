package com.drd.trickytrialsbackport.registry;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public abstract class RegistryHelper {
    public static final Set<String> VANILLA_BACKPORT_IDS = new HashSet<>();

    static {
        // Always add the stuff here so that way they can register in the minecraft namespace
        VANILLA_BACKPORT_IDS.add("backyard");
        VANILLA_BACKPORT_IDS.add("baroque");
        VANILLA_BACKPORT_IDS.add("bogged");
        VANILLA_BACKPORT_IDS.add("bogged_spawn_egg");
        VANILLA_BACKPORT_IDS.add("bolt_armor_trim_smithing_template");
        VANILLA_BACKPORT_IDS.add("bouquet");
        VANILLA_BACKPORT_IDS.add("breeze");
        VANILLA_BACKPORT_IDS.add("breeze_rod");
        VANILLA_BACKPORT_IDS.add("breeze_spawn_egg");
        VANILLA_BACKPORT_IDS.add("breeze_wind_charge");
        VANILLA_BACKPORT_IDS.add("cavebird");
        VANILLA_BACKPORT_IDS.add("changing");
        VANILLA_BACKPORT_IDS.add("chiseled_tuff");
        VANILLA_BACKPORT_IDS.add("chiseled_tuff_bricks");
        VANILLA_BACKPORT_IDS.add("cotan");
        VANILLA_BACKPORT_IDS.add("crafter");
        VANILLA_BACKPORT_IDS.add("endboss");
        VANILLA_BACKPORT_IDS.add("fern");
        VANILLA_BACKPORT_IDS.add("finding");
        VANILLA_BACKPORT_IDS.add("flow");
        VANILLA_BACKPORT_IDS.add("flow_armor_trim_smithing_template");
        VANILLA_BACKPORT_IDS.add("flow_pottery_pattern");
        VANILLA_BACKPORT_IDS.add("flow_banner_pattern");
        VANILLA_BACKPORT_IDS.add("flow_pottery_sherd");
        VANILLA_BACKPORT_IDS.add("gust");
        VANILLA_BACKPORT_IDS.add("gust_emitter_large");
        VANILLA_BACKPORT_IDS.add("gust_emitter_small");
        VANILLA_BACKPORT_IDS.add("guster");
        VANILLA_BACKPORT_IDS.add("guster_banner_pattern");
        VANILLA_BACKPORT_IDS.add("guster_pottery_pattern");
        VANILLA_BACKPORT_IDS.add("guster_pottery_sherd");
        VANILLA_BACKPORT_IDS.add("lowmist");
        VANILLA_BACKPORT_IDS.add("small_gust");
        VANILLA_BACKPORT_IDS.add("heavy_core");
        VANILLA_BACKPORT_IDS.add("humble");
        VANILLA_BACKPORT_IDS.add("infested");
        VANILLA_BACKPORT_IDS.add("mace");
        VANILLA_BACKPORT_IDS.add("meditative");
        VANILLA_BACKPORT_IDS.add("music_disc_creator");
        VANILLA_BACKPORT_IDS.add("music_disc_creator_music_box");
        VANILLA_BACKPORT_IDS.add("music_disc_precipice");
        VANILLA_BACKPORT_IDS.add("ominous_bottle");
        VANILLA_BACKPORT_IDS.add("ominous_spawning");
        VANILLA_BACKPORT_IDS.add("ominous_trial_key");
        VANILLA_BACKPORT_IDS.add("orb");
        VANILLA_BACKPORT_IDS.add("owlemons");
        VANILLA_BACKPORT_IDS.add("passage");
        VANILLA_BACKPORT_IDS.add("polished_tuff");
        VANILLA_BACKPORT_IDS.add("polished_tuff_slab");
        VANILLA_BACKPORT_IDS.add("polished_tuff_stairs");
        VANILLA_BACKPORT_IDS.add("polished_tuff_wall");
        VANILLA_BACKPORT_IDS.add("pond");
        VANILLA_BACKPORT_IDS.add("prairie_ride");
        VANILLA_BACKPORT_IDS.add("raid_omen");
        VANILLA_BACKPORT_IDS.add("scrape_pottery_pattern");
        VANILLA_BACKPORT_IDS.add("scrape_pottery_sherd");
        VANILLA_BACKPORT_IDS.add("set_ominous_bottle_amplifier");
        VANILLA_BACKPORT_IDS.add("sunflowers");
        VANILLA_BACKPORT_IDS.add("tides");
        VANILLA_BACKPORT_IDS.add("trial_key");
        VANILLA_BACKPORT_IDS.add("trial_omen");
        VANILLA_BACKPORT_IDS.add("trial_spawner");
        VANILLA_BACKPORT_IDS.add("trial_spawner_detection");
        VANILLA_BACKPORT_IDS.add("trial_spawner_detection_ominous");
        VANILLA_BACKPORT_IDS.add("tuff_brick_slab");
        VANILLA_BACKPORT_IDS.add("tuff_brick_stairs");
        VANILLA_BACKPORT_IDS.add("tuff_brick_wall");
        VANILLA_BACKPORT_IDS.add("tuff_bricks");
        VANILLA_BACKPORT_IDS.add("tuff_slab");
        VANILLA_BACKPORT_IDS.add("tuff_stairs");
        VANILLA_BACKPORT_IDS.add("tuff_wall");
        VANILLA_BACKPORT_IDS.add("unpacked");
        VANILLA_BACKPORT_IDS.add("vault");
        VANILLA_BACKPORT_IDS.add("vault_connection");
        VANILLA_BACKPORT_IDS.add("wind_charge");
    }

    private static RegistryHelper instance;

    public static RegistryHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("RegistryHelper not initialized!");
        }
        return instance;
    }

    public static void setInstance(RegistryHelper helper) {
        if (instance != null) {
            throw new IllegalStateException("RegistryHelper already initialized!");
        }
        instance = helper;
    }

    public static boolean isVanillaBackport(String name) {
        return VANILLA_BACKPORT_IDS.contains(name);
    }

    protected final List<Runnable> registrationCallbacks = new ArrayList<>();

    public abstract <T> Supplier<T> register(ResourceKey<? extends Registry<? super T>> registry, String name, Supplier<T> supplier);

    public abstract <T> Supplier<T> registerWithNamespace(ResourceKey<? extends Registry<? super T>> registry, String namespace, String name, Supplier<T> supplier);

    public <T> Supplier<T> registerAuto(ResourceKey<? extends Registry<? super T>> registry, String name, Supplier<T> supplier) {
        if (isVanillaBackport(name)) {
            return registerWithNamespace(registry, TrickyTrialsBackport.NAMESPACE, name, supplier);
        } else {
            return register(registry, name, supplier);
        }
    }

    public void onRegisterComplete(Runnable callback) {
        registrationCallbacks.add(callback);
    }

    protected void fireRegistrationCallbacks() {
        registrationCallbacks.forEach(Runnable::run);
    }

    protected ResourceLocation id(String name) {
        return new ResourceLocation(TrickyTrialsBackport.MOD_ID, name);
    }

    protected ResourceLocation minecraftId(String name) {
        return new ResourceLocation(TrickyTrialsBackport.NAMESPACE, name);
    }

    public void restoreVanillaNamespaceEntries() {
    }
}
