package com.github.tacowasa059.snakeplayer.client.event;

import com.github.tacowasa059.snakeplayer.client.renderers.HexagonalPrismRenderer;
import com.github.tacowasa059.snakeplayer.client.renderers.SphereRenderer;
import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class ClientRenderListener {
    private ClientRenderListener() {
    }

    public static boolean shouldCancelHand(AbstractClientPlayer player) {
        return player != null && !player.isSpectator() && ((IPlayerData) player).snakePlayer$getIsSnake();
    }

    // プレイヤー本体の描画を球体の頭とボディセグメントへ置き換える。
    public static boolean renderPlayer(AbstractClientPlayer player, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks, int lightmap) {
        if (player.isSpectator()) {
            return false;
        }

        IPlayerData playerData = (IPlayerData) player;
        if (!playerData.snakePlayer$getIsSnake()) {
            return false;
        }

        List<PlayerPart> playerParts = playerData.snakePlayer$getPlayerParts();
        Vec3 playerVec = player.getPosition(partialTicks);
        int overlay = OverlayTexture.NO_OVERLAY;
        if (player.hurtTime > 0 || player.deathTime > 0) {
            overlay = OverlayTexture.pack(OverlayTexture.u(partialTicks), OverlayTexture.v(true));
        }

        ResourceLocation location = player.getSkin().texture();
        float headRadius = playerData.snakePlayer$getHeadSize() / 2;
        poseStack.pushPose();
        poseStack.translate(0, headRadius, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-player.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(player.getXRot()));
        SphereRenderer.drawTexturedSphere(poseStack, bufferSource, location, headRadius, 8, 0, 0, lightmap, true, overlay);
        poseStack.popPose();

        float segmentRadius = playerData.snakePlayer$getBodySegmentSize() / 2;
        for (int i = 0; i < playerParts.size(); i++) {
            PlayerPart playerPart = playerParts.get(i);
            Vec3 base = playerPart.getPosition(partialTicks).subtract(playerVec).add(0, segmentRadius, 0);
            Vec3 target = i > 0 ? playerParts.get(i - 1).getPosition(partialTicks) : player.getPosition(partialTicks);
            target = target.subtract(playerVec).add(0, segmentRadius, 0);
            HexagonalPrismRenderer.renderHexagonalPrism(player, poseStack, bufferSource, segmentRadius, base, target, lightmap, overlay);
        }

        return true;
    }

    // 一人称視点で自分の胴体セグメントだけをワールド側へ描画する。
    public static void renderFirstPerson(PoseStack poseStack, MultiBufferSource bufferSource, AbstractClientPlayer player, Vec3 cameraPos, float partialTicks) {
        if (player == null || player.isSpectator()) {
            return;
        }

        IPlayerData playerData = (IPlayerData) player;
        if (!playerData.snakePlayer$getIsSnake()) {
            return;
        }

        List<PlayerPart> playerParts = playerData.snakePlayer$getPlayerParts();
        int overlay = OverlayTexture.NO_OVERLAY;
        if (player.hurtTime > 0 || player.deathTime > 0) {
            overlay = OverlayTexture.pack(OverlayTexture.u(partialTicks), OverlayTexture.v(true));
        }

        int lightmap = LevelRenderer.getLightColor(player.level(), player.getOnPos().above());
        float segmentRadius = playerData.snakePlayer$getBodySegmentSize() / 2;
        for (int i = 0; i < playerParts.size(); i++) {
            PlayerPart playerPart = playerParts.get(i);
            Vec3 base = playerPart.getPosition(partialTicks).subtract(cameraPos).add(0, segmentRadius, 0);
            Vec3 target = i > 0 ? playerParts.get(i - 1).getPosition(partialTicks) : player.getPosition(partialTicks);
            target = target.subtract(cameraPos).add(0, segmentRadius, 0);
            HexagonalPrismRenderer.renderHexagonalPrism(player, poseStack, bufferSource, segmentRadius, base, target, lightmap, overlay);
        }
    }
}
