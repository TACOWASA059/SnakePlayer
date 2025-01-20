package com.github.tacowasa059.snakeplayer.client.event;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.SnakePlayer;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SnakePlayer.MODID, value = Dist.CLIENT)
public class AutoMoveHandler {


    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        IPlayerData playerData = (IPlayerData)player;
        if(!playerData.getIsSnake() || player.isSpectator()) return;

        // 現在の速度を取得
        Vec3 currentVelocity = player.getDeltaMovement();

        // 視線方向を取得
        Vec3 forward = player.getLookAngle();

        forward = forward.subtract(0, forward.y,0);

        if (forward.lengthSqr() < 1.0E-6) {
            List<PlayerPart> list = playerData.getPlayerParts();
            if(list.size()==0) forward = new Vec3(0, 0, 1);
            else{
                Vec3 vec3 = (player.position().subtract(list.get(0).position()));
                vec3 = vec3.subtract(0, vec3.y, 0);
                forward = vec3.normalize();
                if(forward.lengthSqr() < 1.0E-6){
                    forward = new Vec3(0, 0, 1);
                }
            }
        } else {
            forward = forward.normalize();
        }

        double additionalSpeed = playerData.getSnakeSpeed();

        Vec3 newVelocity = currentVelocity.add(
                forward.x * additionalSpeed,
                0.0,
                forward.z * additionalSpeed
        );

        // 新しい速度を設定
        player.setDeltaMovement(newVelocity);

    }
}

