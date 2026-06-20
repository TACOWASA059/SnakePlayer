package com.github.tacowasa059.snakeplayer;

import com.github.tacowasa059.snakeplayer.client.event.AutoMoveHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class SnakePlayerFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> AutoMoveHandler.onClientTick());
        // 第三者視点・一人称のボディ描画は共通 mixin
        // （LivingEntityRenderer.submit / LevelRenderer.submitEntities）で行う。
    }
}
