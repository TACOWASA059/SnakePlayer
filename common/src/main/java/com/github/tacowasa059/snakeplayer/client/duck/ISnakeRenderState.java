package com.github.tacowasa059.snakeplayer.client.duck;

import net.minecraft.client.player.AbstractClientPlayer;

/**
 * 1.21.2 で導入されたレンダーステート方式に対応するためのダックインターフェース。
 * extractRenderState の時点で生きているプレイヤー参照と partialTick を保持し、
 * 描画段階（Fabric の mixin / Forge・NeoForge の RenderPlayerEvent）で取り出して
 * 通常モデルを球体描画へ差し替える。
 */
public interface ISnakeRenderState {
    void snakePlayer$setPlayer(AbstractClientPlayer player);

    AbstractClientPlayer snakePlayer$getPlayer();

    void snakePlayer$setPartialTick(float partialTick);

    float snakePlayer$getPartialTick();
}
