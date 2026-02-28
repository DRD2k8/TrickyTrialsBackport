package com.drd.trickytrialsbackport.block.entity.vault;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

public class PlayerDetector {

    public static final PlayerDetector INCLUDING_CREATIVE_PLAYERS =
            new PlayerDetector(false);

    public static final PlayerDetector EXCLUDING_CREATIVE_PLAYERS =
            new PlayerDetector(true);

    private final boolean ignoreCreative;

    public PlayerDetector(boolean ignoreCreative) {
        this.ignoreCreative = ignoreCreative;
    }

    public boolean isValidPlayer(Player player) {
        if (ignoreCreative && player.isCreative()) {
            return false;
        }
        return true;
    }

    public List<UUID> detect(ServerLevel level,
                             EntitySelector selector,
                             BlockPos pos,
                             double range,
                             boolean includeSpectators) {

        List<Player> players = selector.select(level, pos, range, this);

        return players.stream()
                .filter(p -> includeSpectators || !p.isSpectator())
                .map(Player::getUUID)
                .toList();
    }

    public enum EntitySelector {
        SELECT_FROM_LEVEL,
        SELECT_FROM_NEARBY;

        public List<Player> select(ServerLevel level,
                                   BlockPos pos,
                                   double range,
                                   PlayerDetector detector) {

            switch (this) {
                case SELECT_FROM_LEVEL: {
                    return level.players().stream()
                            .filter(detector::isValidPlayer)
                            .filter(p -> p.distanceToSqr(
                                    pos.getX() + 0.5,
                                    pos.getY() + 0.5,
                                    pos.getZ() + 0.5
                            ) <= range * range)
                            .map(p -> (Player) p)
                            .toList();
                }

                case SELECT_FROM_NEARBY: {
                    AABB box = new AABB(pos).inflate(range);
                    return level.getEntitiesOfClass(Player.class, box).stream()
                            .filter(detector::isValidPlayer)
                            .toList();
                }
            }

            return List.of();
        }
    }
}
