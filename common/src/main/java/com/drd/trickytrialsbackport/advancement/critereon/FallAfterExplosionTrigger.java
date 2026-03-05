package com.drd.trickytrialsbackport.advancement.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FallAfterExplosionTrigger extends SimpleCriterionTrigger<FallAfterExplosionTrigger.TriggerInstance> {
    public static final ResourceLocation ID = new ResourceLocation("fall_after_explosion");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer player, Vec3 startPos, @Nullable Entity causeEntity) {
        Vec3 endPos = player.position();

        this.trigger(player, inst -> inst.matches(
                player,
                startPos,
                endPos,
                causeEntity
        ));
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json,
                                             ContextAwarePredicate playerPredicate,
                                             DeserializationContext context) {

        LocationPredicate startPos = LocationPredicate.fromJson(json.get("start_position"));
        DistancePredicate distance = DistancePredicate.fromJson(json.get("distance"));
        EntityPredicate cause = EntityPredicate.fromJson(json.get("cause"));

        return new TriggerInstance(playerPredicate, startPos, distance, cause);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final LocationPredicate startPosition;
        private final DistancePredicate distance;
        private final EntityPredicate cause;

        public TriggerInstance(ContextAwarePredicate player,
                               LocationPredicate startPosition,
                               DistancePredicate distance,
                               EntityPredicate cause) {
            super(FallAfterExplosionTrigger.ID, player);
            this.startPosition = startPosition;
            this.distance = distance;
            this.cause = cause;
        }

        public boolean matches(ServerPlayer player,
                               Vec3 start,
                               Vec3 end,
                               @Nullable Entity causeEntity) {
            if (!this.startPosition.matches(player.serverLevel(), start.x, start.y, start.z)) {
                return false;
            }

            if (!this.distance.matches(start.x, start.y, start.z, end.x, end.y, end.z)) {
                return false;
            }

            if (!this.cause.matches(player, player)) {
                return false;
            }

            return true;
        }
    }
}
