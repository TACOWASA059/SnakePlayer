package com.github.tacowasa059.snakeplayer.common.entity;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.entity.PartEntity;

public final class NeoForgePlayerPart extends PartEntity<Player> implements PlayerPart {
    private final String name;

    public NeoForgePlayerPart(Player parentMob, String name) {
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

    // 1.21.2 で Entity.hurtServer が abstract 化されたため実装する。
    // セグメントは元々 isPickable=false で直接攻撃対象にならないため、ダメージは受けない。
    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return false;
    }
}
