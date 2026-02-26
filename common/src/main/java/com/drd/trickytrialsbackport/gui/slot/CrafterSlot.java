package com.drd.trickytrialsbackport.gui.slot;

import com.drd.trickytrialsbackport.gui.CrafterMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CrafterSlot extends Slot {
    private final CrafterMenu menu;

    public CrafterSlot(Container p_311610_, int p_312176_, int p_310849_, int p_312973_, CrafterMenu p_310976_) {
        super(p_311610_, p_312176_, p_310849_, p_312973_);
        this.menu = p_310976_;
    }

    public boolean mayPlace(ItemStack p_310494_) {
        return !this.menu.isSlotDisabled(this.index) && super.mayPlace(p_310494_);
    }

    public void setChanged() {
        super.setChanged();
        this.menu.slotsChanged(this.container);
    }
}

