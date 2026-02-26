package com.drd.trickytrialsbackport.client.screen;

import com.drd.trickytrialsbackport.gui.CrafterMenu;
import com.drd.trickytrialsbackport.gui.slot.CrafterSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CrafterScreen extends AbstractContainerScreen<CrafterMenu> {
    private static final ResourceLocation CONTAINER_LOCATION =
            new ResourceLocation("textures/gui/container/crafter.png");
    private static final ResourceLocation DISABLED_SLOT_TEXTURE =
            new ResourceLocation("textures/gui/sprites/container/crafter/disabled_slot.png");
    private static final ResourceLocation POWERED_REDSTONE_TEXTURE =
            new ResourceLocation("textures/gui/sprites/container/crafter/powered_redstone.png");
    private static final ResourceLocation UNPOWERED_REDSTONE_TEXTURE =
            new ResourceLocation("textures/gui/sprites/container/crafter/unpowered_redstone.png");
    private static final Component DISABLED_SLOT_TOOLTIP =
            Component.translatable("gui.togglable_slot");

    private final Player player;

    public CrafterScreen(CrafterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.player = inv.player;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void slotClicked(Slot slot, int slotIndex, int mouseButton, ClickType clickType) {
        if (slot instanceof CrafterSlot && !slot.hasItem() && !this.player.isSpectator()) {
            switch (clickType) {
                case PICKUP:
                    if (this.menu.isSlotDisabled(slotIndex)) {
                        enableSlot(slotIndex);
                    } else if (this.menu.getCarried().isEmpty()) {
                        disableSlot(slotIndex);
                    }
                    break;

                case SWAP:
                    ItemStack hotbarItem = this.player.getInventory().getItem(mouseButton);
                    if (this.menu.isSlotDisabled(slotIndex) && !hotbarItem.isEmpty()) {
                        enableSlot(slotIndex);
                    }
                    break;
            }
        }

        super.slotClicked(slot, slotIndex, mouseButton, clickType);
    }

    private void enableSlot(int index) {
        updateSlotState(index, true);
    }

    private void disableSlot(int index) {
        updateSlotState(index, false);
    }

    private void updateSlotState(int index, boolean enabled) {
        this.menu.setSlotState(index, enabled);

        Minecraft.getInstance().gameMode.handleInventoryButtonClick(
                this.menu.containerId,
                enabled ? index : (index | 0x80)
        );

        float pitch = enabled ? 1.0F : 0.75F;
        this.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, pitch);
    }

    private void renderDisabledSlot(GuiGraphics graphics, CrafterSlot slot) {
        int x = this.leftPos + slot.x - 1;
        int y = this.topPos + slot.y - 1;

        RenderSystem.setShaderTexture(0, DISABLED_SLOT_TEXTURE);
        graphics.blit(DISABLED_SLOT_TEXTURE, x, y, 0, 0, 18, 18, 18, 18);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        for (Slot slot : this.menu.slots) {
            if (slot instanceof CrafterSlot && this.menu.isSlotDisabled(slot.index)) {
                renderDisabledSlot(graphics, (CrafterSlot) slot);
            }
        }

        renderRedstone(graphics);
        renderTooltip(graphics, mouseX, mouseY);

        if (this.hoveredSlot instanceof CrafterSlot &&
                !this.menu.isSlotDisabled(this.hoveredSlot.index) &&
                this.menu.getCarried().isEmpty() &&
                !this.hoveredSlot.hasItem()) {

            graphics.renderTooltip(this.font, DISABLED_SLOT_TOOLTIP, mouseX, mouseY);
        }
    }

    private void renderRedstone(GuiGraphics graphics) {
        int x = this.width / 2 + 9;
        int y = this.height / 2 - 48;

        ResourceLocation tex = this.menu.isPowered()
                ? POWERED_REDSTONE_TEXTURE
                : UNPOWERED_REDSTONE_TEXTURE;

        RenderSystem.setShaderTexture(0, tex);
        graphics.blit(tex, x, y, 0, 0, 16, 16, 16, 16);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        RenderSystem.setShaderTexture(0, CONTAINER_LOCATION);
        graphics.blit(CONTAINER_LOCATION, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
}
