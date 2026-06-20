package com.github.tacowasa059.snakeplayer.client.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;

/**
 * クライアント専用イベントの登録。dist == CLIENT のときだけ呼ばれる（サーバーでは本クラスをロードしない）。
 * EventBus 7 では単一リスナーのクラスを migration helper で登録できないため、各イベントの BUS へ直接 addListener する。
 */
public final class ForgeClientEvents {
    private ForgeClientEvents() {
    }

    public static void register() {
        // 旧 END フェーズ相当の Post で自動移動を駆動する。
        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> AutoMoveHandler.onClientTick());

        // キャンセル可能イベントは boolean を返す Predicate リスナー（true でキャンセル）。
        // ブロック＋return にして Consumer ではなく Predicate へ確実に解決させる。
        RenderHandEvent.BUS.addListener((RenderHandEvent event) -> {
            return ClientRenderListener.shouldCancelHand(Minecraft.getInstance().player);
        });
    }
}
