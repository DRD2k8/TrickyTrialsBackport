package com.drd.trickytrialsbackport.block.entity;

import com.drd.trickytrialsbackport.block.CrafterBlock;
import com.drd.trickytrialsbackport.gui.CrafterMenu;
import com.drd.trickytrialsbackport.registry.ModBlockEntities;
import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Iterator;

public class CrafterBlockEntity extends RandomizableContainerBlockEntity implements CraftingContainer {
    public static final int CONTAINER_WIDTH = 3;
    public static final int CONTAINER_HEIGHT = 3;
    public static final int CONTAINER_SIZE = 9;
    public static final int SLOT_DISABLED = 1;
    public static final int SLOT_ENABLED = 0;
    public static final int DATA_TRIGGERED = 9;
    public static final int NUM_DATA = 10;
    private NonNullList<ItemStack> items;
    private int craftingTicksRemaining;
    protected final ContainerData containerData;

    public CrafterBlockEntity(BlockPos p_309972_, BlockState p_313058_) {
        super(ModBlockEntities.CRAFTER.get(), p_309972_, p_313058_);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        this.craftingTicksRemaining = 0;
        this.containerData = new ContainerData() {
            private final int[] slotStates = new int[9];
            private int triggered = 0;

            public int get(int p_310435_) {
                return p_310435_ == 9 ? this.triggered : this.slotStates[p_310435_];
            }

            public void set(int p_313229_, int p_312585_) {
                if (p_313229_ == 9) {
                    this.triggered = p_312585_;
                } else {
                    this.slotStates[p_313229_] = p_312585_;
                }

            }

            public int getCount() {
                return 10;
            }
        };
    }

    protected Component getDefaultName() {
        return Component.translatable("container.crafter");
    }

    protected AbstractContainerMenu createMenu(int p_312650_, Inventory p_309858_) {
        return new CrafterMenu(p_312650_, p_309858_, this, this.containerData);
    }

    public void setSlotState(int p_310046_, boolean p_310331_) {
        if (this.slotCanBeDisabled(p_310046_)) {
            this.containerData.set(p_310046_, p_310331_ ? 0 : 1);
            this.setChanged();
        }
    }

    public boolean isSlotDisabled(int p_312222_) {
        if (p_312222_ >= 0 && p_312222_ < 9) {
            return this.containerData.get(p_312222_) == 1;
        } else {
            return false;
        }
    }

    public boolean canPlaceItem(int p_311324_, ItemStack p_312777_) {
        if (this.containerData.get(p_311324_) == 1) {
            return false;
        } else {
            ItemStack $$2 = (ItemStack)this.items.get(p_311324_);
            int $$3 = $$2.getCount();
            if ($$3 >= $$2.getMaxStackSize()) {
                return false;
            } else if ($$2.isEmpty()) {
                return true;
            } else {
                return !this.smallerStackExist($$3, $$2, p_311324_);
            }
        }
    }

    private boolean smallerStackExist(int p_312152_, ItemStack p_309554_, int p_312872_) {
        for(int $$3 = p_312872_ + 1; $$3 < 9; ++$$3) {
            if (!this.isSlotDisabled($$3)) {
                ItemStack $$4 = this.getItem($$3);
                if ($$4.isEmpty() || $$4.getCount() < p_312152_ && ItemStack.isSameItemSameTags($$4, p_309554_)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void load(CompoundTag p_310330_) {
        super.load(p_310330_);
        this.craftingTicksRemaining = p_310330_.getInt("crafting_ticks_remaining");
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(p_310330_)) {
            ContainerHelper.loadAllItems(p_310330_, this.items);
        }

        int[] $$1 = p_310330_.getIntArray("disabled_slots");

        for(int $$2 = 0; $$2 < 9; ++$$2) {
            this.containerData.set($$2, 0);
        }

        int[] var7 = $$1;
        int var4 = $$1.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            int $$3 = var7[var5];
            if (this.slotCanBeDisabled($$3)) {
                this.containerData.set($$3, 1);
            }
        }

        this.containerData.set(9, p_310330_.getInt("triggered"));
    }

    protected void saveAdditional(CompoundTag p_309594_) {
        super.saveAdditional(p_309594_);
        p_309594_.putInt("crafting_ticks_remaining", this.craftingTicksRemaining);
        if (!this.trySaveLootTable(p_309594_)) {
            ContainerHelper.saveAllItems(p_309594_, this.items);
        }

        this.addDisabledSlots(p_309594_);
        this.addTriggered(p_309594_);
    }

    public int getContainerSize() {
        return 9;
    }

    public boolean isEmpty() {
        Iterator var1 = this.items.iterator();

        ItemStack $$0;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            $$0 = (ItemStack)var1.next();
        } while($$0.isEmpty());

        return false;
    }

    public ItemStack getItem(int p_310446_) {
        return (ItemStack)this.items.get(p_310446_);
    }

    public void setItem(int p_312882_, ItemStack p_311521_) {
        if (this.isSlotDisabled(p_312882_)) {
            this.setSlotState(p_312882_, true);
        }

        super.setItem(p_312882_, p_311521_);
    }

    public boolean stillValid(Player p_311318_) {
        if (this.level != null && this.level.getBlockEntity(this.worldPosition) == this) {
            return !(p_311318_.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) > 64.0);
        } else {
            return false;
        }
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    protected void setItems(NonNullList<ItemStack> p_311420_) {
        this.items = p_311420_;
    }

    public int getWidth() {
        return 3;
    }

    public int getHeight() {
        return 3;
    }

    public void fillStackedContents(StackedContents p_310482_) {
        Iterator var2 = this.items.iterator();

        while(var2.hasNext()) {
            ItemStack $$1 = (ItemStack)var2.next();
            p_310482_.accountSimpleStack($$1);
        }

    }

    private void addDisabledSlots(CompoundTag p_309756_) {
        IntList $$1 = new IntArrayList();

        for(int $$2 = 0; $$2 < 9; ++$$2) {
            if (this.isSlotDisabled($$2)) {
                $$1.add($$2);
            }
        }

        p_309756_.putIntArray("disabled_slots", $$1);
    }

    private void addTriggered(CompoundTag p_312165_) {
        p_312165_.putInt("triggered", this.containerData.get(9));
    }

    public void setTriggered(boolean p_311394_) {
        this.containerData.set(9, p_311394_ ? 1 : 0);
    }

    @VisibleForTesting
    public boolean isTriggered() {
        return this.containerData.get(9) == 1;
    }

    public static void serverTick(Level p_311764_, BlockPos p_309568_, BlockState p_311393_, CrafterBlockEntity p_313070_) {
        int $$4 = p_313070_.craftingTicksRemaining - 1;
        if ($$4 >= 0) {
            p_313070_.craftingTicksRemaining = $$4;
            if ($$4 == 0) {
                p_311764_.setBlock(p_309568_, (BlockState)p_311393_.setValue(CrafterBlock.CRAFTING, false), 3);
            }

        }
    }

    public void setCraftingTicksRemaining(int p_312384_) {
        this.craftingTicksRemaining = p_312384_;
    }

    public int getRedstoneSignal() {
        int $$0 = 0;

        for(int $$1 = 0; $$1 < this.getContainerSize(); ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if (!$$2.isEmpty() || this.isSlotDisabled($$1)) {
                ++$$0;
            }
        }

        return $$0;
    }

    private boolean slotCanBeDisabled(int p_309429_) {
        return p_309429_ > -1 && p_309429_ < 9 && ((ItemStack)this.items.get(p_309429_)).isEmpty();
    }
}
