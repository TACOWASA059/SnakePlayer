package com.github.tacowasa059.snakeplayer.network;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 更新処理を少なくするために変更時だけ送る(変更データの同期が間に合わないためパケットで送信する)
 */
public class RefreshDimensionsBatchPacket {
    private final List<RefreshDimensionsPacket> playerPackets;

    public RefreshDimensionsBatchPacket(List<RefreshDimensionsPacket> playerPackets) {
        this.playerPackets = playerPackets;
    }

    public static void encode(RefreshDimensionsBatchPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.playerPackets.size());
        for (RefreshDimensionsPacket playerPacket : packet.playerPackets) {
            RefreshDimensionsPacket.encode(playerPacket, buffer);
        }
    }

    public static RefreshDimensionsBatchPacket decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        List<RefreshDimensionsPacket> playerPackets = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            playerPackets.add(RefreshDimensionsPacket.decode(buffer));
        }
        return new RefreshDimensionsBatchPacket(playerPackets);
    }

    public static void handle(RefreshDimensionsBatchPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            for (RefreshDimensionsPacket playerPacket : packet.playerPackets) {
                if(minecraft.level!=null){
                    Player player = minecraft.level.getPlayerByUUID(playerPacket.playerId);
                    if (player != null) {
                        ((IPlayerData) player).setIsSnake(playerPacket.isSnake);
                        ((IPlayerData) player).setHeadSize(playerPacket.headSize);
                        if(playerPacket.bodySegmentSize!=((IPlayerData)player).getBodySegmentSize()){
                            ((IPlayerData) player).setBodySegmentSize(playerPacket.bodySegmentSize);
                            for(PlayerPart playerPart:((IPlayerData) player).getPlayerParts()){
                                playerPart.refreshDimensions();
                            }
                        }
                        player.refreshDimensions();
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
