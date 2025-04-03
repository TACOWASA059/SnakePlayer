package com.github.tacowasa059.snakeplayer.event;

import com.github.tacowasa059.snakeplayer.Config;
import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.SnakePlayer;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import com.github.tacowasa059.snakeplayer.utils.GridManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;


@Mod.EventBusSubscriber(modid = SnakePlayer.MODID,bus= Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerSpawnEventListener {

    private static final GridManager gridManager = new GridManager();

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) { //respawn + change dimension
        Player originalPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        IPlayerData original_playerData = (IPlayerData)originalPlayer;
        IPlayerData new_playerData = (IPlayerData)newPlayer;

        new_playerData.snakePlayer$setBodySegmentSize(original_playerData.snakePlayer$getBodySegmentSize());
        new_playerData.snakePlayer$setSnakeDamage(original_playerData.snakePlayer$getSnakeDamage());
        new_playerData.snakePlayer$setHeadSize(original_playerData.snakePlayer$getHeadSize());
        new_playerData.snakePlayer$setSnakeSpeed(original_playerData.snakePlayer$getSnakeSpeed());
        new_playerData.snakePlayer$setIsSnake(original_playerData.snakePlayer$getIsSnake());
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

        if(!player.level().isClientSide()){
            if(Config.enableSpread.get()){
                double[] vec = gridManager.sampleValidPoint(player);
                if(vec!=null){
//                    player.sendSystemMessage(Component.literal("find a valid location"));
                    player.teleportTo(vec[0], vec[1], vec[2]);
                    player.setOldPosAndRot();
                }
                else{
                    player.sendSystemMessage(Component.literal(ChatFormatting.RED +"cannot find a valid location"));
                }
            }
        }
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

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        MinecraftServer server=event.getServer();
        if(server.getTickCount() % 10 == 0){
            GridManager.server = server;
            gridManager.updateGrid();
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // ワールドがサーバー側か確認
        int exp = Config.expValue.get();
        if (player.level() instanceof ServerLevel serverLevel) {
            if(exp > 0) spawnExperienceOrb(serverLevel,  player, exp);
        }
    }

    // 経験値オーブを召喚するメソッド
    private static void spawnExperienceOrb(ServerLevel world, Player player,int experienceAmount) {
        IPlayerData playerData = (IPlayerData) player;
        List<PlayerPart> playerPartList = playerData.snakePlayer$getPlayerParts();
        for(PlayerPart playerPart : playerPartList){
            Vec3 pos = playerPart.position();
            ExperienceOrb experienceOrb = new ExperienceOrb(world, pos.x, pos.y+0.5f, pos.z, experienceAmount);
            experienceOrb.setDeltaMovement(0.0, 0.1, 0.0);
            world.addFreshEntity(experienceOrb);
        }
    }
}
