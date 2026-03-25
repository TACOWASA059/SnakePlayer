package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    public LocalPlayerMixin(ClientLevel p_250460_, GameProfile p_249912_) {
        super(p_250460_, p_249912_);
    }

    @Shadow
    protected abstract boolean isControlledCamera();

    @Inject(method = "serverAiStep", at = @At("TAIL"))
    private void disableBackwardMovement(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object)this;
        if (this.isControlledCamera()) {
            IPlayerData playerData = (IPlayerData)player;
            if(!playerData.snakePlayer$getIsSnake() || player.isSpectator() || player.isCreative()) return;

            player.zza = 0;
            player.xxa = 0;
        }
    }
    @Inject(method = "canStartSprinting", at = @At("RETURN"), cancellable = true)
    private void modifyCanStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            LocalPlayer player = (LocalPlayer) (Object) this;
            IPlayerData playerData = (IPlayerData) player;
            if(!playerData.snakePlayer$getIsSnake() || player.isSpectator() || player.isCreative()) return;
            cir.setReturnValue(false);
        }
    }
}
