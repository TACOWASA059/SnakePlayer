package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.client.event.ClientRenderListener;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 一人称時のボディ描画を全ローダー共通で行う。
 * submitEntities の末尾（poseStack はワールド原点＝カメラ相対、SubmitNodeCollector が利用可能）で submit する。
 * 旧来のローダー個別フック（Fabric: WorldRenderEvents / NeoForge: RenderLevelStageEvent / Forge: 専用 mixin）を置き換える。
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(
            method = "submitEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
            at = @At("TAIL")
    )
    private void snakePlayer$submitFirstPersonBody(PoseStack poseStack, LevelRenderState renderState, SubmitNodeCollector nodeCollector, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        AbstractClientPlayer player = minecraft.player;
        if (player == null || minecraft.options.getCameraType() != CameraType.FIRST_PERSON || player.isSpectator()) {
            return;
        }
        float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        ClientRenderListener.renderFirstPerson(poseStack, nodeCollector, player, renderState.cameraRenderState.pos, partialTick);
    }
}
