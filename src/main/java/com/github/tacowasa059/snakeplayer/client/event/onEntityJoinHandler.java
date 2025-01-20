package com.github.tacowasa059.snakeplayer.client.event;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.SnakePlayer;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SnakePlayer.MODID, value = Dist.CLIENT)
public class onEntityJoinHandler {

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof AbstractClientPlayer player){
            player.refreshDimensions();
            IPlayerData playerData = (IPlayerData) player;
            for(PlayerPart parts: playerData.getPlayerParts()){
                parts.refreshDimensions();
            }
        }
    }
}
