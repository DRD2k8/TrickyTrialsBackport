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
        VANILLA_BACKPORT_IDS.add("breeze");
        VANILLA_BACKPORT_IDS.add("breeze_rod");
        VANILLA_BACKPORT_IDS.add("breeze_spawn_egg");
        VANILLA_BACKPORT_IDS.add("breeze_wind_charge");
        VANILLA_BACKPORT_IDS.add("gust");
        VANILLA_BACKPORT_IDS.add("gust_emitter_large");
        VANILLA_BACKPORT_IDS.add("gust_emitter_small");
        VANILLA_BACKPORT_IDS.add("small_gust");
        VANILLA_BACKPORT_IDS.add("heavy_core");
        VANILLA_BACKPORT_IDS.add("mace");
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
