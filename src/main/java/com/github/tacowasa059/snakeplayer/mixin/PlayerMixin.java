package com.github.tacowasa059.snakeplayer.mixin;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import com.github.tacowasa059.snakeplayer.network.ModNetworking;
import com.github.tacowasa059.snakeplayer.network.RefreshDimensionsPacket;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayerData, IForgeEntity {

    @Unique
    private final List<PlayerPart> subEntities = new ArrayList<>();
    @Unique
    private static final EntityDataAccessor<Boolean> IS_Snake = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Float> HEAD_SIZE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
    @Unique
    private static final EntityDataAccessor<Float> BODY_SEGMENT_SIZE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
    @Unique
    private static final EntityDataAccessor<Float> SNAKE_DAMAGE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
    @Unique
    private static final EntityDataAccessor<Float> SNAKE_SPEED = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);

    // NBT キーの定義
    @Unique
    private static final String NBT_KEY_IS_SNAKE = "IsSnake";
    @Unique
    private static final String NBT_KEY_HEAD_SIZE = "SnakeHeadSize";
    @Unique
    private static final String NBT_KEY_BODY_SEGMENT_SIZE = "SnakeBodySegmentSize";
    @Unique
    private static final String NBT_KEY_DAMAGE = "SnakeDamage";
    @Unique
    private static final String NBT_KEY_SPEED = "SnakeSpeed";
    @Unique
    private int partID = 0;


    /**
     * プレイヤーの指定した距離の前方向の座標を計算
     *
     * @param player プレイヤー
     * @param distance プレイヤーからの距離
     * @return 新しい位置ベクトル
     */
    private Vec3 calculateOffsetPosition(Player player, double distance) {
        Vec3 playerPosition = player.position();
        float yaw = player.getYRot();

        double offsetX = distance * Math.sin(Math.toRadians(yaw));
        double offsetZ = -distance * Math.cos(Math.toRadians(yaw));

        return playerPosition.add(offsetX, 0, offsetZ);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructed(CallbackInfo ci) {
        if(!getIsSnake()) return;
        Player player = (Player)(Object)this;
        int current_length = player.experienceLevel + 1;

        partID = 0;
        for(int i = 0; i < current_length; i++){
            PlayerPart playerPart = new PlayerPart(player,"part"+partID);
            subEntities.add(playerPart);
            partID++;

            playerPart.setPos(calculateOffsetPosition(player, (partID - 1) * getBodySegmentSize() + ((getBodySegmentSize() + getHeadSize())/2f)));
            playerPart.setOldPosAndRot();
        }
    }

    @Inject(method="defineSynchedData",at=@At("TAIL"))
    void defineSynchedData(CallbackInfo ci){
        Player player = (Player) (Object)this;
        player.getEntityData().define(IS_Snake, true);
        player.getEntityData().define(HEAD_SIZE, 1.0F);
        player.getEntityData().define(BODY_SEGMENT_SIZE, 1.0F);
        player.getEntityData().define(SNAKE_DAMAGE, 1.0F);
        player.getEntityData().define(SNAKE_SPEED, 0.15F);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if(!getIsSnake()) return;
        Player player = (Player)(Object)this;

        float parts_size = getBodySegmentSize();

        // update entity length
        int target_length = player.experienceLevel + 1;
        int current_length = subEntities.size();

        while(target_length > current_length){ //少ない場合は追加する
            PlayerPart playerPart = new PlayerPart(player,"part"+partID);
            subEntities.add(playerPart);
            partID++;
            current_length = subEntities.size();
            Vec3 vec3;
            if(current_length == 1){
                 vec3 = calculateOffsetPosition(player, (getBodySegmentSize() + getHeadSize())/2f); //need modify

            }else if (current_length == 2){ // ひとつ前の値に足し合わせる
                Vec3 vec0 = player.position();
                Vec3 vec1 = subEntities.get(0).position();
                vec3 = vec1.add((vec1.subtract(vec0)).normalize().scale(parts_size));
            }else{
                Vec3 vec0 = subEntities.get(current_length-3).position();
                Vec3 vec1 = subEntities.get(current_length-2).position();
                vec3 = vec1.add((vec1.subtract(vec0)).normalize().scale(parts_size));
            }
            playerPart.setPos(vec3);
            playerPart.setOldPosAndRot();
        }
        while(target_length < current_length){ //多い場合は減らす
            PlayerPart playerPart = subEntities.get(subEntities.size()-1);

            double x = playerPart.getX();
            double y = playerPart.getY();
            double z = playerPart.getZ();

            int experienceAmount = 5;

            ExperienceOrb experienceOrb = new ExperienceOrb(playerPart.level(), x, y, z, experienceAmount);
            experienceOrb.setDeltaMovement(0.0, 0.1, 0.0);
            playerPart.level().addFreshEntity(experienceOrb);


            playerPart.remove(Entity.RemovalReason.DISCARDED);
            subEntities.remove(subEntities.size()-1);
            partID--;
            current_length = subEntities.size();
        }

        // update position length

        if(subEntities.size()>0){
            subEntities.get(0).setOldPosAndRot();
            subEntities.get(0).setPos(calculateOffsetPosition(player, (getBodySegmentSize() + getHeadSize())/2f));
        }
        for(int i=1;i< subEntities.size();i++){
            Vec3 x0 = subEntities.get(i-1).position();
            Vec3 x1_1 = subEntities.get(i).position();
            Vec3 x1 = x0.subtract((x0.subtract(x1_1)).normalize().scale(parts_size));
            subEntities.get(i).setOldPosAndRot();
            subEntities.get(i).setPos(x1);
        }
        float damageAmount = getSnakeDamage(); // ダメージ量
        // update position of each partEntity
        for(PlayerPart playerPart:subEntities){
            Level level = playerPart.level();
            AABB boundingBox = playerPart.getBoundingBox();
            List<Entity> entities = level.getEntities(playerPart, boundingBox, entity -> (entity instanceof LivingEntity && entity.getId()!=player.getId()));

            Holder<DamageType> damageTypeHolder = level.registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypes.MOB_ATTACK);
            DamageSource damageSource = new DamageSource(damageTypeHolder, (Player)(Object)this);

            for(Entity entity : entities){
                entity.hurt(damageSource, damageAmount);
            }
        }
    }
    public void removeSubEntities(){
        for(int i=subEntities.size()-1;i>=0;i--){
            subEntities.get(i).remove(Entity.RemovalReason.DISCARDED);
        }
        subEntities.clear();
    }


    @Override
    public List<PlayerPart> getPlayerParts(){
        return subEntities;
    }
    @Inject(method="getDimensions",at=@At("HEAD"),cancellable = true)
    public void getDimensions(Pose p_36166_, CallbackInfoReturnable<EntityDimensions> cir) {
        if(getIsSnake()) {
            EntityDimensions entityDimensions = EntityDimensions.scalable(getHeadSize(), getHeadSize());
            cir.setReturnValue(entityDimensions);
            cir.cancel();
        }
    }
    @Inject(method = "getStandingEyeHeight", at=@At("HEAD"),cancellable = true)
    public void getStandingEyeHeight(Pose p_36259_, EntityDimensions p_36260_, CallbackInfoReturnable<Float> cir){
        if(getIsSnake()){
            cir.setReturnValue(0.75F * getHeadSize());
            cir.cancel();
        }
    }

    @Override
    public boolean isMultipartEntity() {
        return getIsSnake();
    }

    @Override
    public net.minecraftforge.entity.PartEntity<?>[] getParts() {
        return this.subEntities.toArray(new PartEntity<?>[0]);
    }

    // NBT 書き込み
    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    public void addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean(NBT_KEY_IS_SNAKE, getIsSnake());
        compound.putFloat(NBT_KEY_HEAD_SIZE, getHeadSize());
        compound.putFloat(NBT_KEY_BODY_SEGMENT_SIZE, getBodySegmentSize());
        compound.putFloat(NBT_KEY_DAMAGE, getSnakeDamage());
        compound.putFloat(NBT_KEY_SPEED, getSnakeSpeed());
    }

    // NBT 読み込み
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    public void readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        if(compound.contains(NBT_KEY_IS_SNAKE)){
            setIsSnake(compound.getBoolean(NBT_KEY_IS_SNAKE));
        }
        if (compound.contains(NBT_KEY_HEAD_SIZE)) {
            setHeadSize(compound.getFloat(NBT_KEY_HEAD_SIZE));
        }
        if (compound.contains(NBT_KEY_BODY_SEGMENT_SIZE)) {
            setBodySegmentSize(compound.getFloat(NBT_KEY_BODY_SEGMENT_SIZE));
        }
        if (compound.contains(NBT_KEY_DAMAGE)) {
            setSnakeDamage(compound.getFloat(NBT_KEY_DAMAGE));
        }
        if (compound.contains(NBT_KEY_SPEED)) {
            setSnakeSpeed(compound.getFloat(NBT_KEY_SPEED));
        }
    }

    // Getter
    @Override
    public boolean getIsSnake(){
        Player player = (Player)(Object)this;
        return player.getEntityData().get(IS_Snake);
    }
    @Override
    public float getHeadSize() {
        Player player = (Player)(Object)this;
        return player.getEntityData().get(HEAD_SIZE);
    }

    @Override
    public float getBodySegmentSize() {
        Player player = (Player)(Object)this;
        return player.getEntityData().get(BODY_SEGMENT_SIZE);
    }

    @Override
    public float getSnakeDamage() {
        Player player = (Player)(Object)this;
        return player.getEntityData().get(SNAKE_DAMAGE);
    }

    @Override
    public float getSnakeSpeed() {
        Player player = (Player)(Object)this;
        return player.getEntityData().get(SNAKE_SPEED);
    }

    // Setter
    @Override
    public void setIsSnake(boolean value){
        Player player = (Player)(Object)this;
        player.getEntityData().set(IS_Snake, value);
        if(!value) removeSubEntities(); //falseのときはsubEntitiesをremove
        player.refreshDimensions();
        if(!player.level().isClientSide()) ModNetworking.CHANNEL.send(PacketDistributor.ALL.noArg(),
                new RefreshDimensionsPacket(player.getUUID(),getIsSnake(),getHeadSize(), getBodySegmentSize()));
    }
    @Override
    public void setHeadSize(float value) {
        Player player = (Player)(Object)this;
        player.getEntityData().set(HEAD_SIZE, value);
        player.refreshDimensions();
        if(!player.level().isClientSide()) ModNetworking.CHANNEL.send(PacketDistributor.ALL.noArg(),
                new RefreshDimensionsPacket(player.getUUID(),getIsSnake(),getHeadSize(), getBodySegmentSize()));
    }

    @Override
    public void setBodySegmentSize(float value) {
        Player player = (Player)(Object)this;
        player.getEntityData().set(BODY_SEGMENT_SIZE, value);
        player.refreshDimensions();
        if(!player.level().isClientSide()) ModNetworking.CHANNEL.send(PacketDistributor.ALL.noArg(),
                new RefreshDimensionsPacket(player.getUUID(),getIsSnake(),getHeadSize(), getBodySegmentSize()));
    }

    @Override
    public void setSnakeDamage(float value) {
        Player player = (Player)(Object)this;
        player.getEntityData().set(SNAKE_DAMAGE, value);
    }

    @Override
    public void setSnakeSpeed(float value) {
        Player player = (Player)(Object)this;
        player.getEntityData().set(SNAKE_SPEED, value);
    }

}
