package com.github.tacowasa059.snakeplayer.client.event;

import com.github.tacowasa059.snakeplayer.SnakePlayer;
import com.github.tacowasa059.snakeplayer.client.duck.ISnakeRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SnakePlayer.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientRenderListenerForge {
    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (ClientRenderListener.shouldCancelHand(Minecraft.getInstance().player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientRender(RenderPlayerEvent.Pre event) {
        // 1.21.2 以降、イベントはエンティティではなくレンダーステートを渡すため、
        // extractRenderState で退避した生きているプレイヤー参照と partialTick を取り出す。
        ISnakeRenderState snakeState = (ISnakeRenderState) event.getState();
        AbstractClientPlayer clientPlayer = snakeState.snakePlayer$getPlayer();
        if (clientPlayer == null) {
            return;
        }
        if (ClientRenderListener.renderPlayer(clientPlayer, event.getPoseStack(), event.getMultiBufferSource(), snakeState.snakePlayer$getPartialTick(), event.getPackedLight())) {
            event.setCanceled(true);
        }
    }

    // 一人称時のボディ描画は Forge 54 で RenderLevelStageEvent が削除されたため、
    // LevelRendererMixin（renderEntities の末尾）で行う。
}
