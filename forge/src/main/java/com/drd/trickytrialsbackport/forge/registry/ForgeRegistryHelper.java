package com.drd.trickytrialsbackport.forge.registry;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import com.drd.trickytrialsbackport.registry.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ForgeRegistryHelper extends RegistryHelper {
    private final Map<ResourceKey<?>, DeferredRegister<?>> registers = new HashMap<>();
    private final Map<ResourceKey<?>, DeferredRegister<?>> minecraftRegisters = new HashMap<>();
    private final IEventBus modEventBus;

    public ForgeRegistryHelper() {
        this.modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> register(ResourceKey<? extends Registry<? super T>> registryKey, String name, Supplier<T> supplier) {
        DeferredRegister<T> register = (DeferredRegister<T>) registers.computeIfAbsent(registryKey, key -> {
            // Safe cast: We know the key matches the registry type T
            ResourceKey<? extends Registry<T>> typedKey = (ResourceKey<? extends Registry<T>>) key;
            DeferredRegister<T> newRegister = DeferredRegister.create(typedKey, TrickyTrialsBackport.MOD_ID);
            newRegister.register(modEventBus);
            return newRegister;
        });

        RegistryObject<T> registryObject = register.register(name, supplier);
        return registryObject;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> registerWithNamespace(ResourceKey<? extends Registry<? super T>> registryKey, String namespace, String name, Supplier<T> supplier) {
        if (namespace.equals(TrickyTrialsBackport.MOD_ID)) {
            return register(registryKey, name, supplier);
        }

        // For minecraft namespace or other namespaces
        String mapKey = namespace + ":" + registryKey.location();
        DeferredRegister<T> register = (DeferredRegister<T>) minecraftRegisters.computeIfAbsent(registryKey, key -> {
            ResourceKey<? extends Registry<T>> typedKey = (ResourceKey<? extends Registry<T>>) key;
            DeferredRegister<T> newRegister = DeferredRegister.create(typedKey, namespace);
            newRegister.register(modEventBus);
            return newRegister;
        });

        RegistryObject<T> registryObject = register.register(name, supplier);
        return registryObject;
    }

    @Override
    public void fireRegistrationCallbacks() {
        super.fireRegistrationCallbacks();
    }
}
