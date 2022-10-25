package it.hurts.sskirillss.relics.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class RelicsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("relics")
                .then(Commands.literal("level")
                        .then(Commands.literal("set")
                                .then(Commands.argument("level", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            RelicItem.setLevel(stack, IntegerArgumentType.getInteger(context, "level"));

                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("add")
                                .then(Commands.argument("level", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            RelicItem.addLevel(stack, IntegerArgumentType.getInteger(context, "level"));

                                            return Command.SINGLE_SUCCESS;
                                        }))))
                .then(Commands.literal("experience")
                        .then(Commands.literal("set")
                                .then(Commands.argument("experience", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            RelicItem.setExperience(stack, IntegerArgumentType.getInteger(context, "experience"));

                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("add")
                                .then(Commands.argument("experience", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            RelicItem.addExperience(player, stack, IntegerArgumentType.getInteger(context, "experience"));

                                            return Command.SINGLE_SUCCESS;
                                        }))))
                .then(Commands.literal("points")
                        .then(Commands.literal("set")
                                .then(Commands.argument("points", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            RelicItem.setPoints(stack, IntegerArgumentType.getInteger(context, "points"));

                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("add")
                                .then(Commands.argument("points", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            RelicItem.addPoints(stack, IntegerArgumentType.getInteger(context, "points"));

                                            return Command.SINGLE_SUCCESS;
                                        }))))
                .then(Commands.literal("ability")
                        .then(Commands.literal("points")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("ability", StringArgumentType.string())
                                                .then(Commands.argument("points", IntegerArgumentType.integer())
                                                        .executes(context -> {
                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                                return 0;
                                                            }

                                                            RelicItem.setAbilityPoints(stack, StringArgumentType.getString(context, "ability"),
                                                                    IntegerArgumentType.getInteger(context, "points"));

                                                            return Command.SINGLE_SUCCESS;
                                                        }))))
                                .then(Commands.literal("add")
                                        .then(Commands.argument("ability", StringArgumentType.string())
                                                .then(Commands.argument("points", IntegerArgumentType.integer())
                                                        .executes(context -> {
                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                                return 0;
                                                            }

                                                            RelicItem.addAbilityPoints(stack, StringArgumentType.getString(context, "ability"),
                                                                    -IntegerArgumentType.getInteger(context, "points"));

                                                            return Command.SINGLE_SUCCESS;
                                                        }))))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("ability", StringArgumentType.string())
                                                .then(Commands.argument("stat", StringArgumentType.string())
                                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                .executes(context -> {
                                                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                                                    ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                                                    if (!(stack.getItem() instanceof RelicItem)) {
                                                                        context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                                        return 0;
                                                                    }

                                                                    RelicItem.setAbilityValue(stack, StringArgumentType.getString(context, "ability"),
                                                                            StringArgumentType.getString(context, "stat"), DoubleArgumentType.getDouble(context, "value"));

                                                                    return Command.SINGLE_SUCCESS;
                                                                }))))))
                        .then(Commands.literal("randomize")
                                .then(Commands.argument("ability", StringArgumentType.string())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            RelicItem.randomizeStats(stack, StringArgumentType.getString(context, "ability"));

                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .then(Commands.literal("all")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof RelicItem relic)) {
                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            RelicData data = relic.getRelicData();

                                            if (data == null)
                                                return 0;

                                            RelicAbilityData abilities = relic.getRelicData().getAbilityData();

                                            if (abilities == null)
                                                return 0;

                                            for (Map.Entry<String, RelicAbilityEntry> entries : abilities.getAbilities().entrySet())
                                                RelicItem.randomizeStats(stack, entries.getKey());

                                            return Command.SINGLE_SUCCESS;
                                        })))
                )
        );
    }
}