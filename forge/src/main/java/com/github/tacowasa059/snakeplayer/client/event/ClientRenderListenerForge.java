package com.github.tacowasa059.snakeplayer.client.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

public final class ClientRenderListenerForge {
    // EventBus 7: キャンセル可能イベントは boolean を返すリスナー（true でキャンセル）。
    @SubscribeEvent
    public static boolean onRenderHand(RenderHandEvent event) {
        return ClientRenderListener.shouldCancelHand(Minecraft.getInstance().player);
    }

    // 第三者視点・一人称のボディ描画は共通 mixin
    // （LivingEntityRenderer.submit / LevelRenderer.submitEntities）で行う。
}
