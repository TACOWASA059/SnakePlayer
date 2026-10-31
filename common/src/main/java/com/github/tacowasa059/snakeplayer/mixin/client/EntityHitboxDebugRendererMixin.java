package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    // 三人称視点および他プレイヤーのパーツヒットボックス描画
    @Inject(method = "showHitboxes", at = @At("TAIL"))
    private void snakePlayer$renderPartHitboxes(Entity entity, float partialTick, boolean hasDeltaMovement, CallbackInfo ci) {
        snakePlayer$renderParts(entity, partialTick);
    }

    // 一人称視点時にスキップされる自分自身のパーツヒットボックス描画
    @Inject(method = "emitGizmos", at = @At("TAIL"))
    private void snakePlayer$renderFirstPersonPartHitboxes(double camX, double camY, double camZ, DebugValueAccess debugValueAccess, Frustum frustum, float partialTick, CallbackInfo ci) {
        if (this.minecraft.options.getCameraType() == CameraType.FIRST_PERSON && this.minecraft.player != null) {
            snakePlayer$renderParts(this.minecraft.player, partialTick);
        }
    }

    @Unique
    private static void snakePlayer$renderParts(Entity entity, float partialTick) {
        if (entity instanceof IPlayerData playerData && playerData.snakePlayer$getIsSnake()) {
            List<PlayerPart> parts = playerData.snakePlayer$getPlayerParts();
            if (parts != null && !parts.isEmpty()) {
                int strokeColor = ARGB.colorFromFloat(1.0f, 0.25f, 1.0f, 0.0f);
                GizmoStyle style = GizmoStyle.stroke(strokeColor);
                for (PlayerPart part : parts) {
                    Vec3 currentPos = part.position();
                    Vec3 interpPos = part.getPosition(partialTick);
                    Vec3 offset = interpPos.subtract(currentPos);
                    AABB aabb = part.getBoundingBox().move(offset);
                    Gizmos.cuboid(aabb, style);
                }
            }
        }
    }
}
