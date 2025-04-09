package com.github.tacowasa059.snakeplayer.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue CX;
    public static final ForgeConfigSpec.DoubleValue CZ;
    public static final ForgeConfigSpec.DoubleValue L;
    public static final ForgeConfigSpec.DoubleValue R;

    public static final ForgeConfigSpec.IntValue expValue;

    public static final ForgeConfigSpec.BooleanValue enableSpread;

    public static final ForgeConfigSpec.BooleanValue DEFAULT_IS_SNAKE;

    public static final ForgeConfigSpec.DoubleValue DEFAULT_HEAD_SIZE;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_BODY_SEGMENT_SIZE;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_SPEED;

    public static final ForgeConfigSpec.IntValue SPAWN_BLOCK_VIEW_DISTANCE;
    public static final ForgeConfigSpec.IntValue SPAWN_BLOCK_VIEW_HALF_WIDTH;

    static {
        BUILDER.push("Experience Settings");
        expValue = BUILDER.comment("Amount of experience each segment drops")
                .defineInRange("expValue", 10, 0, 10000);
        BUILDER.pop();

        BUILDER.push("Spread Respawn Settings");

        enableSpread = BUILDER.comment("enable spread respawn").define("enable_spread", false);

        CX = BUILDER.comment("Center X coordinate")
                .defineInRange("cx", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE);

        CZ = BUILDER.comment("Center Z coordinate")
                .defineInRange("cz", 0.0,  -Double.MAX_VALUE, Double.MAX_VALUE);

        L = BUILDER.comment("Length of region(edge length) L")
                .defineInRange("L", 50.0, 0.1, 10000.0);

        R = BUILDER.comment("Minimum radius r")
                .defineInRange("r", 5.0, 0.1, 100.0);

        BUILDER.push("Spawn Blocker: Line of Sight");

        SPAWN_BLOCK_VIEW_DISTANCE = BUILDER
                .comment("Maximum distance (blocks) in front of the player to block spawning along the view direction")
                .defineInRange("spawn_block_view_distance", 8, 0, 128);

        SPAWN_BLOCK_VIEW_HALF_WIDTH = BUILDER
                .comment("Half-width (blocks) of the area to block spawning around the view line")
                .defineInRange("spawn_block_view_half_width", 2, 0, 64);

        BUILDER.pop();

        BUILDER.pop();

        BUILDER.push("Snake Player Default Settings");

        DEFAULT_IS_SNAKE = BUILDER.comment("Default is_snake or not")
                .define("default_is_snake", false);

        DEFAULT_HEAD_SIZE = BUILDER.comment("Default head size for snake players")
                .defineInRange("default_head_size", 1.0, 0.001, 100);

        DEFAULT_BODY_SEGMENT_SIZE = BUILDER.comment("Default body segment size")
                .defineInRange("default_body_segment_size", 1.0, 0.001, 100);

        DEFAULT_DAMAGE = BUILDER.comment("Default attack damage for snake player")
                .defineInRange("default_damage", 1000, 0.0, Float.MAX_VALUE);

        DEFAULT_SPEED = BUILDER.comment("Default movement speed for snake player")
                .defineInRange("default_speed", 0.3, 0.0, 1.5);

        BUILDER.pop();


        SPEC = BUILDER.build();
    }
}

