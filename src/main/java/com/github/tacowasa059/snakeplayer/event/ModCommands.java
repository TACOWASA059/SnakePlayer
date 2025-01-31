package com.github.tacowasa059.snakeplayer.event;

import com.github.tacowasa059.snakeplayer.Config;
import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.SnakePlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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
                                                            return handlePlayerBooleanCommand(context.getSource(), targets, "isSnake", value);
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

    private static int handlePlayerBooleanCommand(CommandSourceStack source, Collection<ServerPlayer> targets, String dataKey, boolean value) {
        for (ServerPlayer player : targets) {
            IPlayerData playerData = (IPlayerData) player;
            if ("isSnake".equals(dataKey)) {
                playerData.setIsSnake(value);
            }
        }
        source.sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Updated " + dataKey + " to " + value + " for selected players"), true);
        return targets.size();
    }

    private static int handlePlayerFloatCommand(CommandSourceStack source, Collection<ServerPlayer> targets, String dataKey, float value) {
        for (ServerPlayer player : targets) {
            IPlayerData playerData = (IPlayerData) player;
            switch (dataKey) {
                case "headSize":
                    if (value >= 0.1 && value <= 10.0) {
                        playerData.setHeadSize(value);
                    } else {
                        source.sendFailure(Component.literal(ChatFormatting.RED + "Invalid value for headSize: " + value + ChatFormatting.RED + " (Range: 0.1 - 10.0)"));
                        return 0;
                    }
                    break;
                case "bodySegmentSize":
                    if (value >= 0.1 && value <= 10.0) {
                        playerData.setBodySegmentSize(value);
                    } else {
                        source.sendFailure(Component.literal(ChatFormatting.RED + "Invalid value for bodySegmentSize: " + value + ChatFormatting.RED + " (Range: 0.1 - 10.0)"));
                        return 0;
                    }
                    break;
                case "damage":
                    if (value >= 0) {
                        playerData.setSnakeDamage(value);
                    } else {
                        source.sendFailure(Component.literal(ChatFormatting.RED + "Invalid value for damage: " + value + ChatFormatting.RED + " (Range: >= 0)"));
                        return 0;
                    }
                    break;
                case "speed":
                    if (value >= 0.0 && value <= 0.75) {
                        playerData.setSnakeSpeed(value);
                    } else {
                        source.sendFailure(Component.literal(ChatFormatting.RED + "Invalid value for speed: " + value + ChatFormatting.RED + " (Range: 0.0 - 0.75)"));
                        return 0;
                    }
                    break;
                default:
                    source.sendFailure(Component.literal(ChatFormatting.RED + "Invalid data key: " + dataKey));
                    return 0;
            }
        }
        source.sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Updated " + dataKey + " to " + value + " for selected players"), true);
        return targets.size();
    }

    @SubscribeEvent
    public static void registerConfigCommand(RegisterCommandsEvent event){
        CommandDispatcher dispatcher = event.getDispatcher();
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
                                                            context.getSource().sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Set spread position: " + cx + ", " + cz), true);
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

                                                    context.getSource().sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Spread set to: " + enabled), true);
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

                                                    context.getSource().sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Spread range set to: " + L), true);
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

                                                    context.getSource().sendSuccess(()->Component.literal(ChatFormatting.DARK_GREEN + "Minimum spread radius set to: " + r), true);
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        // Get current config values
                        .then(Commands.literal("get")
                                .then(Commands.argument("key", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            return net.minecraft.commands.SharedSuggestionProvider.suggest(
                                                    new String[]{"spread_pos", "spread", "spread_Range", "spread_minimum_distance"}, builder
                                            );
                                        })
                                        .executes(context -> {
                                            String key = StringArgumentType.getString(context, "key");
                                            String value;
                                            switch (key) {
                                                case "spread_pos" -> value = "CX: " + Config.CX.get() + ", CZ: " + Config.CZ.get();
                                                case "spread" -> value = "Spread Enabled: " + Config.enableSpread.get();
                                                case "spread_Range" -> value = "Spread Range: " + Config.L.get();
                                                case "spread_minimum_distance" -> value = "Minimum Spread Radius: " + Config.R.get();
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
