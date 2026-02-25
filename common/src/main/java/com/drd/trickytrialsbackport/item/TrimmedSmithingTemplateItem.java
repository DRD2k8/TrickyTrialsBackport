package com.drd.trickytrialsbackport.item;

import com.drd.trickytrialsbackport.TrickyTrialsBackport;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrimmedSmithingTemplateItem extends Item {
    private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
    private static final Component INGREDIENTS_TITLE = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.ingredients"))).withStyle(TITLE_FORMAT);
    private static final Component APPLIES_TO_TITLE = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.applies_to"))).withStyle(TITLE_FORMAT);
    private final Component appliesTo;
    private final Component ingredients;
    private final Component upgradeDescription;

    public TrimmedSmithingTemplateItem(Properties properties, String name) {
        super(properties);
        this.appliesTo = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.armor_trim.applies_to"))).withStyle(DESCRIPTION_FORMAT);
        this.ingredients = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.armor_trim.ingredients"))).withStyle(DESCRIPTION_FORMAT);
        this.upgradeDescription = Component.translatable("trim_pattern." + TrickyTrialsBackport.NAMESPACE + "." + name).withStyle(TITLE_FORMAT);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        list.add(this.upgradeDescription);
        list.add(CommonComponents.EMPTY);
        list.add(APPLIES_TO_TITLE);
        list.add(CommonComponents.space().append(this.appliesTo));
        list.add(INGREDIENTS_TITLE);
        list.add(CommonComponents.space().append(this.ingredients));
    }
}
