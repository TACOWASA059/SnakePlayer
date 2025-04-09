package com.github.tacowasa059.snakeplayer.client.util;

import net.minecraft.client.player.RemotePlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;


public class ClientPlayerUtils {

    public static float CameraYRot = Float.MIN_VALUE;

    public static float CameraYRot0 = Float.MIN_VALUE;

    /**
     * remotePlayerのみ位置を変更 (クライアントでの再計算のため)
     * @param player player
     * @param vec3 vector
     */
    public static void SetPosIfRemote(Player player, Vec3 vec3){
        if(player instanceof RemotePlayer){
            player.setPos(vec3);
        }
    }

    /**
     * カメラ視点
     * @param partialTicks partialTicks
     * @return interpolated cameraViewYRot
     */
    public static float getCameraViewYRot(float partialTicks) {
        return partialTicks == 1.0F ? ClientPlayerUtils.CameraYRot0 : Mth.lerp(partialTicks, ClientPlayerUtils.CameraYRot0, ClientPlayerUtils.CameraYRot);
    }

    public static void resetCameraYRot(){
        CameraYRot = Float.MIN_VALUE;
        CameraYRot0 = Float.MIN_VALUE;
    }

    public static void addCameraYRot(Player player, float value){
        if(CameraYRot == Float.MIN_VALUE){
            CameraYRot = player.getYRot();
            CameraYRot0 = player.yRotO;
        }
        else{
            CameraYRot += value;
            CameraYRot0 += value;
        }
    }
}
