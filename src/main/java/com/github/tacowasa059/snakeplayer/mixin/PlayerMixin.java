package com.github.tacowasa059.snakeplayer.mixin;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayerData, IForgeEntity {


    private final List<PlayerPart> subEntities = new ArrayList<>();;
    private final LinkedList<Vec3> subPositions = new LinkedList<>();

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
        Player player = (Player)(Object)this;
        int current_length = player.experienceLevel + 1;
        for(int i = 0; i < current_length; i++){
            PlayerPart playerPart = new PlayerPart(player,"part"+partID,1f,1f);
            subEntities.add(playerPart);
            partID++;
            Vec3 vec3 = calculateOffsetPosition(player, 1);

            subPositions.add(vec3);
            playerPart.setPos(vec3);
            playerPart.setOldPosAndRot();
        }
    }



    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        Player player = (Player)(Object)this;

        // update entity length
        int target_length = player.experienceLevel + 1;
        int current_length = subEntities.size();

        while(target_length > current_length){ //少ない場合は追加する
            PlayerPart playerPart = new PlayerPart(player,"part"+partID,1f,1f);
            subEntities.add(playerPart);
            partID++;
            current_length = subEntities.size();
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
        Vec3 vec3 = calculateOffsetPosition(player, 1);

        // addition of position
        if(subPositions.size()==0){
            subPositions.addFirst(vec3);
        }else{
            Vec3 vec31 = subPositions.getFirst();
            if(vec3.distanceTo(vec31)>0.75f) subPositions.addFirst(vec3);
        }

        // allocate same size with subEntities
        int size = subEntities.size();
        while(subPositions.size() > size){
            if(size==0)break;
            subPositions.removeLast();
        }
        while(subPositions.size() < size){
            subPositions.addLast(subPositions.get(subPositions.size()-1));
        }

        // update position of each partEntity
        int id = 0;
        for(PlayerPart playerPart:subEntities){
            if(subPositions.size()==0)break;

            Vec3 vec31 = subPositions.get(id);
            playerPart.setPos(vec31);


            playerPart.setOldPosAndRot();
            if(id+1 < subPositions.size()){
                id++;
            }

            Level level = playerPart.level();
            AABB boundingBox = playerPart.getBoundingBox();
            List<Entity> entities = level.getEntities(playerPart, boundingBox, entity -> (entity instanceof LivingEntity && entity.getId()!=player.getId()));

            Holder<DamageType> damageTypeHolder = level.registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypes.MOB_ATTACK);

            for(Entity entity : entities){
                DamageSource damageSource = new DamageSource(damageTypeHolder, (Player)(Object)this);
                float damageAmount = 1.0F; // ダメージ量
                entity.hurt(damageSource, damageAmount);
            }
        }
//        System.out.println(player.experienceLevel);
    }



    @Override
    public List<PlayerPart> getPlayerParts(){
        return subEntities;
    }
    @Inject(method="getDimensions",at=@At("HEAD"),cancellable = true)
    public void getDimensions(Pose p_36166_, CallbackInfoReturnable<EntityDimensions> cir) {
        EntityDimensions entityDimensions = EntityDimensions.scalable(1F, 1F);
        cir.setReturnValue(entityDimensions);
        cir.cancel();
    }
    @Inject(method = "getStandingEyeHeight", at=@At("HEAD"),cancellable = true)
    public void getStandingEyeHeight(Pose p_36259_, EntityDimensions p_36260_, CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(0.75F);
        cir.cancel();
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public net.minecraftforge.entity.PartEntity<?>[] getParts() {
        return this.subEntities.toArray(new PartEntity<?>[0]);
    }

}
