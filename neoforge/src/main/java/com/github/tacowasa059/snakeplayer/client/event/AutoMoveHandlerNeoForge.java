package com.github.tacowasa059.snakeplayer.client.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class AutoMoveHandlerNeoForge {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        AutoMoveHandler.onClientTick();
    }
}
