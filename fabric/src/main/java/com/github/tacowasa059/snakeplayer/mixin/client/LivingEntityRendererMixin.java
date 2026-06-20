package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.client.duck.ISnakeRenderState;
// 共通モジュール（common）の duck インターフェースを使用する。
import com.github.tacowasa059.snakeplayer.client.event.ClientRenderListener;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    // レンダーステートにスネークプレイヤー情報があれば、頭の球体とボディを描画して通常モデルを差し替える。
    @Inject(
            method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void snakePlayer$render(LivingEntityRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (!(state instanceof ISnakeRenderState snakeState)) {
            return;
        }
        AbstractClientPlayer player = snakeState.snakePlayer$getPlayer();
        if (player == null) {
            return;
        }
        if (ClientRenderListener.renderPlayer(player, poseStack, bufferSource, snakeState.snakePlayer$getPartialTick(), packedLight)) {
            ci.cancel();
        }
    }
}
