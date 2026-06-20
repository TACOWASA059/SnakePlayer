package com.github.tacowasa059.snakeplayer.mixin.client;

import com.github.tacowasa059.snakeplayer.client.util.ClientPlayerUtils;
import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.1.2 で Camera は setup(...) から update(DeltaTracker) + alignWithEntity(float) へ再構成された。
 * スネークプレイヤーのカメラ（移動方向追従ヨー＋頭サイズ基準のズーム）を alignWithEntity で再実装する。
 */
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private Entity entity;
    @Shadow
    private float xRot;
    @Shadow
    private float yRot;
    @Shadow
    private boolean detached;
    @Shadow
    private float eyeHeight;
    @Shadow
    private float eyeHeightOld;

    @Shadow
    protected abstract float getMaxZoom(float maxZoom);

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Shadow
    protected abstract void move(float zoom, float dy, float dx);

    @Inject(method = "alignWithEntity", at = @At("HEAD"), cancellable = true)
    private void snakePlayer$alignWithEntity(float partialTicks, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player == null || !player.equals(this.entity)) {
            return;
        }
        IPlayerData playerData = (IPlayerData) player;
        if (!playerData.snakePlayer$getIsSnake() || player.isSpectator()) {
            return;
        }

        // 回転：スネーク（非クリエイティブ）は移動方向追従の独自ヨーを使う。
        if (!player.isCreative()) {
            this.setRotation(ClientPlayerUtils.getCameraViewYRot(partialTicks), this.entity.getViewXRot(partialTicks));
        } else {
            this.setRotation(this.entity.getViewYRot(partialTicks), this.entity.getViewXRot(partialTicks));
        }

        // 位置
        this.setPosition(
                Mth.lerp((double) partialTicks, this.entity.xo, this.entity.getX()),
                Mth.lerp((double) partialTicks, this.entity.yo, this.entity.getY()) + Mth.lerp(partialTicks, this.eyeHeightOld, this.eyeHeight),
                Mth.lerp((double) partialTicks, this.entity.zo, this.entity.getZ())
        );

        // 三人称ズーム（頭サイズ基準）／睡眠
        this.detached = !Minecraft.getInstance().options.getCameraType().isFirstPerson();
        if (this.detached) {
            if (Minecraft.getInstance().options.getCameraType().isMirrored()) {
                this.setRotation(this.yRot + 180.0F, -this.xRot);
            }
            this.move(-this.getMaxZoom(8.0F * playerData.snakePlayer$getHeadSize()), 0.0F, 0.0F);
        } else if (this.entity instanceof LivingEntity living && living.isSleeping()) {
            Direction bedOrientation = living.getBedOrientation();
            this.setRotation(bedOrientation != null ? bedOrientation.toYRot() - 180.0F : 0.0F, 0.0F);
            this.move(0.0F, 0.3F, 0.0F);
        }

        ci.cancel();
    }
}
