package com.github.tacowasa059.snakeplayer.client.event;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.SnakePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SnakePlayer.MODID, value = Dist.CLIENT)
public class AutoMoveHandler {

    private static boolean autoMove = true; // 自動前進を有効にするかどうか

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (autoMove) {
            // 現在の速度を取得
            Vec3 currentVelocity = player.getDeltaMovement();

            // 視線方向を取得
            Vec3 forward = player.getLookAngle();

            forward = forward.subtract(0, forward.y,0);
            // 長さがゼロの場合にデフォルトの方向を設定（例: Z方向）
            if (forward.lengthSqr() < 1.0E-6) { // 長さの2乗が非常に小さい場合
                forward = new Vec3(0, 0, 1); // Z方向をデフォルトとする
            } else {
                forward = forward.normalize();
            }

            // 移動速度の追加量を設定
            double additionalSpeed = 0.15; // 追加する速度の大きさ

            // 新しい速度を計算
            Vec3 newVelocity = currentVelocity.add(
                    forward.x * additionalSpeed, // 視線方向に速度を追加
                    0.0,                         // Y方向はそのまま（必要なら変更）
                    forward.z * additionalSpeed  // Z方向も視線方向に追加
            );

            // 新しい速度を設定
            player.setDeltaMovement(newVelocity);

        }
    }

    public static void toggleAutoMove() {
        autoMove = !autoMove; // 状態を反転
    }
}

