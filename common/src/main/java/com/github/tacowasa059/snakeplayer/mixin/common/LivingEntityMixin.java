package com.github.tacowasa059.snakeplayer.mixin.common;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;", at = @At("HEAD"), cancellable = true)
    private void snakePlayer$getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (!((Object) this instanceof Player player)) {
            return;
        }
        IPlayerData playerData = (IPlayerData) player;
        if (!playerData.snakePlayer$getIsSnake()) {
            return;
        }
        float size = playerData.snakePlayer$getHeadSize();
        cir.setReturnValue(EntityDimensions.scalable(size, size));
    }
}
