package com.github.tacowasa059.snakeplayer.common.event;

import com.github.tacowasa059.snakeplayer.common.Config;
import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.SnakePlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = SnakePlayer.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("snake")
                        .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.literal("isSnake")
                                                .then(Commands.argument("value", BoolArgumentType.bool())
                                                        .executes(context -> {
                                                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                                            boolean value = BoolArgumentType.getBool(context, "value");
                                                            return handlePlayerBooleanCommand(context.getSource(), targets, value);
                                                        })
                                                )
                                        )
                                        .then(Commands.argument("dataparameter_key", StringArgumentType.string())
                                                .suggests((context, builder) -> {
                                                    builder.suggest("headSize");
                                                    builder.suggest("bodySegmentSize");
                                                    builder.suggest("damage");
                                                    builder.suggest("speed");
                                                    return builder.buildFuture();
                                                })
                                                .then(Commands.argument("value", FloatArgumentType.floatArg())
                                                        .executes(context -> {
                                                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                                            String dataKey = StringArgumentType.getString(context, "dataparameter_key");
                                                            float value = FloatArgumentType.getFloat(context, "value");
                                                            return handlePlayerFloatCommand(context.getSource(), targets, dataKey, value);
                                                        })
                                                )
                                        )
                                )
        );
    }

    private static int handlePlayerBooleanCommand(CommandSourceStack source, Collection<ServerPlayer> targets, boolean value) {
        for (ServerPlayer player : targets) {
            IPlayerData playerData = (IPlayerData) player;
            playerData.snakePlayer$setIsSnake(value);

        }
        source.sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Updated " + "isSnake" + " to " + value + " for selected players"), true);
        return targets.size();
    }

    private static int handlePlayerFloatCommand(CommandSourceStack source, Collection<ServerPlayer> targets, String dataKey, float value) {
        for (ServerPlayer player : targets) {
            IPlayerData playerData = (IPlayerData) player;
            switch (dataKey) {
                case "headSize" -> {
                    if (value >= 0.1 && value <= 10.0) {
                        playerData.snakePlayer$setHeadSize(value);
                    } else {
                        source.sendFailure(Component.literal(ChatFormatting.RED + "Invalid value for headSize: " + value + ChatFormatting.RED + " (Range: 0.1 - 10.0)"));
                        return 0;
                    }
                }
                case "bodySegmentSize" -> {
                    if (value >= 0.1 && value <= 10.0) {
                        playerData.snakePlayer$setBodySegmentSize(value);
                    } else {
                        source.sendFailure(Component.literal(ChatFormatting.RED + "Invalid value for bodySegmentSize: " + value + ChatFormatting.RED + " (Range: 0.1 - 10.0)"));
                        return 0;
                    }
                }
                case "damage" -> {
                    if (value >= 0) {
                        playerData.snakePlayer$setSnakeDamage(value);
                    } else {
                        source.sendFailure(Component.literal(ChatFormatting.RED + "Invalid value for damage: " + value + ChatFormatting.RED + " (Range: >= 0)"));
                        return 0;
                    }
                }
                case "speed" -> {
                    if (value >= 0.0 && value <= 1.5) {
                        playerData.snakePlayer$setSnakeSpeed(value);
                    } else {
                        source.sendFailure(Component.literal(ChatFormatting.RED + "Invalid value for speed: " + value + ChatFormatting.RED + " (Range: 0.0 - 1.5)"));
                        return 0;
                    }
                }
                default -> {
                    source.sendFailure(Component.literal(ChatFormatting.RED + "Invalid data key: " + dataKey));
                    return 0;
                }
            }
        }
        source.sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Updated " + dataKey + " to " + value + " for selected players"), true);
        return targets.size();
    }

    @SubscribeEvent
    public static void registerConfigCommand(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("snake")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("config")
                        .then(Commands.literal("set")
                                // Set spread_pos cx cz
                                .then(Commands.literal("spread_pos")
                                        .then(Commands.argument("cx", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("cz", DoubleArgumentType.doubleArg())
                                                        .executes(context -> {
                                                            double cx = DoubleArgumentType.getDouble(context, "cx");
                                                            double cz = DoubleArgumentType.getDouble(context, "cz");

                                                            Config.CX.set(cx);
                                                            Config.CZ.set(cz);
                                                            context.getSource().sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Set spread position: " +ChatFormatting.AQUA+ cx + ", " + cz), true);
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                                // Set spread true/false
                                .then(Commands.literal("spread")
                                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                                .executes(context -> {
                                                    boolean enabled = BoolArgumentType.getBool(context, "enabled");
                                                    Config.enableSpread.set(enabled);

                                                    context.getSource().sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Spread set to: " +ChatFormatting.AQUA+ enabled), true);
                                                    return 1;
                                                })
                                        )
                                )
                                // Set spread_Range L
                                .then(Commands.literal("spread_Range")
                                        .then(Commands.argument("L", DoubleArgumentType.doubleArg(0.1, 10000.0))
                                                .executes(context -> {
                                                    double L = DoubleArgumentType.getDouble(context, "L");
                                                    Config.L.set(L);

                                                    context.getSource().sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Spread range set to: " +ChatFormatting.AQUA+ L), true);
                                                    return 1;
                                                })
                                        )
                                )
                                // Set spread_minimum_r r
                                .then(Commands.literal("spread_minimum_distance")
                                        .then(Commands.argument("r", DoubleArgumentType.doubleArg(0.1, 100.0))
                                                .executes(context -> {
                                                    double r = DoubleArgumentType.getDouble(context, "r");
                                                    Config.R.set(r);

                                                    context.getSource().sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Minimum spread radius set to: " +ChatFormatting.AQUA+ r), true);
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("segment_experience")
                                        .then(Commands.argument("exp", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                .executes(context -> {

                                                    int experience = IntegerArgumentType.getInteger(context, "exp");
                                                    Config.expValue.set(experience);

                                                    context.getSource().sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Segment Experience set to: " +ChatFormatting.AQUA+ experience), true);
                                                    return 1;
                                                })
                                        )
                                )

                                .then(Commands.literal("default_is_snake")
                                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                                .executes(context -> {
                                                    boolean enabled = BoolArgumentType.getBool(context, "enabled");
                                                    Config.DEFAULT_IS_SNAKE.set(enabled);
                                                    context.getSource().sendSuccess(() ->
                                                            Component.literal(ChatFormatting.DARK_GREEN + "Default is_snake set to: " +ChatFormatting.AQUA+ enabled), true);
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("default_head_size")
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.001, 100))
                                                .executes(context -> {
                                                    double v = DoubleArgumentType.getDouble(context, "value");
                                                    Config.DEFAULT_HEAD_SIZE.set(v);
                                                    context.getSource().sendSuccess(() ->
                                                            Component.literal(ChatFormatting.DARK_GREEN + "Default head size set to: " +ChatFormatting.AQUA+ v), true);
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("default_body_segment_size")
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.001, 100))
                                                .executes(context -> {
                                                    double v = DoubleArgumentType.getDouble(context, "value");
                                                    Config.DEFAULT_BODY_SEGMENT_SIZE.set(v);
                                                    context.getSource().sendSuccess(() ->
                                                            Component.literal(ChatFormatting.DARK_GREEN + "Default body segment size set to: " +ChatFormatting.AQUA+ v), true);
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("default_damage")
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, Float.MAX_VALUE))
                                                .executes(context -> {
                                                    double v = DoubleArgumentType.getDouble(context, "value");
                                                    Config.DEFAULT_DAMAGE.set(v);
                                                    context.getSource().sendSuccess(() ->
                                                            Component.literal(ChatFormatting.DARK_GREEN + "Default snake damage set to: " +ChatFormatting.AQUA+ v), true);
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("default_speed")
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.5))
                                                .executes(context -> {
                                                    double v = DoubleArgumentType.getDouble(context, "value");
                                                    Config.DEFAULT_SPEED.set(v);
                                                    context.getSource().sendSuccess(() ->
                                                            Component.literal(ChatFormatting.DARK_GREEN + "Default snake speed set to: " +ChatFormatting.AQUA+ v), true);
                                                    return 1;
                                                })
                                        )
                                )

                                .then(Commands.literal("spawn_block_view_distance")
                                        .then(Commands.argument("distance", IntegerArgumentType.integer(0, 128))
                                                .executes(context -> {
                                                    int distance = IntegerArgumentType.getInteger(context, "distance");
                                                    Config.SPAWN_BLOCK_VIEW_DISTANCE.set(distance);
                                                    context.getSource().sendSuccess(() ->
                                                            Component.literal(ChatFormatting.DARK_GREEN + "Set view-blocking spawn distance to: " +ChatFormatting.AQUA+ distance), true);
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("spawn_block_view_half_width")
                                        .then(Commands.argument("halfWidth", IntegerArgumentType.integer(0, 64))
                                                .executes(context -> {
                                                    int width = IntegerArgumentType.getInteger(context, "halfWidth");
                                                    Config.SPAWN_BLOCK_VIEW_HALF_WIDTH.set(width);
                                                    context.getSource().sendSuccess(() ->
                                                            Component.literal(ChatFormatting.DARK_GREEN + "Set view-blocking spawn half-width to: " +ChatFormatting.AQUA+ width), true);
                                                    return 1;
                                                })
                                        )
                                )



                        )
                        // Get current config values
                        .then(Commands.literal("get")
                                .then(Commands.argument("key", StringArgumentType.string())
                                        .suggests((context, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(
                                                new String[]{"spread_pos", "spread", "spread_Range", "spread_minimum_distance", "segment_experience",
                                                        "default_is_snake", "default_head_size", "default_body_segment_size", "default_damage", "default_speed",
                                                        "spawn_block_view_distance", "spawn_block_view_half_width"}, builder
                                        ))
                                        .executes(context -> {
                                            String key = StringArgumentType.getString(context, "key");
                                            String value;
                                            switch (key) {
                                                case "spread_pos" -> value = "CX: " + ChatFormatting.AQUA + Config.CX.get() + ChatFormatting.DARK_GREEN+", CZ: " +ChatFormatting.AQUA + Config.CZ.get();
                                                case "spread" -> value = "Spread Enabled: " +ChatFormatting.AQUA + Config.enableSpread.get();
                                                case "spread_Range" -> value = "Spread Range: " +ChatFormatting.AQUA + Config.L.get();
                                                case "spread_minimum_distance" -> value = "Minimum Spread Radius: " +ChatFormatting.AQUA + Config.R.get();
                                                case "segment_experience" -> value = "Experience Level "+ChatFormatting.AQUA+Config.expValue.get();
                                                case "default_is_snake" -> value = "Default Is Snake: " + ChatFormatting.AQUA + Config.DEFAULT_IS_SNAKE.get();
                                                case "default_head_size" -> value = "Default Head Size: " + ChatFormatting.AQUA + Config.DEFAULT_HEAD_SIZE.get();
                                                case "default_body_segment_size" -> value = "Default Body Segment Size: " + ChatFormatting.AQUA + Config.DEFAULT_BODY_SEGMENT_SIZE.get();
                                                case "default_damage" -> value = "Default Damage: " + ChatFormatting.AQUA + Config.DEFAULT_DAMAGE.get();
                                                case "default_speed" -> value = "Default Speed: " + ChatFormatting.AQUA + Config.DEFAULT_SPEED.get();
                                                case "spawn_block_view_distance" -> value = "Spawn block view distance: " + ChatFormatting.AQUA + Config.SPAWN_BLOCK_VIEW_DISTANCE.get();
                                                case "spawn_block_view_half_width" -> value = "Spawn block view half-width: " + ChatFormatting.AQUA + Config.SPAWN_BLOCK_VIEW_HALF_WIDTH.get();

                                                default -> {
                                                    context.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Invalid config key."));
                                                    return 0;
                                                }
                                            }
                                            context.getSource().sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + value), false);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
