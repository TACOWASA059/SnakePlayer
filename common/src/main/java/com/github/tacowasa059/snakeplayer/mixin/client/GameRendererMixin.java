package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    // bobView の引数は版ごとに変わる（26.1.2 では (CameraRenderState, PoseStack)）。
    // 引数は使わないため、CallbackInfo のみのハンドラにしてシグネチャ変更に強くする。
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void bobView(CallbackInfo ci){
        Player player = Minecraft.getInstance().player;
        if(player==null)return;
        IPlayerData playerData = (IPlayerData)player;
        if(!playerData.snakePlayer$getIsSnake() || player.isSpectator() || player.isCreative()) return;

        ci.cancel();
    }
}
