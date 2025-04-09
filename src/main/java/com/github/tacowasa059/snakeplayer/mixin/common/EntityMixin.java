package com.github.tacowasa059.snakeplayer.mixin.common;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    private static Vec3 collideWithShapes(Vec3 p_198901_, AABB p_198902_, List<VoxelShape> p_198903_) {
        return null;
    }

    @Inject(method = "collideBoundingBox",at=@At("HEAD"),cancellable = true)
    private static void collideBoundingBox(Entity entity, Vec3 vec3, AABB aabb, Level p_198898_, List<VoxelShape> p_198899_, CallbackInfoReturnable<Vec3> cir) {
        if(!(entity instanceof Player player)) return;
        IPlayerData playerData = (IPlayerData)player;
        if(!playerData.snakePlayer$getIsSnake()) return;

        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(p_198899_.size() + 1);
        if (!p_198899_.isEmpty()) {
            builder.addAll(p_198899_);
        }


        builder.addAll(p_198898_.getBlockCollisions(entity, aabb.expandTowards(vec3)));

        cir.setReturnValue(collideWithShapes(vec3, aabb, builder.build()));
        cir.cancel();
    }
}
