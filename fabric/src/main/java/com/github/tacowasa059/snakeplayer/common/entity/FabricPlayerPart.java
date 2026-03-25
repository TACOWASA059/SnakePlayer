package com.github.tacowasa059.snakeplayer.common.entity;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class FabricPlayerPart implements PlayerPart {
    private final Player parentMob;
    private final String name;
    private Vec3 position = Vec3.ZERO;
    private Vec3 oldPosition = Vec3.ZERO;
    private boolean removed;
    private AABB boundingBox = new AABB(0, 0, 0, 0, 0, 0);

    public FabricPlayerPart(Player parentMob, String name) {
        this.parentMob = parentMob;
        this.name = name;
        setPos(parentMob.position());
        setOldPosAndRot();
        refreshDimensions();
    }

    @Override
    public Level level() {
        return parentMob.level();
    }

    @Override
    public Vec3 position() {
        return position;
    }

    @Override
    public Vec3 getPosition(float partialTicks) {
        return oldPosition.lerp(position, partialTicks);
    }

    @Override
    public void setPos(Vec3 pos) {
        this.position = pos;
        updateBoundingBox();
    }

    @Override
    public void setPos(double x, double y, double z) {
        setPos(new Vec3(x, y, z));
    }

    @Override
    public void setOldPosAndRot() {
        this.oldPosition = this.position;
    }

    @Override
    public void refreshDimensions() {
        updateBoundingBox();
    }

    @Override
    public AABB getBoundingBox() {
        return boundingBox;
    }

    @Override
    public double getX() {
        return position.x;
    }

    @Override
    public double getY() {
        return position.y;
    }

    @Override
    public double getZ() {
        return position.z;
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        this.removed = true;
    }

    private void updateBoundingBox() {
        IPlayerData playerData = (IPlayerData) parentMob;
        float size = playerData.snakePlayer$getBodySegmentSize();
        EntityDimensions dimensions = EntityDimensions.scalable(size, size);
        this.boundingBox = dimensions.makeBoundingBox(position);
    }
}