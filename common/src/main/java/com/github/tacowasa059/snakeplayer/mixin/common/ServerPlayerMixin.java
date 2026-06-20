package com.github.tacowasa059.snakeplayer.mixin.common;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method="setExperienceLevels",at=@At("TAIL"))
    public void setExperienceLevels(int p_9175_, CallbackInfo ci){
        snakePlayer$setExp();
    }

    @Inject(method="restoreFrom", at=@At("TAIL"))
    public void restoreFrom(ServerPlayer p_9016_, boolean p_9017_, CallbackInfo ci){
        snakePlayer$setExp();
    }

    @Unique
    private void snakePlayer$setExp() {
        ServerPlayer player = (ServerPlayer) (Object)this;
        if (!(player instanceof IPlayerData playerData)) {
            return;
        }
        playerData.snakePlayer$setSnakeExperience(player.experienceLevel);
    }

}
