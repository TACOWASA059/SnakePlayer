package com.github.tacowasa059.snakeplayer.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SphereRenderer {
    public static void drawTexturedSphere(PoseStack poseStack, MultiBufferSource buffer, ResourceLocation texture, float radius, int segments, float x, float z, int packedLight, boolean lightmap2, int overlay) {
        Matrix4f positionMatrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();
        poseStack.mulPose(Axis.YP.rotationDegrees(45));

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
        addBottomSphere(radius, segments, x, z, vertexConsumer, positionMatrix, normalMatrix,0.1875F, false,packedLight,lightmap2,overlay);
        addBottomSphere(radius*1.01F, segments, x, z, vertexConsumer, positionMatrix,normalMatrix,0.1875F+0.5F, false,packedLight,lightmap2,overlay);

        addBottomSphere(radius, segments, x, z, vertexConsumer, positionMatrix, normalMatrix,0.1875F+0.125F, true,packedLight,lightmap2,overlay);
        addBottomSphere(radius*1.01F, segments, x, z, vertexConsumer, positionMatrix, normalMatrix,0.1875F+0.125F+0.5F, true,packedLight,lightmap2,overlay);
        addSideSphere(radius, segments, x, z, vertexConsumer,positionMatrix,normalMatrix,false,packedLight,lightmap2,overlay);
        addSideSphere(radius*1.01F, segments, x, z, vertexConsumer,positionMatrix,normalMatrix,true,packedLight,lightmap2,overlay);

        poseStack.mulPose(Axis.YP.rotationDegrees(-45));
    }
    private static void addBottomSphere(float radius, int segments, float x, float z, VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, float u0, boolean isLower, int packedLight, boolean lightmap2, int overlay) {
        float cube_size=0.0625F;//texture
        for (int j = 0; j< Math.round(segments/4f);j++) {
            float theta= (float) (Math.PI*j/(4 * Math.round(segments/4f)));
            float sin_theta= (float) Math.sin(theta);
            float cos_theta= (float) Math.cos(theta);

            float next_theta= (float) (Math.PI*(j+1)/(4 * Math.round(segments/4f)));
            float next_sin_theta= (float) Math.sin(next_theta);
            float next_cos_theta= (float) Math.cos(next_theta);

            for (int i = 0; i < segments; i++) {
                double angle = -Math.PI * 2 * i / segments;
                double next_angle = -Math.PI * 2 * ((i + 1)) / segments;
                if(isLower){
                    angle=-angle;
                    next_angle=-next_angle;
                }

                float dx = (float) ((radius*sin_theta * Math.cos(angle)));
                float dz = (float) ((radius*sin_theta * Math.sin(angle)));
                float next_i_dx = (float) ((radius*sin_theta * Math.cos(next_angle)));
                float next_i_dz = (float) ((radius*sin_theta * Math.sin(next_angle)));
                float next_j_dx = (float) ((radius*next_sin_theta * Math.cos(angle)));
                float next_j_dz = (float) ((radius*next_sin_theta * Math.sin(angle)));
                float next_ij_dx = (float) ((radius*next_sin_theta * Math.cos(next_angle)));
                float next_ij_dz = (float) ((radius*next_sin_theta * Math.sin(next_angle)));

                float[]pos_list;

                if(isLower){
                    pos_list=new float[]{
                            dx, - radius*cos_theta,dz,
                            next_i_dx, - radius*cos_theta,next_i_dz,
                            next_j_dx, - radius*next_cos_theta,next_j_dz,
                            next_ij_dx,- radius*next_cos_theta,next_ij_dz
                    };
                }
                else{
                    pos_list=new float[]{
                            dx, radius * cos_theta, dz,
                            next_i_dx, radius * cos_theta, next_i_dz,
                            next_j_dx, radius * next_cos_theta, next_j_dz,
                            next_ij_dx, radius * next_cos_theta, next_ij_dz
                    };
                }



                float x1 = (float) Math.cos(angle - Math.PI / 4)*sin_theta * (float)Math.sqrt(2.0F);
                float z1 = (float) Math.sin(angle - Math.PI / 4)*sin_theta * (float)Math.sqrt(2.0F);
                float next_i_x1 = (float) Math.cos(next_angle - Math.PI / 4)*sin_theta * (float)Math.sqrt(2.0F);
                float next_i_z1 = (float) Math.sin(next_angle - Math.PI / 4)*sin_theta * (float)Math.sqrt(2.0F);
                float next_j_x1 = (float) Math.cos(angle - Math.PI / 4)*next_sin_theta * (float)Math.sqrt(2.0F);
                float next_j_z1 = (float) Math.sin(angle - Math.PI / 4)*next_sin_theta * (float)Math.sqrt(2.0F);
                float next_ij_x1 = (float) Math.cos(next_angle - Math.PI / 4)*next_sin_theta * (float)Math.sqrt(2.0F);
                float next_ij_z1 = (float) Math.sin(next_angle - Math.PI / 4)*next_sin_theta * (float)Math.sqrt(2.0F);


                float square_x = getSquareX(x1, z1, cube_size);
                float square_z = getSquareZ(x1, z1, cube_size);
                float next_i_square_x = getSquareX(next_i_x1, next_i_z1, cube_size);
                float next_i_square_z = getSquareZ(next_i_x1, next_i_z1, cube_size);
                float next_j_square_x = getSquareX(next_j_x1, next_j_z1, cube_size);
                float next_j_square_z = getSquareZ(next_j_x1, next_j_z1, cube_size);
                float next_ij_square_x = getSquareX(next_ij_x1, next_ij_z1, cube_size);
                float next_ij_square_z = getSquareZ(next_ij_x1, next_ij_z1, cube_size);


                float u = u0 + square_x;
                float v = (float) 0.0625 + square_z;
                float next_i_u = u0 + next_i_square_x;
                float next_i_v = (float) 0.0625 + next_i_square_z;
                float next_j_u = u0 + next_j_square_x;
                float next_j_v = (float) 0.0625 + next_j_square_z;
                float next_ij_u = u0 + next_ij_square_x;
                float next_ij_v = (float) 0.0625 + next_ij_square_z;

                float[]uv_list=new float[]{
                        u,v,next_i_u,next_i_v,next_j_u,next_j_v,next_ij_u,next_ij_v
                };
                addSideSphereQuads(vertexConsumer, positionMatrix, normalMatrix, x, z, pos_list,uv_list,packedLight,lightmap2,overlay);
            }
        }
    }

    private static void addSideSphere(float radius, int segments, float x, float z, VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, boolean isInner, int packedLight, boolean lightmap2, int overlay) {
        for (int j = Math.round(segments/4f); j< Math.round(3*segments/4f);j++) {
            float theta= (float) (Math.PI*j/(4 * Math.round(segments/4f)));
            float sin_theta= (float) Math.sin(theta);
            float cos_theta= (float) Math.cos(theta);

            float next_theta= (float) (Math.PI*(j+1)/(4 * Math.round(segments/4f)));
            float next_sin_theta= (float) Math.sin(next_theta);
            float next_cos_theta= (float) Math.cos(next_theta);

            for (int i = 0; i < segments; i++) {//髮朱ｯ会ｽｽ・ｴ髯晢ｽｷ繝ｻ・ｳ髫ｴ繝ｻ・ｽ・ｹ髯ｷ・ｷ郢晢ｽｻ
                double angle = -Math.PI / 2 - Math.PI * 2 * i / segments;
                double next_angle = -Math.PI / 2 - Math.PI * 2 * ((i + 1)) / segments;

                float dx = (float) ((radius * Math.cos(angle) * sin_theta));
                float dz = (float) ((radius * Math.sin(angle) * sin_theta));
                float next_i_dx = (float) ((radius * Math.cos(next_angle)* sin_theta));
                float next_i_dz = (float) ((radius * Math.sin(next_angle)* sin_theta));
                float next_j_dx = (float) ((radius * Math.cos(angle) * next_sin_theta));
                float next_j_dz = (float) ((radius * Math.sin(angle) * next_sin_theta));
                float next_ij_dx = (float) ((radius * Math.cos(next_angle)* next_sin_theta));
                float next_ij_dz = (float) ((radius * Math.sin(next_angle)* next_sin_theta));

                float[]pos_list=new float[]{
                        dx,radius*cos_theta,dz,
                        next_i_dx,radius*cos_theta,next_i_dz,
                        next_j_dx,radius*next_cos_theta,next_j_dz,
                        next_ij_dx,radius*next_cos_theta,next_ij_dz
                };

                float u = 0.5F * i / segments;
                float v = 0.125F + 0.125F*(j-Math.round(segments/4f))/Math.round(segments/2f);
                float next_i_u = 0.5F * (i+1) / segments;
                float next_j_u = 0.5F * i / segments;
                float next_j_v = 0.125F + 0.125F*(j+1-Math.round(segments/4f))/Math.round(segments/2f);
                float next_ij_u = 0.5F * (i+1) / segments;

                if (isInner) {
                    u += 0.5F;
                    next_i_u += 0.5F;
                    next_j_u += 0.5F;
                    next_ij_u += 0.5F;
                }
                float[]uv_list=new float[]{
                        u,v,next_i_u, v,next_j_u,next_j_v,next_ij_u, next_j_v
                };

                addSideSphereQuads(vertexConsumer, positionMatrix, normalMatrix, x, z, pos_list, uv_list, packedLight,lightmap2,overlay);
            }
        }
    }
    private static void addSideSphereQuads(VertexConsumer vvertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, float x, float z, float[] pos_list, float[]uv_list, int packedLight, boolean lightmap2, int overlay) {
        if(lightmap2){
            vvertexConsumer.vertex(positionMatrix, pos_list[0]+x, pos_list[1]+ (float) 0.0, pos_list[2]+z)
                    .color(1.0f, 1.0f, 1.0f, 1.0f)
                    .uv(uv_list[0],uv_list[1]).overlayCoords(overlay).uv2(packedLight).normal(normalMatrix, pos_list[0],pos_list[1],pos_list[2]).endVertex();
            vvertexConsumer.vertex(positionMatrix, pos_list[3]+x, pos_list[4]+ (float) 0.0, pos_list[5]+z)
                    .color(1.0f, 1.0f, 1.0f, 1.0f)
                    .uv(uv_list[2],uv_list[3]).overlayCoords(overlay).uv2(packedLight).normal(normalMatrix, pos_list[3], pos_list[4], pos_list[5]).endVertex();
            vvertexConsumer.vertex(positionMatrix, pos_list[9]+x, pos_list[10]+ (float) 0.0, pos_list[11]+z)
                    .color(1.0f, 1.0f, 1.0f, 1.0f)
                    .uv(uv_list[6],uv_list[7]).overlayCoords(overlay).uv2(packedLight).normal(normalMatrix, pos_list[9], pos_list[10], pos_list[11]).endVertex();
            vvertexConsumer.vertex(positionMatrix, pos_list[6]+x, pos_list[7]+ (float) 0.0, pos_list[8]+z)
                    .color(1.0f, 1.0f, 1.0f, 1.0f)
                    .uv(uv_list[4],uv_list[5]).overlayCoords(overlay).uv2(packedLight).normal(normalMatrix, pos_list[6], pos_list[7], pos_list[8]).endVertex();
        }else{
            vvertexConsumer.vertex(positionMatrix, pos_list[0]+x, pos_list[1]+ (float) 0.0, pos_list[2]+z)
                    .color(1.0f, 1.0f, 1.0f, 1.0f)
                    .uv(uv_list[0],uv_list[1]).overlayCoords(overlay).uv2(packedLight).normal(pos_list[0],pos_list[1],pos_list[2]).endVertex();
            vvertexConsumer.vertex(positionMatrix, pos_list[3]+x, pos_list[4]+ (float) 0.0, pos_list[5]+z)
                    .color(1.0f, 1.0f, 1.0f, 1.0f)
                    .uv(uv_list[2],uv_list[3]).overlayCoords(overlay).uv2(packedLight).normal(pos_list[3], pos_list[4], pos_list[5]).endVertex();
            vvertexConsumer.vertex(positionMatrix, pos_list[9]+x, pos_list[10]+ (float) 0.0, pos_list[11]+z)
                    .color(1.0f, 1.0f, 1.0f, 1.0f)
                    .uv(uv_list[6],uv_list[7]).overlayCoords(overlay).uv2(packedLight).normal(pos_list[9], pos_list[10], pos_list[11]).endVertex();
            vvertexConsumer.vertex(positionMatrix, pos_list[6]+x, pos_list[7]+ (float) 0.0, pos_list[8]+z)
                    .color(1.0f, 1.0f, 1.0f, 1.0f)
                    .uv(uv_list[4],uv_list[5]).overlayCoords(overlay).uv2(packedLight).normal(pos_list[6], pos_list[7], pos_list[8]).endVertex();
        }
    }
    private static float getSquareZ(float x1, float z1, float cube_size) {
        return (float) (cube_size * (0.5 * Math.sqrt(2 - x1 * x1 + z1 * z1 + 2 * Math.sqrt(2.0) * z1) - 0.5 * Math.sqrt(2 - x1 * x1 + z1 * z1 - 2 * Math.sqrt(2.0) * z1)));
    }

    private static float getSquareX(float x1, float z1, float cube_size) {
        return (float) (cube_size * (0.5 * Math.sqrt(2 + x1 * x1 - z1 * z1 + 2 * Math.sqrt(2.0) * x1) - 0.5 * Math.sqrt(2 + x1 * x1 - z1 * z1 - 2 * Math.sqrt(2.0) * x1)));
    }
}
