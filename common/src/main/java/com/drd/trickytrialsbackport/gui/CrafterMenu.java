package com.drd.trickytrialsbackport.gui;

import com.drd.trickytrialsbackport.block.CrafterBlock;
import com.drd.trickytrialsbackport.gui.slot.CrafterSlot;
import com.drd.trickytrialsbackport.gui.slot.NonInteractiveResultSlot;
import com.drd.trickytrialsbackport.registry.ModMenuTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CrafterMenu extends AbstractContainerMenu implements ContainerListener {
    protected static final int SLOT_COUNT = 9;
    private static final int INV_SLOT_START = 9;
    private static final int INV_SLOT_END = 36;
    private static final int USE_ROW_SLOT_START = 36;
    private static final int USE_ROW_SLOT_END = 45;
    private final ResultContainer resultContainer = new ResultContainer();
    private final ContainerData containerData;
    private final Player player;
    private final CraftingContainer container;

    public CrafterMenu(int id, Inventory inv) {
        super(ModMenuTypes.CRAFTER_3x3.get(), id);
        this.player = inv.player;
        this.containerData = new SimpleContainerData(10);
        this.container = new TransientCraftingContainer(this, 3, 3);
        this.addSlots(inv);
    }

    public CrafterMenu(int id, Inventory inv, CraftingContainer container, ContainerData containerData) {
        super(ModMenuTypes.CRAFTER_3x3.get(), id);
        this.player = inv.player;
        this.containerData = containerData;
        this.container = container;
        checkContainerSize(container, 9);
        container.startOpen(inv.player);
        this.addSlots(inv);
        this.addSlotListener(this);
    }

    private void addSlots(Inventory p_312143_) {
        int $$6;
        int $$5;
        for($$6 = 0; $$6 < 3; ++$$6) {
            for($$5 = 0; $$5 < 3; ++$$5) {
                int $$3 = $$5 + $$6 * 3;
                this.addSlot(new CrafterSlot(this.container, $$3, 26 + $$5 * 18, 17 + $$6 * 18, this));
            }
        }

        for($$6 = 0; $$6 < 3; ++$$6) {
            for($$5 = 0; $$5 < 9; ++$$5) {
                this.addSlot(new Slot(p_312143_, $$5 + $$6 * 9 + 9, 8 + $$5 * 18, 84 + $$6 * 18));
            }
        }

        for($$6 = 0; $$6 < 9; ++$$6) {
            this.addSlot(new Slot(p_312143_, $$6, 8 + $$6 * 18, 142));
        }

        this.addSlot(new NonInteractiveResultSlot(this.resultContainer, 0, 134, 35));
        this.addDataSlots(this.containerData);
        this.refreshRecipeResult();
    }

    public void setSlotState(int p_312148_, boolean p_312187_) {
        CrafterSlot $$2 = (CrafterSlot)this.getSlot(p_312148_);
        this.containerData.set($$2.index, p_312187_ ? 0 : 1);
        this.broadcastChanges();
    }

    public boolean isSlotDisabled(int p_311661_) {
        if (p_311661_ > -1 && p_311661_ < 9) {
            return this.containerData.get(p_311661_) == 1;
        } else {
            return false;
        }
    }

    public boolean isPowered() {
        return this.containerData.get(9) == 1;
    }

    public ItemStack quickMoveStack(Player p_313133_, int p_309724_) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get(p_309724_);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            if (p_309724_ < 9) {
                if (!this.moveItemStackTo($$4, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo($$4, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if ($$4.isEmpty()) {
                $$3.set(ItemStack.EMPTY);
            } else {
                $$3.setChanged();
            }

            if ($$4.getCount() == $$2.getCount()) {
                return ItemStack.EMPTY;
            }

            $$3.onTake(p_313133_, $$4);
        }

        return $$2;
    }

    public boolean stillValid(Player p_309546_) {
        return this.container.stillValid(p_309546_);
    }

    private void refreshRecipeResult() {
        Player var2 = this.player;
        if (var2 instanceof ServerPlayer $$0) {
            Level $$1 = $$0.level();
            ItemStack $$2 = (ItemStack) CrafterBlock.getPotentialResults($$1, this.container).map((p_309555_) -> {
                return p_309555_.assemble(this.container, $$1.registryAccess());
            }).orElse(ItemStack.EMPTY);
            this.resultContainer.setItem(0, $$2);
        }
    }

    public Container getContainer() {
        return this.container;
    }

    public void slotChanged(AbstractContainerMenu p_313164_, int p_310604_, ItemStack p_312680_) {
        this.refreshRecipeResult();
    }

    public void dataChanged(AbstractContainerMenu p_312122_, int p_310028_, int p_310424_) {
    }
}
