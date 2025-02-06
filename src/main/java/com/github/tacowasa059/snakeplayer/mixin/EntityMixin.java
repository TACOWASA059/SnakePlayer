package com.github.tacowasa059.snakeplayer.mixin;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow
    private Entity vehicle;
    @Inject(method="shouldRender",at=@At("HEAD"),cancellable = true)
    public void render(double p_20296_, double p_20297_, double p_20298_, CallbackInfoReturnable<Boolean> cir){
        Entity entity = (Entity)(Object)this;
        if(entity instanceof Player player){
            IPlayerData playerData = (IPlayerData)player;
            if(playerData.getIsSnake()){
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Inject(method = "turn",at=@At("HEAD"),cancellable = true)
    public void turn(double p_19885_, double p_19886_, CallbackInfo ci){

        Player player = Minecraft.getInstance().player;
        if(player==null)return;
        IPlayerData playerData = (IPlayerData)player;
        if(!playerData.getIsSnake() || player.isSpectator() || player.isCreative()) return;

        Entity entity = (Entity) (Object)this;

        if(entity == player){
            float f = (float)p_19886_ * 0.15F;
            float f1 = (float)p_19885_ * 0.15F;
            int exp = player.experienceLevel+1;

            float factor = (0.75f+ 4.25f/exp);
            f1 = Mth.clamp(f1, -factor, factor);

            entity.setXRot(entity.getXRot() + f);
            entity.setYRot(entity.getYRot() + f1);
            entity.setXRot(Mth.clamp(entity.getXRot(), -90.0F, 90.0F));
            entity.xRotO += f;
            entity.yRotO += f1;
            entity.xRotO = Mth.clamp(entity.xRotO, -90.0F, 90.0F);
            if (this.vehicle != null) {
                this.vehicle.onPassengerTurned(entity);
            }
        }

        ci.cancel();
    }
}
