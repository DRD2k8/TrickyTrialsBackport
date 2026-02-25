package com.drd.trickytrialsbackport.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecordItem.class)
public interface RecordItemInvoker {
    @Invoker("<init>")
    static RecordItem create(int i, SoundEvent soundEvent, Item.Properties properties, int j) {
        throw new AssertionError();
    }
}
