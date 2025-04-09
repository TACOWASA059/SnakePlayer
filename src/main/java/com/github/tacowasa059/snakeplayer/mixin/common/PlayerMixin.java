package com.github.tacowasa059.snakeplayer.mixin.common;

import com.github.tacowasa059.snakeplayer.client.util.ClientPlayerUtils;
import com.github.tacowasa059.snakeplayer.common.Config;
import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.common.entity.ModDataSerializers;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.entity.PartEntity;
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
    private final List<PlayerPart> snakePlayer$subEntities = new ArrayList<>();
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
    @Unique
    private static final EntityDataAccessor<Integer> SNAKE_EXPERIENCE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

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
    private static final String NBT_KEY_EXP = "XpLevel";
    @Unique
    private int snakePlayer$partID = 0;

    @Unique
    private static final EntityDataAccessor<List<Vec3>> PART_POSITIONS = SynchedEntityData.defineId(
            Player.class, ModDataSerializers.VEC3_LIST);


    /**
     * プレイヤーの指定した距離の前方向の座標を計算
     *
     * @param player プレイヤー
     * @param distance プレイヤーからの距離
     * @return 新しい位置ベクトル
     */
    @Unique
    private Vec3 snakePlayer$calculateOffsetPosition(Player player, double distance) {
        Vec3 playerPosition = player.position();
        float yaw = player.getYRot();

        double offsetX = distance * Math.sin(Math.toRadians(yaw));
        double offsetZ = -distance * Math.cos(Math.toRadians(yaw));

        return playerPosition.add(offsetX, 0, offsetZ);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructed(CallbackInfo ci) {
        if(!snakePlayer$getIsSnake()) return;
        Player player = (Player)(Object)this;
        int current_length = player.getEntityData().get(SNAKE_EXPERIENCE) + 1;

        snakePlayer$partID = 0;
        for(int i = 0; i < current_length; i++){
            PlayerPart playerPart = new PlayerPart(player,"part"+ snakePlayer$partID);
            snakePlayer$subEntities.add(playerPart);
            snakePlayer$partID++;

            playerPart.setPos(snakePlayer$calculateOffsetPosition(player, (snakePlayer$partID - 1) * snakePlayer$getBodySegmentSize() + ((snakePlayer$getBodySegmentSize() + snakePlayer$getHeadSize())/2f)));
            playerPart.setOldPosAndRot();
        }
    }

    @Inject(method="defineSynchedData",at=@At("TAIL"))
    void defineSynchedData(CallbackInfo ci){
        Player player = (Player) (Object)this;
        player.getEntityData().define(IS_Snake, false);
        player.getEntityData().define(HEAD_SIZE, 1.0F);
        player.getEntityData().define(BODY_SEGMENT_SIZE, 1.0F);
        player.getEntityData().define(SNAKE_DAMAGE, 1.0F);
        player.getEntityData().define(SNAKE_SPEED, 0.3F);
        player.getEntityData().define(SNAKE_EXPERIENCE, 0);
        player.getEntityData().define(PART_POSITIONS, new ArrayList<>());
    }

    /**
     * tick処理
     * @param ci CallbackInfo
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        Player player = (Player)(Object)this;

        if (snakePlayer$restorePlayer(player)) return;

        float parts_size = snakePlayer$getBodySegmentSize();

        // update entity length
        snakePlayer$updateSegmentLength(player, parts_size);

        // update position length (only server side)
        snakePlayer$updateServerPositions(player, parts_size);

        //sync position of each segments
        snakePlayer$syncPositions(player);

        // damage (only server side)
        snakePlayer$SetDamage(player);

        // refreshDimensions (only client side)
        snakePlayer$updateAABB(player);

    }


    /**
     * セグメントの座標更新
     * @param player player
     * @param parts_size parts size
     */
    @Unique
    public void snakePlayer$updateServerPositions(Player player, float parts_size) {
        if(!player.level().isClientSide){
            if(snakePlayer$subEntities.size()>0){
                snakePlayer$subEntities.get(0).setOldPosAndRot();
                snakePlayer$subEntities.get(0).setPos(snakePlayer$calculateOffsetPosition(player, (snakePlayer$getBodySegmentSize() + snakePlayer$getHeadSize())/2f));
            }
            for(int i = 1; i< snakePlayer$subEntities.size(); i++){
                Vec3 x0 = snakePlayer$subEntities.get(i-1).position();
                Vec3 x1_1 = snakePlayer$subEntities.get(i).position();

                Vec3 x1 = x0.subtract((x0.subtract(x1_1)).normalize().scale(parts_size));

                snakePlayer$subEntities.get(i).setOldPosAndRot();
                snakePlayer$subEntities.get(i).setPos(x1);
            }
        }
    }

    /**
     * Clientでのセグメントの座標更新 (位置補正)
     * @param player player
     * @param parts_size parts size
     */
    @Unique
    public void snakePlayer$updateClientPositions(Player player, float parts_size) {
        if(player.level().isClientSide){
            if(snakePlayer$subEntities.size()>0){
                snakePlayer$subEntities.get(0).setPos(snakePlayer$calculateOffsetPosition(player, (snakePlayer$getBodySegmentSize() + snakePlayer$getHeadSize())/2f));
            }
            for(int i = 1; i< snakePlayer$subEntities.size(); i++){
                Vec3 x0 = snakePlayer$subEntities.get(i-1).position();
                Vec3 x1_1 = snakePlayer$subEntities.get(i).position();

                Vec3 x1 = x0.subtract((x0.subtract(x1_1)).normalize().scale(parts_size));

                snakePlayer$subEntities.get(i).setPos(x1);
            }
        }
    }

    /**
     * セグメント座標の同期処理
     * @param player 対象となるプレイヤー
     */
    @Unique
    public void snakePlayer$syncPositions(Player player) {
        if(!player.level().isClientSide()){ //server side
            List<Vec3> partPositions = new ArrayList<>();

            partPositions.add(player.position());
            for(PlayerPart playerPart: snakePlayer$subEntities){
                partPositions.add(new Vec3(playerPart.getX(), playerPart.getY(), playerPart.getZ()));
            }

            player.getEntityData().set(PART_POSITIONS, partPositions, true);

        }else{ // client side
            List<Vec3> partPositions = player.getEntityData().get(PART_POSITIONS);

            if(partPositions.size()>0) ClientPlayerUtils.SetPosIfRemote(player, partPositions.get(0));

            for(int i = 0; i < Math.min(partPositions.size()-1, snakePlayer$subEntities.size()); i++){
                Vec3 pos = partPositions.get(i+1);

                PlayerPart playerPart = snakePlayer$subEntities.get(i);
                if(playerPart != null){
                    playerPart.setOldPosAndRot();
                    playerPart.setPos(pos);
                }

            }
            snakePlayer$updateClientPositions(player, snakePlayer$getBodySegmentSize());
        }
    }

    /**
     * AABBの更新
     *
     * @param player player
     */
    @Unique
    private void snakePlayer$updateAABB(Player player) {
        if(player.level().isClientSide() && player.tickCount % 5 == 0){

            float width = (float) player.getBoundingBox().getXsize();
            float height = (float) player.getBoundingBox().getYsize();

            float current_size = snakePlayer$getHeadSize();
            if(Math.abs(current_size - width)> (float) 0.05 || Math.abs(current_size - height)> (float) 0.05){
                player.refreshDimensions();
            }

            float current_segment_size = snakePlayer$getBodySegmentSize();
            for(PlayerPart playerPart : snakePlayer$subEntities){
                float parts_width = (float) playerPart.getBoundingBox().getXsize();
                float parts_height = (float) playerPart.getBoundingBox().getYsize();
                if(Math.abs(current_segment_size - parts_width)> (float) 0.05 ||
                        Math.abs(current_segment_size - parts_height)> (float) 0.05){
                    playerPart.refreshDimensions();
                }
            }

        }
    }

    /**
     * ダメージの設定
     * @param player 基準となるプレイヤー
     */
    @Unique
    private void snakePlayer$SetDamage(Player player) {
        float damageAmount = snakePlayer$getSnakeDamage(); // ダメージ量
        if((!player.level().isClientSide) && (!player.isDeadOrDying())&&(!player.isSpectator())){ // 死んでいないときのみ
            for(PlayerPart playerPart: snakePlayer$subEntities){
                if(playerPart.isRemoved()) continue;
                Level level = playerPart.level();
                AABB boundingBox = playerPart.getBoundingBox();
                List<Entity> entities = level.getEntities(playerPart, boundingBox, entity -> (entity instanceof LivingEntity && entity.getId()!= player.getId()));

                Holder<DamageType> damageTypeHolder = level.registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.MOB_ATTACK);
                DamageSource damageSource = new DamageSource(damageTypeHolder, (Player)(Object)this);

                for(Entity entity : entities){
                    if(entity instanceof Player player1){
                        if((!player1.isCreative())&&(!player1.isSpectator())){
                            entity.hurt(damageSource, damageAmount);
                        }
                    }
                    else{
                        entity.hurt(damageSource, damageAmount);
                    }
                }
            }
        }
    }



    /**
     * セグメント数の変更
     * @param player player
     * @param parts_size segment size
     */
    @Unique
    private void snakePlayer$updateSegmentLength(Player player, float parts_size) {
        int target_length = player.getEntityData().get(SNAKE_EXPERIENCE) + 1;
        int current_length = snakePlayer$subEntities.size();

        int addition = 0;

        while(target_length > current_length){ //少ない場合は追加する
            PlayerPart playerPart = new PlayerPart(player,"part"+ snakePlayer$partID);
            snakePlayer$subEntities.add(playerPart);
            snakePlayer$partID++;
            current_length = snakePlayer$subEntities.size();
            Vec3 vec3;
            if(current_length == 1){
                 vec3 = snakePlayer$calculateOffsetPosition(player, (snakePlayer$getBodySegmentSize() + snakePlayer$getHeadSize())/2f); //need modify

            }else if (current_length == 2){ // ひとつ前の値に足し合わせる
                Vec3 vec0 = player.position();
                Vec3 vec1 = snakePlayer$subEntities.get(0).position();
                vec3 = vec1.add((vec1.subtract(vec0)).normalize().scale(parts_size));
            }else{
                Vec3 vec0 = snakePlayer$subEntities.get(current_length-3).position();
                Vec3 vec1 = snakePlayer$subEntities.get(current_length-2).position();
                Vec3 relative = (vec1.subtract(vec0)).normalize().scale(parts_size);

                if(addition > 4){ // added : 急に増えたとき用
                    relative = new Vec3(-relative.x, relative.y, -relative.z);
                }

                vec3 = vec1.add(relative);
            }
            playerPart.setPos(vec3);
            playerPart.setOldPosAndRot();

            addition++;
        }
        while(target_length < current_length){ //多い場合は減らす
            PlayerPart playerPart = snakePlayer$subEntities.get(snakePlayer$subEntities.size()-1);

            double x = playerPart.getX();
            double y = playerPart.getY();
            double z = playerPart.getZ();

            if(!player.level().isClientSide){
                int experienceAmount = Config.expValue.get();
                if(experienceAmount > 0){
                    ExperienceOrb experienceOrb = new ExperienceOrb(playerPart.level(), x, y, z, experienceAmount);
                    experienceOrb.setDeltaMovement(0.0, 0.1, 0.0);
                    playerPart.level().addFreshEntity(experienceOrb);
                }
            }

            playerPart.remove(Entity.RemovalReason.DISCARDED);
            snakePlayer$subEntities.remove(snakePlayer$subEntities.size()-1);
            snakePlayer$partID--;
            current_length = snakePlayer$subEntities.size();
        }
    }

    /**
     * プレイヤー状態に戻す処理(client)
     *
     * @param player player
     * @return 蛇状態でない
     */
    @Unique
    private boolean snakePlayer$restorePlayer(Player player) {
        if(!snakePlayer$getIsSnake()){
            if(player.level().isClientSide()){
                float width = (float) player.getBoundingBox().getXsize();
                float height = (float) player.getBoundingBox().getYsize();
                EntityDimensions entityDimensions = player.getDimensions(player.getPose());
                if(Math.abs(entityDimensions.width - width) > (float) 0.05 ||
                        Math.abs(entityDimensions.height - height) > (float) 0.05){
                    player.refreshDimensions();
                }
            }
            return true;
        }
        return false;
    }

    @Unique
    public void snakePlayer$removeSubEntities(){
        for(int i = snakePlayer$subEntities.size()-1; i>=0; i--){
            snakePlayer$subEntities.get(i).remove(Entity.RemovalReason.DISCARDED);
        }
        snakePlayer$subEntities.clear();
    }


    @Override
    public List<PlayerPart> snakePlayer$getPlayerParts(){
        return snakePlayer$subEntities;
    }
    @Inject(method="getDimensions",at=@At("HEAD"),cancellable = true)
    public void getDimensions(Pose p_36166_, CallbackInfoReturnable<EntityDimensions> cir) {
        if(snakePlayer$getIsSnake()) {
            EntityDimensions entityDimensions = EntityDimensions.scalable(snakePlayer$getHeadSize(), snakePlayer$getHeadSize());
            cir.setReturnValue(entityDimensions);
            cir.cancel();
        }
    }
    @Inject(method = "getStandingEyeHeight", at=@At("HEAD"),cancellable = true)
    public void getStandingEyeHeight(Pose p_36259_, EntityDimensions p_36260_, CallbackInfoReturnable<Float> cir){
        if(snakePlayer$getIsSnake()){
            cir.setReturnValue(0.75F * snakePlayer$getHeadSize());
            cir.cancel();
        }
    }

    @Override
    public boolean isMultipartEntity() {
        return snakePlayer$getIsSnake();
    }

    @Override
    public net.minecraftforge.entity.PartEntity<?>[] getParts() {
        return this.snakePlayer$subEntities.toArray(new PartEntity<?>[0]);
    }

    // NBT 書き込み
    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    public void addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        Player player = (Player)(Object)this;
        compound.putBoolean(NBT_KEY_IS_SNAKE, snakePlayer$getIsSnake());
        compound.putFloat(NBT_KEY_HEAD_SIZE, snakePlayer$getHeadSize());
        compound.putFloat(NBT_KEY_BODY_SEGMENT_SIZE, snakePlayer$getBodySegmentSize());
        compound.putFloat(NBT_KEY_DAMAGE, snakePlayer$getSnakeDamage());
        compound.putFloat(NBT_KEY_SPEED, snakePlayer$getSnakeSpeed());
        compound.putInt(NBT_KEY_EXP, player.getEntityData().get(SNAKE_EXPERIENCE));
    }

    // NBT 読み込み
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    public void readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        if(compound.contains(NBT_KEY_IS_SNAKE)){
            snakePlayer$setIsSnake(compound.getBoolean(NBT_KEY_IS_SNAKE));
        }
        if (compound.contains(NBT_KEY_HEAD_SIZE)) {
            snakePlayer$setHeadSize(compound.getFloat(NBT_KEY_HEAD_SIZE));
        }
        if (compound.contains(NBT_KEY_BODY_SEGMENT_SIZE)) {
            snakePlayer$setBodySegmentSize(compound.getFloat(NBT_KEY_BODY_SEGMENT_SIZE));
        }
        if (compound.contains(NBT_KEY_DAMAGE)) {
            snakePlayer$setSnakeDamage(compound.getFloat(NBT_KEY_DAMAGE));
        }
        if (compound.contains(NBT_KEY_SPEED)) {
            snakePlayer$setSnakeSpeed(compound.getFloat(NBT_KEY_SPEED));
        }
        if (compound.contains(NBT_KEY_EXP)) {
            Player player = (Player)(Object)this;
            player.getEntityData().set(SNAKE_EXPERIENCE, compound.getInt(NBT_KEY_EXP));
        }
    }
    @Inject(method = "giveExperienceLevels",at=@At("TAIL"))
    public void giveExperience(int p_36276_, CallbackInfo ci){
        Player player = (Player) (Object) this;
        snakePlayer$setSnakeExperience(player.experienceLevel);
    }
    @Inject(method = "onEnchantmentPerformed",at=@At("TAIL"))
    public void onEnchantmentPerformed(ItemStack p_36172_, int p_36173_, CallbackInfo ci){
        Player player = (Player) (Object) this;
        snakePlayer$setSnakeExperience(player.experienceLevel);
    }

    // Getter
    @Override
    public boolean snakePlayer$getIsSnake(){
        Player player = (Player)(Object)this;
        return player.getEntityData().get(IS_Snake);
    }
    @Override
    public float snakePlayer$getHeadSize() {
        Player player = (Player)(Object)this;
        return player.getEntityData().get(HEAD_SIZE);
    }

    @Override
    public float snakePlayer$getBodySegmentSize() {
        Player player = (Player)(Object)this;
        return player.getEntityData().get(BODY_SEGMENT_SIZE);
    }

    @Override
    public float snakePlayer$getSnakeDamage() {
        Player player = (Player)(Object)this;
        return player.getEntityData().get(SNAKE_DAMAGE);
    }

    @Override
    public float snakePlayer$getSnakeSpeed() {
        Player player = (Player)(Object)this;
        return player.getEntityData().get(SNAKE_SPEED);
    }

    // Setter
    @Override
    public void snakePlayer$setIsSnake(boolean value){
        Player player = (Player)(Object)this;
        player.getEntityData().set(IS_Snake, value);
        if(!value) snakePlayer$removeSubEntities(); //falseのときはsubEntitiesをremove
        player.refreshDimensions();
    }
    @Override
    public void snakePlayer$setHeadSize(float value) {
        Player player = (Player)(Object)this;
        player.getEntityData().set(HEAD_SIZE, value);
        player.refreshDimensions();
    }

    @Override
    public void snakePlayer$setBodySegmentSize(float value) {
        Player player = (Player)(Object)this;
        player.getEntityData().set(BODY_SEGMENT_SIZE, value);
        player.refreshDimensions();
    }

    @Override
    public void snakePlayer$setSnakeDamage(float value) {
        Player player = (Player)(Object)this;
        player.getEntityData().set(SNAKE_DAMAGE, value);
    }

    @Override
    public void snakePlayer$setSnakeSpeed(float value) {
        Player player = (Player)(Object)this;
        player.getEntityData().set(SNAKE_SPEED, value);
    }
    @Override
    public void snakePlayer$setSnakeExperience(int experience){
        Player player = (Player)(Object)this;
        player.getEntityData().set(SNAKE_EXPERIENCE, experience);
    }
}
