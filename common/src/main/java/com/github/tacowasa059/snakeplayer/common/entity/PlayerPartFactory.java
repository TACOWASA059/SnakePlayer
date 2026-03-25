package com.github.tacowasa059.snakeplayer.common.entity;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiFunction;

public final class PlayerPartFactory {
    private static BiFunction<Player, String, PlayerPart> factory;

    private PlayerPartFactory() {
    }

    public static void setFactory(BiFunction<Player, String, PlayerPart> factory) {
        PlayerPartFactory.factory = factory;
    }

    public static PlayerPart create(Player player, String name) {
        if (factory == null) {
            throw new IllegalStateException("PlayerPart factory is not initialized");
        }
        return factory.apply(player, name);
    }
}