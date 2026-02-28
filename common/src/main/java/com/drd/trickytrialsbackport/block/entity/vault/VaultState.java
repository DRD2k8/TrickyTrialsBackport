package com.drd.trickytrialsbackport.block.entity.vault;

import com.drd.trickytrialsbackport.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public enum VaultState implements StringRepresentable {
    INACTIVE("inactive", LightLevel.HALF_LIT) {
        @Override
        protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData shared, boolean ominous) {
            shared.setDisplayItem(ItemStack.EMPTY);
            level.levelEvent(3016, pos, ominous ? 1 : 0);
        }
    },

    ACTIVE("active", LightLevel.LIT) {
        @Override
        protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData shared, boolean ominous) {
            if (!shared.hasDisplayItem()) {
                VaultBlockEntity.Server.cycleDisplayItemFromLootTable(level, this, config, shared, pos);
            }
            level.levelEvent(3015, pos, ominous ? 1 : 0);
        }
    },

    UNLOCKING("unlocking", LightLevel.LIT) {
        @Override
        protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData shared, boolean ominous) {
            level.playSound(null, pos, ModSounds.VAULT_INSERT_ITEM.get(), SoundSource.BLOCKS);
        }
    },

    EJECTING("ejecting", LightLevel.LIT) {
        @Override
        protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData shared, boolean ominous) {
            level.playSound(null, pos, ModSounds.VAULT_OPEN_SHUTTER.get(), SoundSource.BLOCKS);
        }

        @Override
        protected void onExit(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData shared) {
            level.playSound(null, pos, ModSounds.VAULT_CLOSE_SHUTTER.get(), SoundSource.BLOCKS);
        }
    };

    private static final int UPDATE_CONNECTED_PLAYERS_TICK_RATE = 20;
    private static final int DELAY_BETWEEN_EJECTIONS_TICKS = 20;
    private static final int DELAY_AFTER_LAST_EJECTION_TICKS = 20;
    private static final int DELAY_BEFORE_FIRST_EJECTION_TICKS = 20;

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

    public VaultState tickAndGetNext(ServerLevel level,
                                     BlockPos pos,
                                     VaultConfig config,
                                     VaultServerData server,
                                     VaultSharedData shared) {

        return switch (this) {

            case INACTIVE -> updateStateForConnectedPlayers(
                    level, pos, config, server, shared, config.activationRange()
            );

            case ACTIVE -> updateStateForConnectedPlayers(
                    level, pos, config, server, shared, config.deactivationRange()
            );

            case UNLOCKING -> {
                server.pauseStateUpdatingUntil(level.getGameTime() + DELAY_BEFORE_FIRST_EJECTION_TICKS);
                yield EJECTING;
            }

            case EJECTING -> {
                if (server.getItemsToEject().isEmpty()) {
                    server.markEjectionFinished();
                    yield updateStateForConnectedPlayers(
                            level, pos, config, server, shared, config.deactivationRange()
                    );
                } else {
                    float progress = server.ejectionProgress();
                    ItemStack next = server.popNextItemToEject();

                    this.ejectResultItem(level, pos, next, progress);

                    shared.setDisplayItem(server.getNextItemToEject());

                    boolean last = server.getItemsToEject().isEmpty();
                    int delay = last ? DELAY_AFTER_LAST_EJECTION_TICKS : DELAY_BETWEEN_EJECTIONS_TICKS;

                    server.pauseStateUpdatingUntil(level.getGameTime() + delay);

                    yield EJECTING;
                }
            }
        };
    }

    private static VaultState updateStateForConnectedPlayers(ServerLevel level,
                                                             BlockPos pos,
                                                             VaultConfig config,
                                                             VaultServerData server,
                                                             VaultSharedData shared,
                                                             double range) {

        shared.updateConnectedPlayersWithinRange(level, pos, server, config, range);
        server.pauseStateUpdatingUntil(level.getGameTime() + UPDATE_CONNECTED_PLAYERS_TICK_RATE);

        return shared.hasConnectedPlayers() ? ACTIVE : INACTIVE;
    }

    public void onTransition(ServerLevel level,
                             BlockPos pos,
                             VaultState next,
                             VaultConfig config,
                             VaultSharedData shared,
                             boolean ominous) {

        this.onExit(level, pos, config, shared);
        next.onEnter(level, pos, config, shared, ominous);
    }

    protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData shared, boolean ominous) {}
    protected void onExit(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData shared) {}

    private void ejectResultItem(ServerLevel level,
                                 BlockPos pos,
                                 ItemStack stack,
                                 float progress) {

        DefaultDispenseItemBehavior.spawnItem(
                level,
                stack,
                2,
                Direction.UP,
                Vec3.atBottomCenterOf(pos).relative(Direction.UP, 1.2)
        );

        level.levelEvent(3017, pos, 0);
        level.playSound(null, pos, ModSounds.VAULT_EJECT_ITEM.get(), SoundSource.BLOCKS,
                1.0F, 0.8F + 0.4F * progress);
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
