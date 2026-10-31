package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.client.event.ClientRenderListener;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class FirstPersonHandsAndItemsRendererMixin {
    @Inject(method = "submitHandsWithItems", at = @At("HEAD"), cancellable = true)
    private void snakePlayer$submitHandsWithItems(float partialTicks, PoseStack poseStack, SubmitNodeCollector nodeCollector, PlayerRenderState playerRenderState, FirstPersonHandsAndItemsRenderState firstPersonHandsAndItemsRenderState, CallbackInfo ci) {
        if (ClientRenderListener.shouldCancelHand(Minecraft.getInstance().player)) {
            ci.cancel();
        }
    }
}
