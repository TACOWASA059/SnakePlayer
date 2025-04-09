package com.github.tacowasa059.snakeplayer;

import com.github.tacowasa059.snakeplayer.common.Config;
import com.github.tacowasa059.snakeplayer.common.entity.ModDataSerializers;
import com.github.tacowasa059.snakeplayer.common.network.DummyModNetwork;
import com.github.tacowasa059.snakeplayer.common.utils.GridManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SnakePlayer.MODID)
public class SnakePlayer {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "snakeplayer";

    public static final GridManager gridManager = new GridManager();
    @SuppressWarnings("removal")
    public SnakePlayer() {
        FMLJavaModLoadingContext configContext = FMLJavaModLoadingContext.get();
        configContext.getModEventBus().register(this);


        MinecraftForge.EVENT_BUS.register(this);
        ModDataSerializers.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        DummyModNetwork.register();
    }
}
