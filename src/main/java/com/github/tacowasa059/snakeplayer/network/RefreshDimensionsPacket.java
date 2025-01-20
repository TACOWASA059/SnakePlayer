package com.github.tacowasa059.snakeplayer.network;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class RefreshDimensionsPacket {
    public final UUID playerId;
    public final boolean isSnake;
    public final float headSize;
    public final float bodySegmentSize;

    public RefreshDimensionsPacket(UUID playerId, boolean isSnake, float headSize, float bodySegmentSize) {
        this.playerId = playerId;
        this.isSnake = isSnake;
        this.headSize = headSize;
        this.bodySegmentSize = bodySegmentSize;
    }

    public static void encode(RefreshDimensionsPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.playerId);
        buffer.writeBoolean(packet.isSnake);
        buffer.writeFloat(packet.headSize);
        buffer.writeFloat(packet.bodySegmentSize);
    }

    public static RefreshDimensionsPacket decode(FriendlyByteBuf buffer) {
        UUID playerId = buffer.readUUID();
        boolean isSnake = buffer.readBoolean();
        float headSize = buffer.readFloat();
        float bodySegmentSize = buffer.readFloat();
        return new RefreshDimensionsPacket(playerId, isSnake, headSize, bodySegmentSize);
    }

    public static void handle(RefreshDimensionsPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if(minecraft.level!=null){
                Player player = minecraft.level.getPlayerByUUID(packet.playerId);

                if (player != null) {
                    ((IPlayerData) player).setIsSnake(packet.isSnake);
                    ((IPlayerData) player).setHeadSize(packet.headSize);
                    if(packet.bodySegmentSize!=((IPlayerData)player).getBodySegmentSize()){
                        ((IPlayerData) player).setBodySegmentSize(packet.bodySegmentSize);
                        for(PlayerPart playerPart:((IPlayerData) player).getPlayerParts()){
                            playerPart.refreshDimensions();
                        }
                    }
                    player.refreshDimensions();
                }
            }
        });
        context.setPacketHandled(true);
    }
}

