package com.github.tacowasa059.snakeplayer.client.event;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.SnakePlayer;
import com.github.tacowasa059.snakeplayer.client.utils.SphereRenderer;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
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
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

@Mod.EventBusSubscriber(modid = SnakePlayer.MODID,value = Dist.CLIENT)
public class ClientRenderListener {
    /**
     * 手の描画をキャンセル
     * @param event
     */
    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        AbstractClientPlayer player = minecraft.player;

        if (minecraft.player == null) {
            return;
        }
        IPlayerData playerData = (IPlayerData) player;
        if(!playerData.getIsSnake()){
            return;
        }
        event.setCanceled(true); //snakeのときはキャンセル
    }
    @SubscribeEvent
    public static void onClientRender(RenderPlayerEvent.Pre event){
        PoseStack poseStack = event.getPoseStack();
        Frustum frustum = Minecraft.getInstance().levelRenderer.getFrustum();

        Player player = event.getEntity();
        if(player.isSpectator())return;
        IPlayerData playerData = (IPlayerData) player;
        if(!playerData.getIsSnake())return;

        List<PlayerPart> playerParts = playerData.getPlayerParts();

        float partialTicks = event.getPartialTick();
        int lightmap = event.getPackedLight();

        MultiBufferSource bufferSource = event.getMultiBufferSource();

        Vec3 playerVec = player.getPosition(partialTicks);

        int overlay = OverlayTexture.NO_OVERLAY;
        if(player.hurtTime > 0 || player.deathTime > 0)overlay=OverlayTexture.pack(
                OverlayTexture.u(event.getPartialTick()), OverlayTexture.v(player.hurtTime > 0 || player.deathTime > 0));


        ResourceLocation location = ((AbstractClientPlayer)player).getSkinTextureLocation();

        float radius = playerData.getHeadSize()/2;

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



//        double displace = playerVec.subtract(player.getPosition(0)).length();

        int i = 0;
        float segmentsize = playerData.getBodySegmentSize()/2;
        for(PlayerPart playerPart:playerParts){

            AABB aabb2 = getAABB(playerPart);
            if(frustum.isVisible(aabb2)){
                Vec3 vec3 = playerPart.getPosition(partialTicks).subtract(playerVec).add(0,segmentsize,0);
                Vec3 targetVec3 = player.getPosition(partialTicks);
                if(i>0) targetVec3 = playerParts.get(i-1).getPosition(partialTicks);
                targetVec3 = targetVec3.subtract(playerVec).add(0,segmentsize,0);

                renderOctagonalPrism((AbstractClientPlayer) player, poseStack, bufferSource, segmentsize, vec3, targetVec3, lightmap, overlay);
            }
            i++;
        }

        event.setCanceled(true);
    }

    private static <T extends Entity> AABB getAABB(T player) {
        AABB aabb = player.getBoundingBoxForCulling().inflate(0.5D);
        if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
            aabb = new AABB(player.getX() - 2.0D, player.getY() - 2.0D, player.getZ() - 2.0D, player.getX() + 2.0D, player.getY() + 2.0D, player.getZ() + 2.0D);
        }
        return aabb;
    }

    @SubscribeEvent
    public static void onFirstPerspectiveRender(RenderLevelStageEvent event) {
        if(!event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_WEATHER))return;
        Minecraft minecraft = Minecraft.getInstance();
        AbstractClientPlayer player = minecraft.player;

        if (minecraft.player == null || minecraft.options.getCameraType() != CameraType.FIRST_PERSON) {
            return;
        }
        IPlayerData playerData = (IPlayerData) player;
        if(!playerData.getIsSnake())return;


        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();


        List<PlayerPart> playerParts = playerData.getPlayerParts();
        float radius = playerData.getHeadSize()/2;

        float partialTicks = event.getPartialTick();

        int overlay = OverlayTexture.NO_OVERLAY;
        if(player.hurtTime > 0 || player.deathTime > 0)overlay=OverlayTexture.pack(OverlayTexture.u(event.getPartialTick()), OverlayTexture.v(player.hurtTime > 0 || player.deathTime > 0));

        int lightmap = LevelRenderer.getLightColor(player.level(), player.getOnPos().above());

        RenderSystem.getModelViewStack().popPose();  // 行列を元に戻す
        RenderSystem.applyModelViewMatrix();  // 元の状態を再適用

        float segmentsize = playerData.getBodySegmentSize()/2;
        int i = 0;
        for(PlayerPart playerPart:playerParts){
            Vec3 vec3 = playerPart.getPosition(0).subtract(cameraPos).add(0,radius,0);
            Vec3 targetVec3 = player.getPosition(partialTicks);
            if(i > 0) targetVec3 = playerParts.get(i-1).getPosition(partialTicks);
            targetVec3 = targetVec3.subtract(cameraPos).add(0,radius,0);

            renderOctagonalPrism(player, poseStack, bufferSource, segmentsize, vec3, targetVec3, lightmap, overlay);
            i++;
        }

        RenderSystem.getModelViewStack().pushPose();  // 行列を積む
        RenderSystem.getModelViewStack().mulPoseMatrix(poseStack.last().pose());  // 新しい行列を適用
        RenderSystem.applyModelViewMatrix();  // 新しい行列を適用
    }

    /**
     * render cylinder 2 layer
     * @param player
     * @param poseStack
     * @param bufferSource
     * @param base
     * @param target
     * @param lightmap
     * @param overlay
     */
    private static void renderOctagonalPrism(AbstractClientPlayer player, PoseStack poseStack, MultiBufferSource bufferSource, float radius, Vec3 base, Vec3 target, int lightmap, int overlay) {
        ResourceLocation location = player.getSkinTextureLocation();

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(location)); //cullingあり

        // 座標系を平行移動して回転
        poseStack.pushPose();
        poseStack.translate(base.x, base.y, base.z);

        alignPoseStack(poseStack, base, target);


        renderCylinder(poseStack, lightmap, vertexConsumer, radius, false, 1f, overlay);
        renderCylinder(poseStack, lightmap, vertexConsumer, radius, true, 1.05f, overlay);


        poseStack.popPose();
    }

    /**
     * render cylinder 1 layer
     * @param poseStack
     * @param lightmap
     * @param vertexConsumer
     * @param radius radius of the cylider
     * @param isOuter outer layer or inner layer
     * @param mul ratio of outer layer and inner layer
     * @param overlay
     */
    private static void renderCylinder(PoseStack poseStack, int lightmap, VertexConsumer vertexConsumer, float radius, boolean isOuter, float mul, int overlay) {
        float side_length = radius * 2;
        int sides = 6;
        float angleIncrement = 360.0f / sides;

        for (int i = 0; i < sides; i++) {
            float angle1 = -(float) Math.toRadians(i * angleIncrement - 360f*1/12);
            float angle2 = -(float) Math.toRadians((i + 1) % sides * angleIncrement - 360f*1/12);

            float z1 = radius * (float) Math.cos(angle1) * mul;
            float y1 = radius * (float) Math.sin(angle1) * mul;
            float z2 = radius * (float) Math.cos(angle2) * mul;
            float y2 = radius * (float) Math.sin(angle2) * mul;

            float x1Bottom = side_length * mul / 2;
            float x1Top = -side_length * mul / 2;

            int u1 = 16 + 4*i;
            int u2 = u1 + 4;
            int v1 = 20;
            int v2 = 32;
            if(isOuter) {
                v1 += 16;
                v2 += 16;
            }

            addQuad(vertexConsumer, poseStack, x1Bottom, y2, z2, u2/64f, v1/64f ,x1Bottom, y1, z1,u1/64f, v1/64f, x1Top, y1, z1, u1/64f, v2/64f,x1Top, y2, z2,u2/64f, v2/64f, lightmap, overlay);
        }

        int u0 = 24;
        int v0 = 18;
        int[] u_list = new int[]{20,20,24,28,28,24,20};
        int[] v_list = new int[]{16,20,20,20,17,16,17};

        if(isOuter){
            v0 +=16;
            for(int i=0;i<v_list.length;i++){
                v_list[i] += 16;
            }
        }

        for (int i = 0; i < sides; i+=2) {
            float angle1 = -(float) Math.toRadians(i * angleIncrement - 360f * 1/12);
            float angle2 = -(float) Math.toRadians((i + 1) % sides * angleIncrement - 360f * 1/12);
            float angle3 = -(float) Math.toRadians((i + 2) % sides * angleIncrement - 360f * 1/12);

            float z1 = radius * (float) Math.cos(angle1);
            float y1 = radius * (float) Math.sin(angle1);
            float z2 = radius * (float) Math.cos(angle2);
            float y2 = radius * (float) Math.sin(angle2);
            float z3 = radius * (float) Math.cos(angle3);
            float y3 = radius * (float) Math.sin(angle3);

            float x1Top = side_length * mul / 2;;
            float x1Bottom = - side_length * mul / 2;

            addQuad(vertexConsumer, poseStack, x1Top, 0, 0,u0/64f, v0/64f, x1Top, y1, z1, u_list[i]/64f, v_list[i]/64f , x1Top, y2, z2,u_list[i+1]/64f, v_list[i+1]/64f, x1Top, y3, z3, u_list[i+2]/64f, v_list[i+2]/64f, lightmap, overlay);
            addQuad(vertexConsumer, poseStack, x1Bottom, y1, z1, (u_list[i]+8)/64f, v_list[i]/64f ,x1Bottom, 0, 0,(u0+8)/64f, v0/64f, x1Bottom, y3, z3, (u_list[i+2]+8)/64f, v_list[i+2]/64f, x1Bottom, y2, z2,(u_list[i+1]+8)/64f, v_list[i+1]/64f, lightmap, overlay);
        }
    }

    /**
     * 四角形を描画
     */
    private static void addQuad(VertexConsumer vertexConsumer, PoseStack poseStack, float x1, float y1, float z1,float u1, float v1, float x2, float y2, float z2,float u2, float v2, float x3, float y3, float z3,float u3, float v3, float x4, float y4, float z4,float u4, float v4, int lightmap, int overlay) {
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        vertexConsumer.vertex(matrix, x1, y1, z1)
                .color(255, 255, 255, 255) // 色: 白、不透明
                .uv(u1, v1)                // テクスチャ座標: 下端
                .overlayCoords(overlay)      // オーバーレイ座標
                .uv2(lightmap)            // ライトマップ座標
                .normal(normalMatrix, 0, 1, 0) // 法線
                .endVertex();

        vertexConsumer.vertex(matrix, x2, y2, z2)
                .color(255, 255, 255, 255)
                .uv(u2, v2)
                .overlayCoords(overlay)
                .uv2(lightmap)
                .normal(normalMatrix, 0, 1, 0)
                .endVertex();

        vertexConsumer.vertex(matrix, x3, y3, z3)
                .color(255, 255, 255, 255)
                .uv(u3, v3)
                .overlayCoords(overlay)
                .uv2(lightmap)
                .normal(normalMatrix, 0, 1, 0)
                .endVertex();

        vertexConsumer.vertex(matrix, x4, y4, z4)
                .color(255, 255, 255, 255)
                .uv(u4,v4)
                .overlayCoords(overlay)
                .uv2(lightmap)
                .normal(normalMatrix, 0, 1, 0)
                .endVertex();
    }

    /**
     * PoseStackを基準点baseとtargetの方向に揃える
     */
    private static void alignPoseStack(PoseStack poseStack, Vec3 base, Vec3 target) {
        Vec3 direction = target.subtract(base).normalize();

        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x));
        float pitch = (float) Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw)); // Y軸回りの回転
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch)); // Z軸回りの回転
    }
}
