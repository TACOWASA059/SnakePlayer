package com.github.tacowasa059.snakeplayer.event;

import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.SnakePlayer;
import com.mojang.brigadier.arguments.BoolArgumentType;
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
}
