package com.drd.trickytrialsbackport.block.entity.vault;

import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public enum VaultState implements StringRepresentable {
    INACTIVE("inactive", LightLevel.HALF_LIT) {
        @Override
        protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
            sharedData.setDisplayItem(ItemStack.EMPTY);

            level.playSound(null, pos, ModSounds.VAULT_DEACTIVATE.get(), SoundSource.BLOCKS, 1.0F, ominous ? 0.5F : 1.0F);
        }
    },

    ACTIVE("active", LightLevel.LIT) {
        @Override
        protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
            if (!sharedData.hasDisplayItem()) {
                VaultBlockEntity.cycleDisplayItem(level, this, config, sharedData, pos);
            }

            level.playSound(null, pos, ModSounds.VAULT_ACTIVATE.get(), SoundSource.BLOCKS, 1.0F, ominous ? 0.5F : 1.0F);
        }
    },

    UNLOCKING("unlocking", LightLevel.LIT) {
        @Override
        protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
            level.playSound(null, pos, ModSounds.VAULT_INSERT_ITEM.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    },

    EJECTING("ejecting", LightLevel.LIT) {
        @Override
        protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
            level.playSound(null, pos, ModSounds.VAULT_OPEN_SHUTTER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        @Override
        protected void onExit(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
            level.playSound(null, pos, ModSounds.VAULT_CLOSE_SHUTTER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        @Override
        public void onTick(ServerLevel level, BlockPos pos, VaultConfig config,
                           VaultSharedData sharedData, VaultServerData serverData) {
        }
    };

    private final String stateName;
    private final LightLevel lightLevel;

    VaultState(String name, LightLevel lightLevel) {
        this.stateName = name;
        this.lightLevel = lightLevel;
    }

    @Override
    public String getSerializedName() {
        return this.stateName;
    }

    public int lightLevel() {
        return this.lightLevel.value;
    }

    public VaultState tickAndGetNext(ServerLevel level, BlockPos pos, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData) {
        return switch (this) {
            case INACTIVE -> updateStateForConnectedPlayers(level, pos, config, serverData, sharedData, config.activationRange());

            case ACTIVE -> {
                updateStateForConnectedPlayers(level, pos, config, serverData, sharedData, config.deactivationRange());

                if (serverData.getPreviewPool().isEmpty()) {
                    List<ItemStack> items = serverData.generatePreviewLoot(level, serverData.isOminous());
                    items.removeIf(stack -> stack == null || stack.isEmpty());
                    serverData.setPreviewPool(items);
                }

                serverData.previewTicks++;

                if (serverData.previewTicks >= 1) {
                    serverData.previewTicks = 0;

                    ItemStack preview = serverData.popNextPreviewItem();

                    if (preview == null || preview.isEmpty()) {
                        preview = ItemStack.EMPTY;
                    } else {
                        preview = preview.copy();
                    }

                    sharedData.setDisplayItem(preview);

                    VaultBlockEntity be = (VaultBlockEntity) level.getBlockEntity(pos);
                    if (be != null) {
                        be.setChanged();
                        be.sync();
                    }
                }

                yield ACTIVE;
            }

            case UNLOCKING -> {
                serverData.previewTicks = 0;
                serverData.getPreviewPool().clear();
                serverData.pauseStateUpdatingUntil(level.getGameTime() + 20L);
                yield EJECTING;
            }

            case EJECTING -> {
                if (serverData.getItemsToEject().isEmpty()) {
                    serverData.markEjectionFinished();
                    yield updateStateForConnectedPlayers(level, pos, config, serverData, sharedData, config.deactivationRange());
                } else {
                    float progress = serverData.ejectionProgress();

                    ItemStack popped = serverData.popNextItemToEject();
                    if (popped == null || popped.isEmpty()) {
                        serverData.pauseStateUpdatingUntil(level.getGameTime() + 20);
                        yield EJECTING;
                    }

                    sharedData.setDisplayItem(popped);

                    ejectResultItem(level, pos, popped, progress);

                    VaultBlockEntity be = (VaultBlockEntity) level.getBlockEntity(pos);
                    if (be != null) {
                        be.setChanged();
                        level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
                    }

                    boolean last = serverData.getItemsToEject().isEmpty();
                    int delay = last ? 20 : 20;

                    serverData.pauseStateUpdatingUntil(level.getGameTime() + delay);
                    yield EJECTING;
                }
            }
        };
    }

    public ItemStack getRandomPreviewItem(RandomSource random, VaultServerData serverData) {
        List<ItemStack> pool = serverData.getPreviewPool();
        if (pool.isEmpty()) return ItemStack.EMPTY;
        return pool.get(random.nextInt(pool.size()));
    }

    private static VaultState updateStateForConnectedPlayers(
            ServerLevel level, BlockPos pos, VaultConfig config,
            VaultServerData serverData, VaultSharedData sharedData, double range
    ) {
        sharedData.updateConnectedPlayersWithinRange(level, pos, serverData, config, range);
        serverData.pauseStateUpdatingUntil(level.getGameTime() + 20L);
        return sharedData.hasConnectedPlayers() ? ACTIVE : INACTIVE;
    }

    public void onTransition(ServerLevel level, BlockPos pos, VaultState next, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
        this.onExit(level, pos, config, sharedData);
        next.onEnter(level, pos, config, sharedData, ominous);
    }

    protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {}
    protected void onExit(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {}

    private void ejectResultItem(ServerLevel level, BlockPos pos, ItemStack stack, float progress) {
        ItemEntity item = new ItemEntity(level,
                pos.getX() + 0.5,
                pos.getY() + 1.2,
                pos.getZ() + 0.5,
                stack);

        item.setDeltaMovement(0, 0.3 + progress * 0.1, 0);
        level.addFreshEntity(item);

        level.playSound(null, pos, ModSounds.VAULT_EJECT_ITEM.get(), SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * progress);
    }

    public void onTick(ServerLevel server, BlockPos pos, VaultConfig config, VaultSharedData sharedData, VaultServerData serverData) {
    }

    public enum LightLevel {
        HALF_LIT(6),
        LIT(12);

        final int value;

        LightLevel(int value) {
            this.value = value;
        }
    }
}
