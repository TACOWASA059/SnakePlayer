package com.github.tacowasa059.snakeplayer;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue CX;
    public static final ForgeConfigSpec.DoubleValue CZ;
    public static final ForgeConfigSpec.DoubleValue L;
    public static final ForgeConfigSpec.DoubleValue R;

    public static final ForgeConfigSpec.BooleanValue enableSpread;

    static {
        BUILDER.push("General Settings for Spread Respawn");

        enableSpread = BUILDER.comment("enable spread respawn").define("enable_spread", false);

        CX = BUILDER.comment("Center X coordinate")
                .defineInRange("cx", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE);

        CZ = BUILDER.comment("Center Z coordinate")
                .defineInRange("cz", 0.0,  -Double.MAX_VALUE, Double.MAX_VALUE);

        L = BUILDER.comment("Length of region(edge length) L")
                .defineInRange("L", 50.0, 0.1, 10000.0);

        R = BUILDER.comment("Minimum radius r")
                .defineInRange("r", 5.0, 0.1, 100.0);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}

