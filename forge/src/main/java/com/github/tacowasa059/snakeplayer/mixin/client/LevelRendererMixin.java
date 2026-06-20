package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.client.event.ClientRenderListener;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Forge 54 では RenderLevelStageEvent が削除されたため、一人称時のボディ描画を
 * LevelRenderer.renderEntities の末尾（ワールド原点＝カメラ相対の PoseStack が使える時点）で行う。
 * Fabric は WorldRenderEvents.LAST、NeoForge は RenderLevelStageEvent を使用する。
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(
            method = "renderEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/Camera;Lnet/minecraft/client/DeltaTracker;Ljava/util/List;)V",
            at = @At("TAIL")
    )
    private void snakePlayer$renderFirstPersonBody(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, Camera camera, DeltaTracker deltaTracker, List<Entity> entities, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        AbstractClientPlayer player = minecraft.player;
        if (player == null || minecraft.options.getCameraType() != CameraType.FIRST_PERSON || player.isSpectator()) {
            return;
        }
        ClientRenderListener.renderFirstPerson(
                poseStack,
                bufferSource,
                player,
                camera.getPosition(),
                deltaTracker.getGameTimeDeltaPartialTick(false)
        );
    }
}
