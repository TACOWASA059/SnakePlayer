package com.github.tacowasa059.snakeplayer.common.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class PlayerSpawnEventListenerNeoForge {
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        PlayerSpawnEventListener.onPlayerClone(event.getOriginal(), event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        PlayerSpawnEventListener.onPlayerChangeDimension(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerSpawnEventListener.onPlayerRespawn(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerSpawnEventListener.onPlayerLogin(event.getEntity());
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        PlayerSpawnEventListener.onServerTick(event.getServer());
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        PlayerSpawnEventListener.onLivingDeath(event.getEntity());
    }
}
