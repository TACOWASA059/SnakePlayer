package com.github.tacowasa059.snakeplayer.mixin;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow
    protected abstract boolean isControlledCamera();

    /**
     * 前進と後退を無効化
     * @param ci
     */
    @Inject(method = "serverAiStep", at = @At("TAIL"))
    private void disableBackwardMovement(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object)this;
        if (this.isControlledCamera()) {
            IPlayerData playerData = (IPlayerData)player;
            if(!playerData.getIsSnake() || player.isSpectator() || player.isCreative()) return;

            player.zza = 0;
        }
    }

    /**
     * 蛇の状態では走れない
     * @param cir
     */
    @Inject(method = "canStartSprinting", at = @At("RETURN"), cancellable = true)
    private void modifyCanStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            LocalPlayer player = (LocalPlayer) (Object) this;
            IPlayerData playerData = (IPlayerData) player;
            if(!playerData.getIsSnake() || player.isSpectator() || player.isCreative()) return;
            cir.setReturnValue(false);
        }
    }
}
