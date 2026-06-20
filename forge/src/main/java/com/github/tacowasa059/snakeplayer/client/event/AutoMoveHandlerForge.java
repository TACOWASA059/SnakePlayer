package com.github.tacowasa059.snakeplayer.client.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

public final class AutoMoveHandlerForge {
    // EventBus 7 / Forge 61: TickEvent は Pre/Post レコードへ分割された（Post = 旧 END フェーズ）。
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        AutoMoveHandler.onClientTick();
    }
}
