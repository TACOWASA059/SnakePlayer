package com.github.tacowasa059.snakeplayer.mixin;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method="setExperienceLevels",at=@At("TAIL"))
    public void setExperienceLevels(int p_9175_, CallbackInfo ci){
        setExp();
    }

    @Inject(method="restoreFrom", at=@At("TAIL"))
    public void restoreFrom(ServerPlayer p_9016_, boolean p_9017_, CallbackInfo ci){
        setExp();
    }

    private void setExp() {
        ServerPlayer player = (ServerPlayer) (Object)this;
        IPlayerData playerData = (IPlayerData) player;
        playerData.setSnakeExperience(player.experienceLevel);
    }

}
