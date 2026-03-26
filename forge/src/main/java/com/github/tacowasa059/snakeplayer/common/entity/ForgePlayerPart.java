package com.github.tacowasa059.snakeplayer.common.entity;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.entity.PartEntity;

public final class ForgePlayerPart extends PartEntity<Player> implements PlayerPart {
    private final String name;

    public ForgePlayerPart(Player parentMob, String name) {
        super(parentMob);
        this.name = name;
        setPos(parentMob.position());
        setOldPosAndRot();
        refreshDimensions();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        IPlayerData playerData = (IPlayerData) getParent();
        float size = playerData.snakePlayer$getBodySegmentSize();
        return EntityDimensions.scalable(size, size);
    }
}
