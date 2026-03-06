package com.drd.trickytrialsbackport.fabric.server;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WindChargeTracker {
    private static final Map<UUID, ServerPlayerHooks.FallData> fallData = new HashMap<>();
    private static final Map<UUID, ServerPlayerHooks.LaunchData> launchData = new HashMap<>();

    public static void init() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                var fd = fallData.computeIfAbsent(player.getUUID(), id -> new ServerPlayerHooks.FallData());
                var ld = launchData.computeIfAbsent(player.getUUID(), id -> new ServerPlayerHooks.LaunchData());

                fallData.put(player.getUUID(), ServerPlayerHooks.trackStartFallingPosition(player, fd));
                launchData.put(player.getUUID(), ServerPlayerHooks.detectWindChargeLaunch(player, ld));
            }
        });
    }
}
