package com.github.tacowasa059.snakeplayer.common.event;

import com.github.tacowasa059.snakeplayer.SnakePlayer;
import com.github.tacowasa059.snakeplayer.common.Config;
import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import com.github.tacowasa059.snakeplayer.common.utils.GridManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class PlayerSpawnEventListener {
    private PlayerSpawnEventListener() {
    }

    public static void onPlayerClone(Player originalPlayer, Player newPlayer) {
        if (!(originalPlayer instanceof IPlayerData originalPlayerData) || !(newPlayer instanceof IPlayerData newPlayerData)) {
            return;
        }
        newPlayerData.snakePlayer$setBodySegmentSize(originalPlayerData.snakePlayer$getBodySegmentSize());
        newPlayerData.snakePlayer$setSnakeDamage(originalPlayerData.snakePlayer$getSnakeDamage());
        newPlayerData.snakePlayer$setHeadSize(originalPlayerData.snakePlayer$getHeadSize());
        newPlayerData.snakePlayer$setSnakeSpeed(originalPlayerData.snakePlayer$getSnakeSpeed());
        newPlayerData.snakePlayer$setIsSnake(originalPlayerData.snakePlayer$getIsSnake());
    }

    public static void onPlayerChangeDimension(Player player) {
        reflectBoundingBox(player);
    }

    public static void onPlayerRespawn(Player player) {
        reflectBoundingBox(player);
        if (!player.level().isClientSide() && Config.enableSpread.get()) {
            double[] vec = SnakePlayer.gridManager.sampleValidPoint(player);
            if (vec != null) {
                player.teleportTo(vec[0], vec[1], vec[2]);
                player.setOldPosAndRot();
            }
        }
    }

    public static void onPlayerLogin(Player player) {
        reflectBoundingBox(player);
    }

    public static void onServerTick(MinecraftServer server) {
        if (server.getTickCount() % 10 == 0) {
            GridManager.server = server;
            SnakePlayer.gridManager.updateGrid();
        }
    }

    public static void onLivingDeath(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return;
        }
        int exp = Config.expValue.get();
        if (player.level() instanceof ServerLevel serverLevel && exp > 0) {
            spawnExperienceOrb(serverLevel, player, exp);
        }
    }

    private static void reflectBoundingBox(Player playerEntity) {
        if (playerEntity instanceof ServerPlayer newPlayer) {
            newPlayer.refreshDimensions();
        }
    }

    private static void spawnExperienceOrb(ServerLevel world, Player player, int experienceAmount) {
        if (!(player instanceof IPlayerData playerData)) {
            return;
        }
        List<PlayerPart> playerPartList = playerData.snakePlayer$getPlayerParts();
        for (PlayerPart playerPart : playerPartList) {
            Vec3 pos = playerPart.position();
            ExperienceOrb experienceOrb = new ExperienceOrb(world, pos.x, pos.y + 0.5f, pos.z, experienceAmount);
            experienceOrb.setDeltaMovement(0.0, 0.1, 0.0);
            world.addFreshEntity(experienceOrb);
        }
    }
}
