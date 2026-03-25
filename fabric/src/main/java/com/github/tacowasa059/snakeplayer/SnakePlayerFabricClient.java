package com.github.tacowasa059.snakeplayer;

import com.github.tacowasa059.snakeplayer.client.event.AutoMoveHandler;
import com.github.tacowasa059.snakeplayer.client.event.ClientRenderListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public final class SnakePlayerFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> AutoMoveHandler.onClientTick());
        WorldRenderEvents.LAST.register(context -> {
            Minecraft minecraft = Minecraft.getInstance();
            AbstractClientPlayer player = minecraft.player;
            if (player == null || minecraft.options.getCameraType() != CameraType.FIRST_PERSON || player.isSpectator()) {
                return;
            }

            MultiBufferSource.BufferSource consumers = minecraft.renderBuffers().bufferSource();
            Vec3 cameraPos = context.camera().getPosition();
            ClientRenderListener.renderFirstPerson(context.matrixStack(), consumers, player, cameraPos, context.tickDelta());
            consumers.endBatch();
        });
    }
}