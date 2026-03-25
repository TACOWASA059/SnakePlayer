package com.github.tacowasa059.snakeplayer.common.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface PlayerPart {
    Level level();
    Vec3 position();
    Vec3 getPosition(float partialTicks);
    void setPos(Vec3 pos);
    void setPos(double x, double y, double z);
    void setOldPosAndRot();
    void refreshDimensions();
    AABB getBoundingBox();
    double getX();
    double getY();
    double getZ();
    boolean isRemoved();
    void remove(Entity.RemovalReason removalReason);
}