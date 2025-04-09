package com.github.tacowasa059.snakeplayer.client.event;

import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.SnakePlayer;
import com.github.tacowasa059.snakeplayer.client.renderers.HexagonalPrismRenderer;
import com.github.tacowasa059.snakeplayer.client.renderers.SphereRenderer;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SnakePlayer.MODID,value = Dist.CLIENT)
public class ClientRenderListener {
    /**
     * 一人称視点での手の描画をキャンセル
     * @param event RenderHandEvent
     */
    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        AbstractClientPlayer player = minecraft.player;

        if (minecraft.player == null) {
            return;
        }
        IPlayerData playerData = (IPlayerData) player;
        if(!playerData.snakePlayer$getIsSnake()){
            return;
        }
        event.setCanceled(true); //snakeのときはキャンセル
    }

    /**
     * 三人称視点(Third Perspective Renderer)
     * @param event RenderPlayerEvent.Pre
     */
    @SubscribeEvent
    public static void onClientRender(RenderPlayerEvent.Pre event){
        PoseStack poseStack = event.getPoseStack();
        Frustum frustum = Minecraft.getInstance().levelRenderer.getFrustum();

        Player player = event.getEntity();
        if(player.isSpectator())return;
        IPlayerData playerData = (IPlayerData) player;
        if(!playerData.snakePlayer$getIsSnake())return;

        List<PlayerPart> playerParts = playerData.snakePlayer$getPlayerParts();

        float partialTicks = event.getPartialTick();
        int lightmap = event.getPackedLight();

        MultiBufferSource bufferSource = event.getMultiBufferSource();

        Vec3 playerVec = player.getPosition(partialTicks);

        int overlay = OverlayTexture.NO_OVERLAY;
        if(player.hurtTime > 0 || player.deathTime > 0) overlay = OverlayTexture.pack(
                OverlayTexture.u(event.getPartialTick()), OverlayTexture.v(player.hurtTime > 0 || player.deathTime > 0));


        ResourceLocation location = ((AbstractClientPlayer)player).getSkinTextureLocation();

        float radius = playerData.snakePlayer$getHeadSize()/2;

        AABB aabb = getAABB(player);

        if(frustum.isVisible(aabb)){
            poseStack.pushPose();
            poseStack.translate(0, radius,0);
            float yaw = player.getYRot();    // 水平方向の回転
            float pitch = player.getXRot();  // 上下方向の回転

            poseStack.mulPose(Axis.YP.rotationDegrees(-yaw)); // Y軸回りの回転
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch)); // Z軸回りの回転
            SphereRenderer.drawTexturedSphere(poseStack,bufferSource, location, radius,8,0,0, lightmap,true, overlay);
            poseStack.popPose();
        }


        int i = 0;
        float segmentsize = playerData.snakePlayer$getBodySegmentSize()/2;
        for(PlayerPart playerPart : playerParts){

            AABB aabb2 = getAABB(playerPart);
            if(frustum.isVisible(aabb2)){
                Vec3 vec3 = playerPart.getPosition(partialTicks).subtract(playerVec).add(0, segmentsize,0); //segment size
                Vec3 targetVec3 = player.getPosition(partialTicks);

                if(i > 0) targetVec3 = playerParts.get(i-1).getPosition(partialTicks);
                targetVec3 = targetVec3.subtract(playerVec).add(0, segmentsize,0);

                HexagonalPrismRenderer.renderHexagonalPrism((AbstractClientPlayer) player, poseStack, bufferSource, segmentsize, vec3, targetVec3, lightmap, overlay);
            }
            i++;
        }

        event.setCanceled(true);
    }

    /**
     * 一人称視点(First Perspective Renderer)
     * @param event RenderLevelStageEvent.Stage.AFTER_WEATHER
     */
    @SubscribeEvent
    public static void onFirstPerspectiveRender(RenderLevelStageEvent event) {
        if(!event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_WEATHER))return;
        Minecraft minecraft = Minecraft.getInstance();
        AbstractClientPlayer player = minecraft.player;

        if (minecraft.player == null || minecraft.options.getCameraType() != CameraType.FIRST_PERSON ||player.isSpectator()) {
            return;
        }
        IPlayerData playerData = (IPlayerData) player;
        if(!playerData.snakePlayer$getIsSnake())return;


        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();


        List<PlayerPart> playerParts = playerData.snakePlayer$getPlayerParts();

        float partialTicks = event.getPartialTick();

        int overlay = OverlayTexture.NO_OVERLAY;
        if(player.hurtTime > 0 || player.deathTime > 0)overlay = OverlayTexture.pack(OverlayTexture.u(event.getPartialTick()),
                OverlayTexture.v(player.hurtTime > 0 || player.deathTime > 0));

        int lightmap = LevelRenderer.getLightColor(player.level(), player.getOnPos().above());

        RenderSystem.getModelViewStack().popPose();  // 行列を元に戻す
        RenderSystem.applyModelViewMatrix();  // 元の状態を再適用

        float segmentsize = playerData.snakePlayer$getBodySegmentSize()/2;
        int i = 0;
        for(PlayerPart playerPart:playerParts){
            Vec3 vec3 = playerPart.getPosition(partialTicks).subtract(cameraPos).add(0, segmentsize,0);
            Vec3 targetVec3 = player.getPosition(partialTicks);
            if(i > 0) targetVec3 = playerParts.get(i-1).getPosition(partialTicks);
            targetVec3 = targetVec3.subtract(cameraPos).add(0, segmentsize,0);

            HexagonalPrismRenderer.renderHexagonalPrism(player, poseStack, bufferSource, segmentsize, vec3, targetVec3, lightmap, overlay);
            i++;
        }

        RenderSystem.getModelViewStack().pushPose();  // 行列を積む
        RenderSystem.getModelViewStack().mulPoseMatrix(poseStack.last().pose());  // 新しい行列を適用
        RenderSystem.applyModelViewMatrix();  // 新しい行列を適用
    }


    /**
     * For frustum culling (カリングの計算用)
     * @param player T player
     * @return AABB
     * @param <T> <T extends Entity>
     */
    private static <T extends Entity> AABB getAABB(T player) {
        AABB aabb = player.getBoundingBoxForCulling().inflate(0.5D);
        if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
            aabb = new AABB(player.getX() - 2.0D, player.getY() - 2.0D, player.getZ() - 2.0D,
                    player.getX() + 2.0D, player.getY() + 2.0D, player.getZ() + 2.0D);
        }
        return aabb;
    }
}
