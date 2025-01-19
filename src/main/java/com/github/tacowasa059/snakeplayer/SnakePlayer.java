package com.github.tacowasa059.snakeplayer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SnakePlayer.MODID)
public class SnakePlayer {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "snakeplayer";

    public SnakePlayer() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
}
