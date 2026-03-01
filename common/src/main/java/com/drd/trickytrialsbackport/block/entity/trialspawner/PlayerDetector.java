package com.drd.trickytrialsbackport.block.entity.trialspawner;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public interface PlayerDetector {
    PlayerDetector NO_CREATIVE_PLAYERS = (serverLevel, selector, pos, range, requireLOS) ->
            selector.getPlayers(
                            serverLevel,
                            player -> player.blockPosition().closerThan(pos, range)
                                    && !player.isCreative()
                                    && !player.isSpectator()
                    )
                    .stream()
                    .filter(player -> !requireLOS || inLineOfSight(serverLevel, pos.getCenter(), player.getEyePosition()))
                    .map(Entity::getUUID)
                    .toList();

    PlayerDetector INCLUDING_CREATIVE_PLAYERS = (serverLevel, selector, pos, range, requireLOS) ->
            selector.getPlayers(
                            serverLevel,
                            player -> player.blockPosition().closerThan(pos, range)
                                    && !player.isSpectator()
                    )
                    .stream()
                    .filter(player -> !requireLOS || inLineOfSight(serverLevel, pos.getCenter(), player.getEyePosition()))
                    .map(Entity::getUUID)
                    .toList();

    PlayerDetector SHEEP = (serverLevel, selector, pos, range, requireLOS) -> {
        AABB aabb = new AABB(pos).inflate(range);
        return selector.getEntities(serverLevel, EntityType.SHEEP, aabb, LivingEntity::isAlive)
                .stream()
                .filter(entity -> !requireLOS || inLineOfSight(serverLevel, pos.getCenter(), entity.getEyePosition()))
                .map(Entity::getUUID)
                .toList();
    };

    List<UUID> detect(ServerLevel level,
                      PlayerDetector.EntitySelector selector,
                      BlockPos pos,
                      double range,
                      boolean requireLineOfSight);

    private static boolean inLineOfSight(Level level, Vec3 targetPos, Vec3 eyePos) {
        BlockHitResult hit = level.clip(
                new ClipContext(
                        eyePos,
                        targetPos,
                        ClipContext.Block.VISUAL,
                        ClipContext.Fluid.NONE,
                        null
                )
        );
        return hit.getBlockPos().equals(BlockPos.containing(targetPos))
                || hit.getType() == HitResult.Type.MISS;
    }

    interface EntitySelector {

        PlayerDetector.EntitySelector SELECT_FROM_LEVEL = new PlayerDetector.EntitySelector() {
            @Override
            public List<ServerPlayer> getPlayers(ServerLevel level, Predicate<? super Player> predicate) {
                return level.getPlayers(predicate);
            }

            @Override
            public <T extends Entity> List<T> getEntities(ServerLevel level,
                                                          EntityTypeTest<Entity, T> typeTest,
                                                          AABB box,
                                                          Predicate<? super T> predicate) {
                return level.getEntities(typeTest, box, predicate);
            }
        };

        List<? extends Player> getPlayers(ServerLevel level, Predicate<? super Player> predicate);

        <T extends Entity> List<T> getEntities(ServerLevel level,
                                               EntityTypeTest<Entity, T> typeTest,
                                               AABB box,
                                               Predicate<? super T> predicate);

        static PlayerDetector.EntitySelector onlySelectPlayer(Player player) {
            return onlySelectPlayers(List.of(player));
        }

        static PlayerDetector.EntitySelector onlySelectPlayers(final List<Player> players) {
            return new PlayerDetector.EntitySelector() {
                @Override
                public List<Player> getPlayers(ServerLevel level, Predicate<? super Player> predicate) {
                    return players.stream().filter(predicate).toList();
                }

                @Override
                public <T extends Entity> List<T> getEntities(ServerLevel level,
                                                              EntityTypeTest<Entity, T> typeTest,
                                                              AABB box,
                                                              Predicate<? super T> predicate) {
                    return players.stream()
                            .map(typeTest::tryCast)
                            .filter(Objects::nonNull)
                            .filter(predicate)
                            .toList();
                }
            };
        }
    }
}
