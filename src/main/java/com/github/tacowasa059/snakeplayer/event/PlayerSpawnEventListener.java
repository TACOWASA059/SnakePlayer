package com.github.tacowasa059.snakeplayer.event;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.SnakePlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = SnakePlayer.MODID,bus= Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerSpawnEventListener {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) { //respawn + change dimension
        Player originalPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        IPlayerData original_playerData = (IPlayerData)originalPlayer;
        IPlayerData new_playerData = (IPlayerData)newPlayer;

        new_playerData.setBodySegmentSize(original_playerData.getBodySegmentSize());
        new_playerData.setSnakeDamage(original_playerData.getSnakeDamage());
        new_playerData.setHeadSize(original_playerData.getHeadSize());
        new_playerData.setSnakeSpeed(original_playerData.getSnakeSpeed());
        new_playerData.setIsSnake(original_playerData.getIsSnake());
    }
    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        ReflectBoundingBox(player);
    }
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        ReflectBoundingBox(player);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        ReflectBoundingBox(player);
    }
    public static void ReflectBoundingBox(Player playerEntity) {
        if (playerEntity instanceof ServerPlayer newPlayer) {

            newPlayer.refreshDimensions();
        }
    }
}
