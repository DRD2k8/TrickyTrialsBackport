package com.drd.trickytrialsbackport.block.entity.vault;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VaultSharedData {
    private ItemStack displayItem = ItemStack.EMPTY;
    final Set<UUID> connectedPlayers = new HashSet<>();
    private double connectedParticlesRange = 0.0D;

    public void load(CompoundTag tag) {
        displayItem = ItemStack.EMPTY;
        connectedPlayers.clear();
        connectedParticlesRange = 0.0D;

        if (tag.contains("display_item", Tag.TAG_COMPOUND)) {
            displayItem = ItemStack.of(tag.getCompound("display_item"));
        }

        if (tag.contains("connected_players", Tag.TAG_LIST)) {
            ListTag list = tag.getList("connected_players", Tag.TAG_INT_ARRAY);
            for (Tag t : list) {
                if (t instanceof IntArrayTag arr) {
                    connectedPlayers.add(NbtUtils.loadUUID(arr));
                }
            }
        }

        if (tag.contains("connected_particles_range", Tag.TAG_DOUBLE)) {
            connectedParticlesRange = tag.getDouble("connected_particles_range");
        }
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        if (!displayItem.isEmpty()) {
            tag.put("display_item", displayItem.save(new CompoundTag()));
        }

        ListTag players = new ListTag();
        for (UUID id : connectedPlayers) {
            players.add(NbtUtils.createUUID(id));
        }
        tag.put("connected_players", players);

        tag.putDouble("connected_particles_range", connectedParticlesRange);

        return tag;
    }

    public void setDisplayItem(ItemStack stack) {
        this.displayItem = stack.copy();
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public boolean hasDisplayItem() {
        return !displayItem.isEmpty();
    }

    public void updateConnectedPlayersWithinRange(ServerLevel level, BlockPos pos, VaultServerData serverData, VaultConfig config, double range) {
        connectedPlayers.clear();

        AABB box = new AABB(pos).inflate(range);
        List<Player> players = level.getEntitiesOfClass(Player.class, box, p -> !p.isSpectator());

        for (Player p : players) {
            connectedPlayers.add(p.getUUID());
        }

        connectedParticlesRange = range;
    }

    public boolean hasConnectedPlayers() {
        return !connectedPlayers.isEmpty();
    }
}
