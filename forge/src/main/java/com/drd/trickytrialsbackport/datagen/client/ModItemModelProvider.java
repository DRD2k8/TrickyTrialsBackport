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
        simpleItem("bolt_armor_trim_smithing_template");
        simpleItem("breeze_rod");
        simpleItem("flow_armor_trim_smithing_template");
        simpleItem("flow_banner_pattern");
        simpleItem("flow_pottery_sherd");
        simpleItem("guster_banner_pattern");
        simpleItem("guster_pottery_sherd");
        evenSimplerBlockItem("heavy_core");
        musicDisc("music_disc_creator");
        musicDisc("music_disc_creator_music_box");
        musicDisc("music_disc_precipice");
        simpleItem("ominous_trial_key");
        simpleItem("scrape_pottery_sherd");
        simpleItem("trial_key");
        simpleItem("wind_charge");
    }

    private ItemModelBuilder simpleItem(String itemName) {
        return withExistingParent(itemName,
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(TrickyTrialsBackport.NAMESPACE,"item/" + itemName));
    }

    public void evenSimplerBlockItem(String blockName) {
        String name = blockName;
        this.withExistingParent( name, modLoc("block/" + name) );
    }

    private ItemModelBuilder musicDisc(String itemName) {
        return withExistingParent(itemName,
                ResourceLocation.withDefaultNamespace("item/template_music_disc")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(TrickyTrialsBackport.NAMESPACE,"item/" + itemName));
    }
}
