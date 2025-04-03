package com.github.tacowasa059.snakeplayer.mixin;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    @Inject(method="shouldRender",at=@At("HEAD"),cancellable = true)
    public void render(T p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_, CallbackInfoReturnable<Boolean> cir){
        if(p_114491_ instanceof AbstractClientPlayer player){
            IPlayerData playerData = (IPlayerData) player;
            if(playerData.snakePlayer$getIsSnake()){
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
        if(p_114491_ instanceof PlayerPart){
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
