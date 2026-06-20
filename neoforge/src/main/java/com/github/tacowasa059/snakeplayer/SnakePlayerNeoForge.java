package com.github.tacowasa059.snakeplayer;

import com.github.tacowasa059.snakeplayer.client.event.AutoMoveHandlerNeoForge;
import com.github.tacowasa059.snakeplayer.client.event.ClientRenderListenerNeoForge;
import com.github.tacowasa059.snakeplayer.common.entity.NeoForgePlayerPart;
import com.github.tacowasa059.snakeplayer.common.entity.NeoForgeModDataSerializers;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPartFactory;
import com.github.tacowasa059.snakeplayer.common.event.ModCommandsNeoForge;
import com.github.tacowasa059.snakeplayer.common.event.PlayerSpawnEventListenerNeoForge;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;

@Mod(SnakePlayer.MODID)
public final class SnakePlayerNeoForge {
    // NeoForge は @Mod コンストラクタへ Dist を注入できるため、FMLEnvironment.dist の代わりに使う。
    public SnakePlayerNeoForge(IEventBus modEventBus, Dist dist) {
        PlayerPartFactory.setFactory(NeoForgePlayerPart::new);
        SnakePlayer.init(FMLPaths.CONFIGDIR.get());
        NeoForgeModDataSerializers.register(modEventBus);

        NeoForge.EVENT_BUS.register(new PlayerSpawnEventListenerNeoForge());
        NeoForge.EVENT_BUS.register(new ModCommandsNeoForge());

        if (dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(ClientRenderListenerNeoForge.class);
            NeoForge.EVENT_BUS.register(AutoMoveHandlerNeoForge.class);
        }
    }
}
