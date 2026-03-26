package com.github.tacowasa059.snakeplayer.client.event;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

public final class ClientRenderListenerNeoForge {
    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (ClientRenderListener.shouldCancelHand(Minecraft.getInstance().player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientRender(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            return;
        }
        if (ClientRenderListener.renderPlayer(clientPlayer, event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTick(), event.getPackedLight())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onFirstPerspectiveRender(RenderLevelStageEvent event) {
        if (!event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_WEATHER)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        AbstractClientPlayer player = minecraft.player;
        if (player == null || minecraft.options.getCameraType() != CameraType.FIRST_PERSON || player.isSpectator()) {
            return;
        }

        MultiBufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();
        ClientRenderListener.renderFirstPerson(event.getPoseStack(), bufferSource, player, cameraPos, event.getPartialTick());
    }
}
