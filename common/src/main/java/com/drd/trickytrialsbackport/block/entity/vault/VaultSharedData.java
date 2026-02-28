package com.drd.trickytrialsbackport.block.entity.vault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class VaultSharedData {
    public static final String TAG_NAME = "shared_data";

    public static final Codec<VaultSharedData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.CODEC
                            .optionalFieldOf("display_item", ItemStack.EMPTY)
                            .forGetter(d -> d.displayItem),

                    Codec.list(UUIDUtil.CODEC)
                            .optionalFieldOf("connected_players", List.of())
                            .xmap(list -> {
                                Set<UUID> set = new ObjectLinkedOpenHashSet<>();
                                set.addAll(list);
                                return set;
                            }, set -> new ArrayList<>(set))
                            .forGetter(d -> d.connectedPlayers),

                    Codec.DOUBLE
                            .optionalFieldOf("connected_particles_range", VaultConfig.DEFAULT.deactivationRange())
                            .forGetter(d -> d.connectedParticlesRange)
            ).apply(instance, VaultSharedData::new)
    );

    private ItemStack displayItem = ItemStack.EMPTY;
    private Set<UUID> connectedPlayers = new ObjectLinkedOpenHashSet<>();
    private double connectedParticlesRange = VaultConfig.DEFAULT.deactivationRange();
    boolean isDirty;

    VaultSharedData(ItemStack displayItem,
                    Set<UUID> connectedPlayers,
                    double connectedParticlesRange) {
        this.displayItem = displayItem;
        this.connectedPlayers.addAll(connectedPlayers);
        this.connectedParticlesRange = connectedParticlesRange;
    }

    public VaultSharedData() {
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public boolean hasDisplayItem() {
        return !this.displayItem.isEmpty();
    }

    public void setDisplayItem(ItemStack stack) {
        if (!ItemStack.matches(this.displayItem, stack)) {
            this.displayItem = stack.copy();
            this.markDirty();
        }
    }

    boolean hasConnectedPlayers() {
        return !this.connectedPlayers.isEmpty();
    }

    Set<UUID> getConnectedPlayers() {
        return this.connectedPlayers;
    }

    double connectedParticlesRange() {
        return this.connectedParticlesRange;
    }

    void updateConnectedPlayersWithinRange(ServerLevel level,
                                           BlockPos pos,
                                           VaultServerData serverData,
                                           VaultConfig config,
                                           double range) {

        Set<UUID> newSet = config.playerDetector()
                .detect(level, config.entitySelector(), pos, range, false)
                .stream()
                .filter(p -> !serverData.getRewardedPlayers().contains(p))
                .collect(Collectors.toSet());

        if (!this.connectedPlayers.equals(newSet)) {
            this.connectedPlayers = newSet;
            this.markDirty();
        }
    }

    private void markDirty() {
        this.isDirty = true;
    }

    void set(VaultSharedData other) {
        this.displayItem = other.displayItem;
        this.connectedPlayers = other.connectedPlayers;
        this.connectedParticlesRange = other.connectedParticlesRange;
    }
}
