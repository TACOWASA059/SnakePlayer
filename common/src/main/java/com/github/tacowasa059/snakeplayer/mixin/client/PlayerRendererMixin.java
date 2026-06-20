package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.client.duck.ISnakeRenderState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * extractRenderState の時点で生きているプレイヤー参照と partialTick をレンダーステートへ退避する。
 * 1.21.6 以降 PlayerRenderer は AvatarRenderer、PlayerRenderState は AvatarRenderState へ改名された。
 * extractRenderState の第1引数は総称型 {@code AvatarlikeEntity extends Avatar} のため消去後は Avatar 型。
 */
@Mixin(AvatarRenderer.class)
public class PlayerRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void snakePlayer$extractRenderState(Avatar entity, AvatarRenderState state, float partialTick, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayer player) {
            ISnakeRenderState snakeState = (ISnakeRenderState) state;
            snakeState.snakePlayer$setPlayer(player);
            snakeState.snakePlayer$setPartialTick(partialTick);
        }
    }
}
