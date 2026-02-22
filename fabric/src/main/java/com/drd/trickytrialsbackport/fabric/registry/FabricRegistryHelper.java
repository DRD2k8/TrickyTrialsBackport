package com.drd.trickytrialsbackport.fabric.registry;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.registry.RegistryHelper;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class FabricRegistryHelper extends RegistryHelper {
    private final List<RestorableEntry<?>> minecraftEntries = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> register(ResourceKey<? extends Registry<? super T>> registryKey, String name, Supplier<T> supplier) {
        return registerWithNamespace(registryKey, TrickyTrialsBackport.MOD_ID, name, supplier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> registerWithNamespace(ResourceKey<? extends Registry<? super T>> registryKey,
                                                 String namespace,
                                                 String name,
                                                 Supplier<T> supplier) {
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(registryKey.location());

        if (registry == null) {
            throw new IllegalArgumentException("Unknown registry: " + registryKey.location());
        }

        boolean restoreBootstrapFlag = false;
        boolean originalPostBootstrap = false;

        if (TrickyTrialsBackport.NAMESPACE.equals(namespace)) {
            originalPostBootstrap = RegistrySyncManager.postBootstrap;
            if (originalPostBootstrap) {
                RegistrySyncManager.postBootstrap = false;
                restoreBootstrapFlag = true;
            }
        }

        T registered;
        ResourceLocation id = new ResourceLocation(namespace, name);

        try {
            registered = Registry.register(registry, id, supplier.get());
            if (TrickyTrialsBackport.NAMESPACE.equals(namespace)) {
                cacheMinecraftEntry(registryKey, id, registered);
            }
        } finally {
            if (restoreBootstrapFlag) {
                RegistrySyncManager.postBootstrap = originalPostBootstrap;
            }
        }
        return () -> registered;
    }

    @Override
    public void fireRegistrationCallbacks() {
        super.fireRegistrationCallbacks();
    }

    private <T> void cacheMinecraftEntry(ResourceKey<? extends Registry<? super T>> registryKey,
                                         ResourceLocation id,
                                         T value) {
        minecraftEntries.add(new RestorableEntry<>(registryKey, id, value));
    }

    @Override
    public void restoreVanillaNamespaceEntries() {
        if (minecraftEntries.isEmpty()) {
            return;
        }

        int restored = 0;
        for (RestorableEntry<?> entry : minecraftEntries) {
            restored += restoreEntry(entry) ? 1 : 0;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> boolean restoreEntry(RestorableEntry<T> entry) {
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(entry.registryKey.location());
        if (registry == null) {
            return false;
        }

        T existing = registry.get(entry.id);
        if (existing != null && Objects.equals(existing, entry.value)) {
            return false;
        }

        Registry.register(registry, entry.id, entry.value);
        return true;
    }

    private record RestorableEntry<T>(ResourceKey<? extends Registry<? super T>> registryKey,
                                      ResourceLocation id,
                                      T value) {
    }
}
