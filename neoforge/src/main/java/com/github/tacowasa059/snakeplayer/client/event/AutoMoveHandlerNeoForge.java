package com.github.tacowasa059.snakeplayer.client.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.TickEvent;

public final class AutoMoveHandlerNeoForge {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        AutoMoveHandler.onClientTick();
    }
}
