package com.github.tacowasa059.snakeplayer.client.event;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class AutoMoveHandler {
    private AutoMoveHandler() {
    }

    public static void onClientTick() {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        IPlayerData playerData = (IPlayerData) player;
        if (!playerData.snakePlayer$getIsSnake() || player.isSpectator() || player.isCreative()) {
            return;
        }

        Vec3 currentVelocity = player.getDeltaMovement();
        Vec3 forward;
        List<PlayerPart> list = playerData.snakePlayer$getPlayerParts();

        if (list.isEmpty()) {
            forward = new Vec3(0, 0, 1);
        } else {
            Vec3 vec3 = player.position().subtract(list.get(0).position());
            vec3 = vec3.subtract(0, vec3.y, 0);
            forward = vec3.normalize();
            if (forward.lengthSqr() < 1.0E-6) {
                forward = new Vec3(0, 0, 1);
            }
        }

        double additionalSpeed = playerData.snakePlayer$getSnakeSpeed();
        if (!player.onGround() && !player.isInWater() && !player.isInLava()) {
            additionalSpeed *= 0.2f;
        }

        Vec3 rel = rotateVec3AroundY(new Vec3(forward.x, 0, forward.z), -35 * player.input.leftImpulse * Math.PI / 180f);
        player.setDeltaMovement(currentVelocity.add(rel.scale(additionalSpeed)));
    }

    public static Vec3 rotateVec3AroundY(Vec3 vec, double thetaRadians) {
        double cos = Math.cos(thetaRadians);
        double sin = Math.sin(thetaRadians);
        double x = vec.x * cos - vec.z * sin;
        double z = vec.x * sin + vec.z * cos;
        return new Vec3(x, vec.y, z);
    }
}
