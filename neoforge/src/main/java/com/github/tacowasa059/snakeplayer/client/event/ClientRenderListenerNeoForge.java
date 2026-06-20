package com.github.tacowasa059.snakeplayer.client.event;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;

public final class ClientRenderListenerNeoForge {
    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (ClientRenderListener.shouldCancelHand(Minecraft.getInstance().player)) {
            event.setCanceled(true);
        }
    }

    // 第三者視点・一人称のボディ描画は共通 mixin
    // （LivingEntityRenderer.submit / LevelRenderer.submitEntities）で行う。
}
