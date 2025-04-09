package com.github.tacowasa059.snakeplayer.common.entity;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PlayerPart extends PartEntity<Player> {
    public final Player parentMob;
    public final String name;
    public PlayerPart(Player p_31014_, String p_31015_) {
        super(p_31014_);
        this.parentMob = p_31014_;
        this.name = p_31015_;
        this.refreshDimensions();
    }

    protected void defineSynchedData() {
    }

    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
    }

    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
    }

    public boolean isPickable() {
        return true;
    }

    @Nullable
    public ItemStack getPickResult() {
        return this.parentMob.getPickResult();
    }

    public boolean hurt(@NotNull DamageSource damageSource, float p_31021_) {
        return !this.isInvulnerableTo(damageSource) && this.parentMob.hurt(damageSource, p_31021_);
    }

    public boolean is(@NotNull Entity entity) {
        return this == entity || this.parentMob == entity;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        IPlayerData playerData =(IPlayerData) parentMob;
        float size = playerData.snakePlayer$getBodySegmentSize();
        return EntityDimensions.scalable(size, size);
    }

    public boolean shouldBeSaved() {
        return false;
    }

}
