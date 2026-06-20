package com.github.tacowasa059.snakeplayer;

import com.github.tacowasa059.snakeplayer.common.entity.ForgeModDataSerializers;
import com.github.tacowasa059.snakeplayer.common.entity.ForgePlayerPart;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPartFactory;
import com.github.tacowasa059.snakeplayer.common.event.ModCommandsForge;
import com.github.tacowasa059.snakeplayer.common.event.PlayerSpawnEventListenerForge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(SnakePlayer.MODID)
public final class SnakePlayerForge {
    public SnakePlayerForge(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        PlayerPartFactory.setFactory(ForgePlayerPart::new);
        SnakePlayer.init(FMLPaths.CONFIGDIR.get());
        // PART_POSITIONS のシリアライザは Forge レジストリへ登録する（バニラ登録だと同期 ID 不一致でマルチが飛び飛びになる）。
        ForgeModDataSerializers.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new PlayerSpawnEventListenerForge());
        MinecraftForge.EVENT_BUS.register(new ModCommandsForge());
    }
}
