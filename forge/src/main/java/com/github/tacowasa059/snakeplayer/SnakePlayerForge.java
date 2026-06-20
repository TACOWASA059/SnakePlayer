package com.github.tacowasa059.snakeplayer;

import com.github.tacowasa059.snakeplayer.client.event.AutoMoveHandlerForge;
import com.github.tacowasa059.snakeplayer.client.event.ClientRenderListenerForge;
import com.github.tacowasa059.snakeplayer.common.entity.ForgePlayerPart;
import com.github.tacowasa059.snakeplayer.common.entity.ModDataSerializers;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPartFactory;
import com.github.tacowasa059.snakeplayer.common.event.ModCommandsForge;
import com.github.tacowasa059.snakeplayer.common.event.PlayerSpawnEventListenerForge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(SnakePlayer.MODID)
public final class SnakePlayerForge {
    public SnakePlayerForge() {
        PlayerPartFactory.setFactory(ForgePlayerPart::new);
        SnakePlayer.init(FMLPaths.CONFIGDIR.get());
        ModDataSerializers.register();
        MinecraftForge.EVENT_BUS.register(new PlayerSpawnEventListenerForge());
        MinecraftForge.EVENT_BUS.register(new ModCommandsForge());

        // Forge 61 では @Mod.EventBusSubscriber が廃止されたため、クライアント側リスナーは手動登録する。
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.register(AutoMoveHandlerForge.class);
            MinecraftForge.EVENT_BUS.register(ClientRenderListenerForge.class);
        }
    }
}
