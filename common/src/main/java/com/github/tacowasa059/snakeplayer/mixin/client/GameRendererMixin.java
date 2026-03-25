package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "bobView",at = @At("HEAD"),cancellable = true)
    private void bobView(PoseStack p_109139_, float p_109140_, CallbackInfo ci){
        Player player = Minecraft.getInstance().player;
        if(player==null)return;
        IPlayerData playerData = (IPlayerData)player;
        if(!playerData.snakePlayer$getIsSnake() || player.isSpectator() || player.isCreative()) return;

        ci.cancel();
    }
}
