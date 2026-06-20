package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.client.duck.ISnakeRenderState;
import com.github.tacowasa059.snakeplayer.client.event.ClientRenderListener;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.6 で描画が submit パイプライン化されたため、render ではなく submit へ注入する。
 * レンダーステートにスネークプレイヤー情報があれば、頭の球体とボディを submit して通常モデルを差し替える。
 * 全ローダー共通で適用する（Forge/NeoForge も RenderPlayerEvent ではなくこの mixin で第三者視点を差し替える）。
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void snakePlayer$submit(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (!(state instanceof ISnakeRenderState snakeState)) {
            return;
        }
        AbstractClientPlayer player = snakeState.snakePlayer$getPlayer();
        if (player == null) {
            return;
        }
        if (ClientRenderListener.renderPlayer(player, poseStack, collector, snakeState.snakePlayer$getPartialTick(), state.lightCoords)) {
            ci.cancel();
        }
    }
}
