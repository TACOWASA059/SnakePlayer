package com.github.tacowasa059.snakeplayer;

import com.github.tacowasa059.snakeplayer.client.event.ForgeClientEvents;
import com.github.tacowasa059.snakeplayer.common.entity.ForgeModDataSerializers;
import com.github.tacowasa059.snakeplayer.common.entity.ForgePlayerPart;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPartFactory;
import com.github.tacowasa059.snakeplayer.common.event.ModCommands;
import com.github.tacowasa059.snakeplayer.common.event.PlayerSpawnEventListenerForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(SnakePlayer.MODID)
public final class SnakePlayerForge {
    public SnakePlayerForge(FMLJavaModLoadingContext context) {
        PlayerPartFactory.setFactory(ForgePlayerPart::new);
        SnakePlayer.init(FMLPaths.CONFIGDIR.get());
        // PART_POSITIONS のシリアライザは Forge レジストリへ登録する（バニラ登録だと同期 ID 不一致でマルチが飛び飛びになる）。
        ForgeModDataSerializers.register(context.getModBusGroup());

        // 複数リスナーを持つクラスは EventBus 7 の migration helper で登録できる。
        MinecraftForge.EVENT_BUS.register(new PlayerSpawnEventListenerForge());
        // 単一リスナーは migration helper が拒否するため、BUS へ直接 addListener する。
        RegisterCommandsEvent.BUS.addListener(event -> ModCommands.register(event.getDispatcher()));

        // Forge 61 では @Mod.EventBusSubscriber が廃止されたため、クライアント側リスナーは手動登録する。
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClientEvents.register();
        }
    }
}
