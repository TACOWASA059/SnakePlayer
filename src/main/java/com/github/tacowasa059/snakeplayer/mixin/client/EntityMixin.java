package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {


    @Inject(method="shouldRender",at=@At("HEAD"),cancellable = true)
    public void render(double p_20296_, double p_20297_, double p_20298_, CallbackInfoReturnable<Boolean> cir){
        Entity entity = (Entity)(Object)this;
        if(entity instanceof Player player){
            IPlayerData playerData = (IPlayerData)player;
            if(playerData.snakePlayer$getIsSnake()){
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
