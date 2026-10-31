package com.github.tacowasa059.snakeplayer.client.renderers;

import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class ConnectedBodyRenderer {

    private static final int SIDES = 6;
    private static final float ANGLE_INCREMENT = 360.0f / SIDES;
    private static final float[] COS_ANGLES = new float[SIDES];
    private static final float[] SIN_ANGLES = new float[SIDES];

    static {
        for (int i = 0; i < SIDES; i++) {
            float angle = -(float) Math.toRadians(i * ANGLE_INCREMENT - 360f / 12f);
            COS_ANGLES[i] = (float) Math.cos(angle);
            SIN_ANGLES[i] = (float) Math.sin(angle);
        }
    }

    public static void renderConnectedBody(
            AbstractClientPlayer player,
            PoseStack poseStack,
            SnakeRenderSink sink,
            float radius,
            List<PlayerPart> playerParts,
            Vec3 origin,
            float partialTicks,
            int lightmap,
            int overlay
    ) {
        if (playerParts == null || playerParts.isEmpty()) {
            return;
        }

        List<Vec3> nodes = new ArrayList<>(playerParts.size() + 1);
        nodes.add(player.getPosition(partialTicks).subtract(origin).add(0, radius, 0));
        for (PlayerPart part : playerParts) {
            nodes.add(part.getPosition(partialTicks).subtract(origin).add(0, radius, 0));
        }

        int nodeCount = nodes.size();
        if (nodeCount < 2) {
            return;
        }

        Vec3[] dirs = new Vec3[nodeCount];
        for (int k = 0; k < nodeCount; k++) {
            Vec3 dir;
            if (k == 0) {
                dir = nodes.get(0).subtract(nodes.get(1));
            } else if (k == nodeCount - 1) {
                dir = nodes.get(nodeCount - 2).subtract(nodes.get(nodeCount - 1));
            } else {
                Vec3 prev = nodes.get(k - 1).subtract(nodes.get(k));
                Vec3 next = nodes.get(k).subtract(nodes.get(k + 1));
                double lPrev = prev.length();
                double lNext = next.length();
                Vec3 vPrev = lPrev > 1e-5 ? prev.scale(1.0 / lPrev) : Vec3.ZERO;
                Vec3 vNext = lNext > 1e-5 ? next.scale(1.0 / lNext) : Vec3.ZERO;
                Vec3 sum = vPrev.add(vNext);
                if (sum.lengthSqr() > 1e-5) {
                    dir = sum;
                } else if (lPrev > 1e-5) {
                    dir = prev;
                } else {
                    dir = next;
                }
            }
            double len = dir.length();
            dirs[k] = len > 1e-5 ? dir.scale(1.0 / len) : new Vec3(1, 0, 0);
        }

        Vec3[] ups = new Vec3[nodeCount];
        Vec3[] sides = new Vec3[nodeCount];
        for (int k = 0; k < nodeCount; k++) {
            Vec3 dir = dirs[k];
            double horiz = Math.sqrt(dir.x * dir.x + dir.z * dir.z);
            double yaw = Math.atan2(dir.z, dir.x);
            double pitch = Math.atan2(dir.y, horiz);

            double sinYaw = Math.sin(yaw);
            double cosYaw = Math.cos(yaw);
            double sinPitch = Math.sin(pitch);
            double cosPitch = Math.cos(pitch);

            ups[k] = new Vec3(-sinPitch * cosYaw, cosPitch, -sinPitch * sinYaw);
            sides[k] = new Vec3(-sinYaw, 0, cosYaw);
        }

        Identifier location = player.getSkin().body().texturePath();
        sink.submit(poseStack, RenderTypes.entityTranslucent(location), (pose, vertexConsumer) -> {
            renderMesh(pose, lightmap, vertexConsumer, radius, false, 1.0f, overlay, nodes, dirs, ups, sides);
            renderMesh(pose, lightmap, vertexConsumer, radius, true, 1.05f, overlay, nodes, dirs, ups, sides);
        });
    }

    private static void renderMesh(
            PoseStack.Pose pose,
            int lightmap,
            VertexConsumer vertexConsumer,
            float radius,
            boolean isOuter,
            float mul,
            int overlay,
            List<Vec3> nodes,
            Vec3[] dirs,
            Vec3[] ups,
            Vec3[] sides
    ) {
        int nodeCount = nodes.size();

        // 胴体の接続面（各セグメント間を繋ぐ六角形チューブ）
        for (int k = 0; k < nodeCount - 1; k++) {
            for (int i = 0; i < SIDES; i++) {
                int iNext = (i + 1) % SIDES;

                Vec3 pTopNext = getVertex(nodes.get(k), ups[k], sides[k], radius, mul, iNext);
                Vec3 pTopCurr = getVertex(nodes.get(k), ups[k], sides[k], radius, mul, i);
                Vec3 pBottomCurr = getVertex(nodes.get(k + 1), ups[k + 1], sides[k + 1], radius, mul, i);
                Vec3 pBottomNext = getVertex(nodes.get(k + 1), ups[k + 1], sides[k + 1], radius, mul, iNext);

                Vec3 nTopNext = getNormal(ups[k], sides[k], iNext);
                Vec3 nTopCurr = getNormal(ups[k], sides[k], i);
                Vec3 nBottomCurr = getNormal(ups[k + 1], sides[k + 1], i);
                Vec3 nBottomNext = getNormal(ups[k + 1], sides[k + 1], iNext);

                int u1 = 16 + 4 * i;
                int u2 = u1 + 4;
                int v1 = 20;
                int v2 = 32;
                if (isOuter) {
                    v1 += 16;
                    v2 += 16;
                }

                addVertex(vertexConsumer, pose, pTopNext, u2 / 64f, v1 / 64f, nTopNext, lightmap, overlay);
                addVertex(vertexConsumer, pose, pTopCurr, u1 / 64f, v1 / 64f, nTopCurr, lightmap, overlay);
                addVertex(vertexConsumer, pose, pBottomCurr, u1 / 64f, v2 / 64f, nBottomCurr, lightmap, overlay);
                addVertex(vertexConsumer, pose, pBottomNext, u2 / 64f, v2 / 64f, nBottomNext, lightmap, overlay);
            }
        }

        // 端面のキャップ（頭側の前向きフタと尻尾側の後ろ向きフタ）
        int u0 = 24;
        int v0 = 18;
        int[] u_list = new int[]{20, 20, 24, 28, 28, 24, 20};
        int[] v_list = new int[]{16, 20, 20, 20, 17, 16, 17};

        if (isOuter) {
            v0 += 16;
            for (int i = 0; i < v_list.length; i++) {
                v_list[i] += 16;
            }
        }

        // 頭側キャップ (k = 0, 前向きフタ)
        Vec3 frontNormal = dirs[0];
        Vec3 frontCenter = nodes.get(0);
        for (int i = 0; i < SIDES; i += 2) {
            Vec3 p1 = getVertex(frontCenter, ups[0], sides[0], radius, mul, i);
            Vec3 p2 = getVertex(frontCenter, ups[0], sides[0], radius, mul, (i + 1) % SIDES);
            Vec3 p3 = getVertex(frontCenter, ups[0], sides[0], radius, mul, (i + 2) % SIDES);

            addVertex(vertexConsumer, pose, frontCenter, u0 / 64f, v0 / 64f, frontNormal, lightmap, overlay);
            addVertex(vertexConsumer, pose, p1, u_list[i] / 64f, v_list[i] / 64f, frontNormal, lightmap, overlay);
            addVertex(vertexConsumer, pose, p2, u_list[i + 1] / 64f, v_list[i + 1] / 64f, frontNormal, lightmap, overlay);
            addVertex(vertexConsumer, pose, p3, u_list[i + 2] / 64f, v_list[i + 2] / 64f, frontNormal, lightmap, overlay);
        }

        // 尻尾側キャップ (k = nodeCount - 1, 後ろ向きフタ)
        int lastIdx = nodeCount - 1;
        Vec3 backNormal = dirs[lastIdx].scale(-1);
        Vec3 backCenter = nodes.get(lastIdx);
        for (int i = 0; i < SIDES; i += 2) {
            Vec3 p1 = getVertex(backCenter, ups[lastIdx], sides[lastIdx], radius, mul, i);
            Vec3 p2 = getVertex(backCenter, ups[lastIdx], sides[lastIdx], radius, mul, (i + 1) % SIDES);
            Vec3 p3 = getVertex(backCenter, ups[lastIdx], sides[lastIdx], radius, mul, (i + 2) % SIDES);

            addVertex(vertexConsumer, pose, p1, (u_list[i] + 8) / 64f, v_list[i] / 64f, backNormal, lightmap, overlay);
            addVertex(vertexConsumer, pose, backCenter, (u0 + 8) / 64f, v0 / 64f, backNormal, lightmap, overlay);
            addVertex(vertexConsumer, pose, p3, (u_list[i + 2] + 8) / 64f, v_list[i + 2] / 64f, backNormal, lightmap, overlay);
            addVertex(vertexConsumer, pose, p2, (u_list[i + 1] + 8) / 64f, v_list[i + 1] / 64f, backNormal, lightmap, overlay);
        }
    }

    private static Vec3 getVertex(Vec3 center, Vec3 up, Vec3 side, float radius, float mul, int i) {
        return center
                .add(up.scale(radius * SIN_ANGLES[i] * mul))
                .add(side.scale(radius * COS_ANGLES[i] * mul));
    }

    private static Vec3 getNormal(Vec3 up, Vec3 side, int i) {
        return up.scale(SIN_ANGLES[i]).add(side.scale(COS_ANGLES[i])).normalize();
    }

    private static void addVertex(
            VertexConsumer vertexConsumer,
            PoseStack.Pose pose,
            Vec3 pos,
            float u,
            float v,
            Vec3 normal,
            int lightmap,
            int overlay
    ) {
        Matrix4f matrix = pose.pose();
        vertexConsumer.addVertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(lightmap)
                .setNormal(pose, (float) normal.x, (float) normal.y, (float) normal.z);
    }
}
