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
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private boolean initialized;
    @Shadow
    private BlockGetter level;
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
    protected abstract double getMaxZoom(double p_90567_);
    @Shadow
    protected abstract void setPosition(double p_90585_, double p_90586_, double p_90587_);
    @Shadow
    protected abstract void setRotation(float p_90573_, float p_90574_);

    @Shadow
    protected abstract void move(double p_90569_, double p_90570_, double p_90571_);

    /**
     * カメラの位置設定
     * @param blockGetter blockGetter
     * @param entity entity
     * @param detached_ entityに追従するかどうか
     * @param inv 視点の反転
     * @param partialTicks partialTicks
     * @param ci callbackInfo
     */
    @Inject(method = "setup",at=@At("HEAD"),cancellable = true)
    public void setup(BlockGetter blockGetter, Entity entity, boolean detached_, boolean inv, float partialTicks, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if(player==null)return;
        if(!player.equals(entity))return;
        IPlayerData playerData = (IPlayerData)player;
        if(!playerData.snakePlayer$getIsSnake() || player.isSpectator()) return;
        this.initialized = true;
        this.level = blockGetter;
        this.entity = entity;
        this.detached = detached_;

        if(!player.isCreative()){
            this.setRotation(ClientPlayerUtils.getCameraViewYRot(partialTicks), entity.getViewXRot(partialTicks));
        }else{
            this.setRotation(entity.getViewYRot(partialTicks), entity.getViewXRot(partialTicks));
        }

        this.setPosition(Mth.lerp(partialTicks, entity.xo, entity.getX()),
                Mth.lerp(partialTicks, entity.yo,
                        entity.getY()) + (double)Mth.lerp(partialTicks, this.eyeHeightOld, this.eyeHeight),
                Mth.lerp(partialTicks, entity.zo, entity.getZ()));
        if (detached_) {
            if (inv) {
                this.setRotation(this.yRot + 180.0F, -this.xRot);
            }

            this.move(-this.getMaxZoom(8.0D * playerData.snakePlayer$getHeadSize()), 0.0D, 0.0D);
        } else if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
            Direction direction = ((LivingEntity)entity).getBedOrientation();
            this.setRotation(direction != null ? direction.toYRot() - 180.0F : 0.0F, 0.0F);
            this.move(0.0D, 0.3D, 0.0D);
        }

        ci.cancel();

    }
}
