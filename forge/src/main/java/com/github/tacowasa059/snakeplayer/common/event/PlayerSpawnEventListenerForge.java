package com.github.tacowasa059.snakeplayer.common.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

public final class PlayerSpawnEventListenerForge {
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
    public void onServerTick(TickEvent.ServerTickEvent.Post event) {
        PlayerSpawnEventListener.onServerTick(event.server());
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        PlayerSpawnEventListener.onLivingDeath(event.getEntity());
    }
}
