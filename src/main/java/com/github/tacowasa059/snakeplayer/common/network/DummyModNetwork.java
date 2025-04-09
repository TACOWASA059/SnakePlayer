package com.github.tacowasa059.snakeplayer.common.network;

import com.github.tacowasa059.snakeplayer.SnakePlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;


/**
 * クライアントに導入されてない場合に弾くためのダミー設定
 */
public class DummyModNetwork {
    private static final String PROTOCOL_VERSION = "1.4.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SnakePlayer.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        CHANNEL.registerMessage(0, DummyPacket.class,
                (msg, buf) -> {}, // encode
                buf -> new DummyPacket(), // decode
                (msg, ctx) -> {} // handle
        );
    }

    public static class DummyPacket {}
}

