package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.client.event.ClientRenderListener;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    // 26.2 で renderHandsWithItems は submitHandsWithItems へ改名（引数は同じ）。
    @Inject(method = "submitHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/player/LocalPlayer;I)V", at = @At("HEAD"), cancellable = true)
    private void snakePlayer$submitHandsWithItems(float partialTicks, PoseStack poseStack, SubmitNodeCollector nodeCollector, LocalPlayer player, int packedLight, CallbackInfo ci) {
        if (ClientRenderListener.shouldCancelHand(player)) {
            ci.cancel();
        }
    }
}
