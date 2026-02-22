package com.drd.trickytrialsbackport.platform;

import com.drd.trickytrialsbackport.util.AddAfterRequest;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class CommonPlatformUtils {
    private static final List<AddAfterRequest> REQUESTS = new ArrayList<>();

    @ExpectPlatform
    public static void registerTabContents() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addAfter(ResourceKey<CreativeModeTab> tab, ItemLike reference, ItemLike... values) {
        throw new AssertionError();
    }

    public static void recordAddAfter(ResourceKey<CreativeModeTab> tab, ItemLike reference, ItemLike... values) {
        REQUESTS.add(new AddAfterRequest(tab, reference, List.of(values)));
    }

    public static List<AddAfterRequest> getRequests() {
        return REQUESTS;
    }
}
