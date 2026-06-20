package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.client.duck.ISnakeRenderState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * extractRenderState の時点で生きているプレイヤー参照と partialTick をレンダーステートへ退避する。
 * 退避した情報は描画段階でスネークプレイヤーの球体描画へ差し替えるために使われる。
 */
@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("TAIL"))
    private void snakePlayer$extractRenderState(AbstractClientPlayer player, PlayerRenderState state, float partialTick, CallbackInfo ci) {
        ISnakeRenderState snakeState = (ISnakeRenderState) state;
        snakeState.snakePlayer$setPlayer(player);
        snakeState.snakePlayer$setPartialTick(partialTick);
    }
}
