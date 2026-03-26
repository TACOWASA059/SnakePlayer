package com.github.tacowasa059.snakeplayer.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class HexagonalPrismRenderer {

    public static void renderHexagonalPrism(AbstractClientPlayer player, PoseStack poseStack, MultiBufferSource bufferSource, float radius, Vec3 base, Vec3 target, int lightmap, int overlay) {
        ResourceLocation location = player.getSkin().texture();

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(location));
        poseStack.pushPose();
        poseStack.translate(base.x, base.y, base.z);

        alignPoseStack(poseStack, base, target);


        renderCylinder(poseStack, lightmap, vertexConsumer, radius, false, 1f, overlay);
        renderCylinder(poseStack, lightmap, vertexConsumer, radius, true, 1.05f, overlay);


        poseStack.popPose();
    }

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

            float x1Top = side_length * mul / 2;
            float x1Bottom = - side_length * mul / 2;

            addQuad(vertexConsumer, poseStack, x1Top, 0, 0,u0/64f, v0/64f, x1Top, y1, z1, u_list[i]/64f, v_list[i]/64f , x1Top, y2, z2,u_list[i+1]/64f, v_list[i+1]/64f, x1Top, y3, z3, u_list[i+2]/64f, v_list[i+2]/64f, lightmap, overlay);
            addQuad(vertexConsumer, poseStack, x1Bottom, y1, z1, (u_list[i]+8)/64f, v_list[i]/64f ,x1Bottom, 0, 0,(u0+8)/64f, v0/64f, x1Bottom, y3, z3, (u_list[i+2]+8)/64f, v_list[i+2]/64f, x1Bottom, y2, z2,(u_list[i+1]+8)/64f, v_list[i+1]/64f, lightmap, overlay);
        }
    }

    private static void addQuad(VertexConsumer vertexConsumer, PoseStack poseStack, float x1, float y1, float z1,float u1, float v1, float x2, float y2, float z2,float u2, float v2, float x3, float y3, float z3,float u3, float v3, float x4, float y4, float z4,float u4, float v4, int lightmap, int overlay) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = poseStack.last().pose();

        vertexConsumer.addVertex(matrix, x1, y1, z1)
                .setColor(255, 255, 255, 255)
                .setUv(u1, v1)
                .setOverlay(overlay)
                .setLight(lightmap)
                .setNormal(pose, 0, 1, 0);

        vertexConsumer.addVertex(matrix, x2, y2, z2)
                .setColor(255, 255, 255, 255)
                .setUv(u2, v2)
                .setOverlay(overlay)
                .setLight(lightmap)
                .setNormal(pose, 0, 1, 0);

        vertexConsumer.addVertex(matrix, x3, y3, z3)
                .setColor(255, 255, 255, 255)
                .setUv(u3, v3)
                .setOverlay(overlay)
                .setLight(lightmap)
                .setNormal(pose, 0, 1, 0);

        vertexConsumer.addVertex(matrix, x4, y4, z4)
                .setColor(255, 255, 255, 255)
                .setUv(u4, v4)
                .setOverlay(overlay)
                .setLight(lightmap)
                .setNormal(pose, 0, 1, 0);
    }

    private static void alignPoseStack(PoseStack poseStack, Vec3 base, Vec3 target) {
        Vec3 direction = target.subtract(base).normalize();

        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x));
        float pitch = (float) Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));
    }
}
