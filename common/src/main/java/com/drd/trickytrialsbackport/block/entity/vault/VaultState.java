package com.drd.trickytrialsbackport.block.entity.vault;

import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

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
            serverData.tickEjection();

            if (serverData.getCurrentEjectingItem().isEmpty()) {

                ItemStack next = serverData.popNextItemToEject();

                if (next == null) {
                    VaultBlockEntity be = (VaultBlockEntity) level.getBlockEntity(pos);
                    if (be != null) {
                        be.setState(VaultState.INACTIVE);
                    }
                }
            }
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

            case ACTIVE -> updateStateForConnectedPlayers(level, pos, config, serverData, sharedData, config.deactivationRange());

            case UNLOCKING -> {
                serverData.pauseStateUpdatingUntil(level.getGameTime() + 20L);
                yield EJECTING;
            }

            case EJECTING -> {
                if (serverData.getItemsToEject().isEmpty()) {
                    serverData.markEjectionFinished();
                    yield updateStateForConnectedPlayers(level, pos, config, serverData, sharedData, config.deactivationRange());
                } else {
                    float progress = serverData.ejectionProgress();

                    ejectResultItem(level, pos, serverData.popNextItemToEject(), progress);

                    sharedData.setDisplayItem(serverData.getNextItemToEject());

                    boolean last = serverData.getItemsToEject().isEmpty();
                    int delay = last ? 20 : 20;

                    serverData.pauseStateUpdatingUntil(level.getGameTime() + delay);
                    yield EJECTING;
                }
            }
        };
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
