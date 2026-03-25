package com.github.tacowasa059.snakeplayer;

import com.github.tacowasa059.snakeplayer.common.entity.FabricPlayerPart;
import com.github.tacowasa059.snakeplayer.common.entity.ModDataSerializers;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPartFactory;
import com.github.tacowasa059.snakeplayer.common.event.ModCommands;
import com.github.tacowasa059.snakeplayer.common.event.PlayerSpawnEventListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.loader.api.FabricLoader;

public final class SnakePlayerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PlayerPartFactory.setFactory(FabricPlayerPart::new);
        SnakePlayer.init(FabricLoader.getInstance().getConfigDir());
        ModDataSerializers.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ModCommands.register(dispatcher));
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> PlayerSpawnEventListener.onPlayerClone(oldPlayer, newPlayer));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> PlayerSpawnEventListener.onPlayerRespawn(newPlayer));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> PlayerSpawnEventListener.onPlayerLogin(handler.player));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> PlayerSpawnEventListener.onPlayerChangeDimension(player));
        ServerTickEvents.END_SERVER_TICK.register(PlayerSpawnEventListener::onServerTick);
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> PlayerSpawnEventListener.onLivingDeath(entity));
    }
}
