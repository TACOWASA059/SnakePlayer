package com.github.tacowasa059.snakeplayer;

import com.github.tacowasa059.snakeplayer.common.Config;
import com.github.tacowasa059.snakeplayer.common.utils.GridManager;

import java.nio.file.Path;

public final class SnakePlayer {
    public static final String MODID = "snakeplayer";
    public static final GridManager gridManager = new GridManager();

    private static boolean initialized;

    private SnakePlayer() {
    }

    public static void init(Path configDir) {
        if (initialized) {
            return;
        }
        Config.init(configDir.resolve(MODID + ".toml"));
        initialized = true;
    }
}
