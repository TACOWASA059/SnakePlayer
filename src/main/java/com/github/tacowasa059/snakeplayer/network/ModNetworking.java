package com.github.tacowasa059.snakeplayer.network;

import com.github.tacowasa059.snakeplayer.SnakePlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SnakePlayer.MODID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, RefreshDimensionsPacket.class,
                RefreshDimensionsPacket::encode,
                RefreshDimensionsPacket::decode,
                RefreshDimensionsPacket::handle
        );

        CHANNEL.registerMessage(packetId++, RefreshDimensionsBatchPacket.class,
                RefreshDimensionsBatchPacket::encode,
                RefreshDimensionsBatchPacket::decode,
                RefreshDimensionsBatchPacket::handle
        );
    }
}

