package com.github.tacowasa059.snakeplayer.mixin;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
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
     * camera setting
     * @param p_90576_
     * @param p_90577_
     * @param p_90578_
     * @param p_90579_
     * @param p_90580_
     * @param ci
     */
    @Inject(method = "setup",at=@At("HEAD"),cancellable = true)
    public void setup(BlockGetter p_90576_, Entity p_90577_, boolean p_90578_, boolean p_90579_, float p_90580_, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if(player==null)return;
        IPlayerData playerData = (IPlayerData)player;
        if(!playerData.getIsSnake() || player.isSpectator()) return;
        this.initialized = true;
        this.level = p_90576_;
        this.entity = p_90577_;
        this.detached = p_90578_;
        this.setRotation(p_90577_.getViewYRot(p_90580_), p_90577_.getViewXRot(p_90580_));
        this.setPosition(Mth.lerp(p_90580_, p_90577_.xo, p_90577_.getX()),
                Mth.lerp(p_90580_, p_90577_.yo,
                        p_90577_.getY()) + (double)Mth.lerp(p_90580_, this.eyeHeightOld, this.eyeHeight),
                Mth.lerp(p_90580_, p_90577_.zo, p_90577_.getZ()));
        if (p_90578_) {
            if (p_90579_) {
                this.setRotation(this.yRot + 180.0F, -this.xRot);
            }

            this.move(-this.getMaxZoom(8.0D * playerData.getHeadSize()), 0.0D, 0.0D);
        } else if (p_90577_ instanceof LivingEntity && ((LivingEntity)p_90577_).isSleeping()) {
            Direction direction = ((LivingEntity)p_90577_).getBedOrientation();
            this.setRotation(direction != null ? direction.toYRot() - 180.0F : 0.0F, 0.0F);
            this.move(0.0D, 0.3D, 0.0D);
        }

        ci.cancel();

    }
}
