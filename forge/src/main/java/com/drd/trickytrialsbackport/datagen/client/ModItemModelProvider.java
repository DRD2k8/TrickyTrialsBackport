package com.drd.trickytrialsbackport.datagen.client;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TrickyTrialsBackport.NAMESPACE, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem("breeze_rod");
        simpleItem("wind_charge");
    }

    private ItemModelBuilder simpleItem(String itemName) {
        return withExistingParent(itemName,
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(TrickyTrialsBackport.NAMESPACE,"item/" + itemName));
    }
}
