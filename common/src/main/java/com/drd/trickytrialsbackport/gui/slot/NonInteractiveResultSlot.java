package com.drd.trickytrialsbackport.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class NonInteractiveResultSlot extends Slot {
    public NonInteractiveResultSlot(Container p_311408_, int p_312001_, int p_311632_, int p_309399_) {
        super(p_311408_, p_312001_, p_311632_, p_309399_);
    }

    public void onQuickCraft(ItemStack p_312884_, ItemStack p_313225_) {
    }

    public boolean mayPickup(Player p_311019_) {
        return false;
    }

    public Optional<ItemStack> tryRemove(int p_310666_, int p_311310_, Player p_311612_) {
        return Optional.empty();
    }

    public ItemStack safeTake(int p_313087_, int p_310389_, Player p_309608_) {
        return ItemStack.EMPTY;
    }

    public ItemStack safeInsert(ItemStack p_309950_) {
        return p_309950_;
    }

    public ItemStack safeInsert(ItemStack p_311478_, int p_311938_) {
        return this.safeInsert(p_311478_);
    }

    public boolean allowModification(Player p_309707_) {
        return false;
    }

    public boolean mayPlace(ItemStack p_310756_) {
        return false;
    }

    public ItemStack remove(int p_310438_) {
        return ItemStack.EMPTY;
    }

    public void onTake(Player p_312646_, ItemStack p_313015_) {
    }

    public boolean isHighlightable() {
        return false;
    }

    public boolean isFake() {
        return true;
    }
}

