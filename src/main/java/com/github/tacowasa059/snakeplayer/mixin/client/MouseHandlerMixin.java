package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.client.util.ClientPlayerUtils;
import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Unique
    float snakePlayer$lastTime = 0;
    @Redirect(
            method = "turnPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
            )
    )
    private void sphericalPlayerMod$redirectTurn(LocalPlayer player, double yawDelta, double pitchDelta) {
        double time = Blaze3D.getTime();
        double deltaTicks = (time - snakePlayer$lastTime) * 20.0;
        deltaTicks = Mth.clamp(deltaTicks, 0, 0.3f);
        snakePlayer$lastTime = (float) time;

        IPlayerData playerData = (IPlayerData) player;
        if(!playerData.snakePlayer$getIsSnake() || player.isSpectator() || player.isCreative()){
            ClientPlayerUtils.resetCameraYRot();
            player.turn(yawDelta, pitchDelta);
            return;
        }

        float value = (float)yawDelta * 0.15F;
        ClientPlayerUtils.addCameraYRot(player, value);

        player.turn(0, pitchDelta);

        Vec3 newVelocity = player.getDeltaMovement();
        Vec2 vTarget = new Vec2((float) newVelocity.x, (float) newVelocity.z).normalized();

        float currentYaw = player.getYRot();
        float yawRad = (float) (currentYaw * (Math.PI / 180f));
        Vec2 vCurrent = new Vec2((float) -Math.sin(yawRad), (float) Math.cos(yawRad));


        float dot = vCurrent.dot(vTarget);
        float det = vCurrent.x * vTarget.y - vCurrent.y * vTarget.x;
        float angleRad = (float) Math.atan2(det, dot);

        float diffDeg = -angleRad * (180f / (float) Math.PI);
        float f1 = - (float) (diffDeg * deltaTicks * 0.5);

        player.yRotO += f1;
        player.setYRot(currentYaw + f1);
    }
}
