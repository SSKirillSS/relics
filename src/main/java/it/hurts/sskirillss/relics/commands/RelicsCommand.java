package it.hurts.sskirillss.relics.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import it.hurts.sskirillss.relics.commands.arguments.RelicAbilityArgument;
import it.hurts.sskirillss.relics.commands.arguments.RelicAbilityStatArgument;
import it.hurts.sskirillss.relics.config.ConfigHelper;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.command.EnumArgument;

import java.util.Map;

public class RelicsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("relics").requires(sender -> sender.hasPermission(2))
                .then(Commands.literal("config")
                        .then(Commands.literal("reload")
                                .executes(context -> {
                                    ConfigHelper.readConfigs();

                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("maximize")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                            if (!(stack.getItem() instanceof IRelicItem relic)) {
                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                return 0;
                            }

                            RelicData relicData = relic.getRelicData();

                            relic.setLevel(stack, relicData.getLeveling().getMaxLevel());

                            for (Map.Entry<String, AbilityData> abilityEntry : relicData.getAbilities().getAbilities().entrySet()) {
                                String abilityId = abilityEntry.getKey();
                                AbilityData abilityInfo = abilityEntry.getValue();

                                relic.setAbilityPoints(stack, abilityId, abilityInfo.getMaxLevel());

                                for (Map.Entry<String, StatData> statEntry : abilityInfo.getStats().entrySet()) {
                                    String statId = statEntry.getKey();

                                    relic.setAbilityValue(stack, abilityId, statId, relic.getStatByQuality(abilityId, statId, relic.getMaxQuality()));
                                }
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("minimize")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                            if (!(stack.getItem() instanceof IRelicItem relic)) {
                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                return 0;
                            }

                            RelicData relicData = relic.getRelicData();

                            relic.setLevel(stack, relicData.getLeveling().getMaxLevel());

                            for (Map.Entry<String, AbilityData> abilityEntry : relicData.getAbilities().getAbilities().entrySet()) {
                                String abilityId = abilityEntry.getKey();

                                relic.setAbilityPoints(stack, abilityId, 0);

                                for (Map.Entry<String, StatData> statEntry : abilityEntry.getValue().getStats().entrySet()) {
                                    String statId = statEntry.getKey();

                                    relic.setAbilityValue(stack, abilityId, statId, relic.getStatByQuality(abilityId, statId, 0));
                                }
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("level")
                        .then(Commands.argument("action", EnumArgument.enumArgument(CommandAction.class))
                                .then(Commands.argument("level", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof IRelicItem relic)) {
                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            CommandAction action = context.getArgument("action", CommandAction.class);

                                            int level = IntegerArgumentType.getInteger(context, "level");

                                            switch (action) {
                                                case SET -> relic.setLevel(stack, level);
                                                case ADD -> relic.addLevel(stack, level);
                                                case TAKE -> relic.addLevel(stack, -level);
                                            }

                                            relic.setLevel(stack, IntegerArgumentType.getInteger(context, "level"));

                                            return Command.SINGLE_SUCCESS;
                                        }))))
                .then(Commands.literal("experience")
                        .then(Commands.argument("action", EnumArgument.enumArgument(CommandAction.class))
                                .then(Commands.argument("experience", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof IRelicItem relic)) {
                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            CommandAction action = context.getArgument("action", CommandAction.class);

                                            int experience = IntegerArgumentType.getInteger(context, "experience");

                                            switch (action) {
                                                case SET -> relic.setExperience(stack, experience);
                                                case ADD -> relic.addExperience(stack, experience);
                                                case TAKE -> relic.addExperience(stack, -experience);
                                            }

                                            return Command.SINGLE_SUCCESS;
                                        }))))
                .then(Commands.literal("points")
                        .then(Commands.argument("action", EnumArgument.enumArgument(CommandAction.class))
                                .then(Commands.argument("points", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof IRelicItem relic)) {
                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            CommandAction action = context.getArgument("action", CommandAction.class);

                                            int points = IntegerArgumentType.getInteger(context, "points");

                                            switch (action) {
                                                case SET -> relic.setPoints(stack, points);
                                                case ADD -> relic.addPoints(stack, points);
                                                case TAKE -> relic.addPoints(stack, -points);
                                            }

                                            return Command.SINGLE_SUCCESS;
                                        }))))
                .then(Commands.literal("ability")
                        .then(Commands.literal("points")
                                .then(Commands.argument("action", EnumArgument.enumArgument(CommandAction.class))
                                        .then(Commands.argument("ability", RelicAbilityArgument.ability())
                                                .then(Commands.argument("points", IntegerArgumentType.integer())
                                                        .executes(context -> {
                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                                            if (!(stack.getItem() instanceof IRelicItem relic)) {
                                                                context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                                return 0;
                                                            }

                                                            CommandAction action = context.getArgument("action", CommandAction.class);

                                                            String ability = RelicAbilityArgument.getAbility(context, "ability");
                                                            int points = IntegerArgumentType.getInteger(context, "points");

                                                            if (ability.equals("all")) {
                                                                for (String entry : relic.getRelicData().getAbilities().getAbilities().keySet()) {
                                                                    switch (action) {
                                                                        case SET -> relic.setAbilityPoints(stack, entry, points);
                                                                        case ADD -> relic.addAbilityPoints(stack, entry, points);
                                                                        case TAKE -> relic.addAbilityPoints(stack, entry, -points);
                                                                    }
                                                                }
                                                            } else {
                                                                switch (action) {
                                                                    case SET -> relic.setAbilityPoints(stack, ability, points);
                                                                    case ADD -> relic.addAbilityPoints(stack, ability, points);
                                                                    case TAKE -> relic.addAbilityPoints(stack, ability, -points);
                                                                }
                                                            }

                                                            return Command.SINGLE_SUCCESS;
                                                        })))))
                        .then(Commands.literal("value")
                                .then(Commands.argument("action", EnumArgument.enumArgument(CommandAction.class))
                                        .then(Commands.argument("ability", RelicAbilityArgument.ability())
                                                .then(Commands.argument("stat", RelicAbilityStatArgument.abilityStat())
                                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                .executes(context -> {
                                                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                                                    ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                                                    if (!(stack.getItem() instanceof IRelicItem relic)) {
                                                                        context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                                        return 0;
                                                                    }

                                                                    CommandAction action = context.getArgument("action", CommandAction.class);

                                                                    String ability = RelicAbilityArgument.getAbility(context, "ability");
                                                                    String stat = RelicAbilityStatArgument.getAbilityStat(context, "stat");
                                                                    double value = DoubleArgumentType.getDouble(context, "value");

                                                                    if (ability.equals("all")) {
                                                                        for (String abilityEntry : relic.getRelicData().getAbilities().getAbilities().keySet()) {
                                                                            if (stat.equals("all")) {
                                                                                for (String statEntry : relic.getAbilityData(abilityEntry).getStats().keySet()) {
                                                                                    switch (action) {
                                                                                        case SET -> relic.setAbilityValue(stack, abilityEntry, statEntry, value);
                                                                                        case ADD -> relic.addAbilityValue(stack, abilityEntry, statEntry, value);
                                                                                        case TAKE -> relic.addAbilityValue(stack, abilityEntry, statEntry, -value);
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                switch (action) {
                                                                                    case SET -> relic.setAbilityValue(stack, abilityEntry, stat, value);
                                                                                    case ADD -> relic.addAbilityValue(stack, abilityEntry, stat, value);
                                                                                    case TAKE -> relic.addAbilityValue(stack, abilityEntry, stat, -value);
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        if (stat.equals("all")) {
                                                                            for (String statEntry : relic.getAbilityData(ability).getStats().keySet()) {
                                                                                switch (action) {
                                                                                    case SET -> relic.setAbilityValue(stack, ability, statEntry, value);
                                                                                    case ADD -> relic.addAbilityValue(stack, ability, statEntry, value);
                                                                                    case TAKE -> relic.addAbilityValue(stack, ability, statEntry, -value);
                                                                                }
                                                                            }
                                                                        } else {
                                                                            switch (action) {
                                                                                case SET -> relic.setAbilityValue(stack, ability, stat, value);
                                                                                case ADD -> relic.addAbilityValue(stack, ability, stat, value);
                                                                                case TAKE -> relic.addAbilityValue(stack, ability, stat, -value);
                                                                            }
                                                                        }
                                                                    }

                                                                    return Command.SINGLE_SUCCESS;
                                                                }))))))
                        .then(Commands.literal("quality")
                                .then(Commands.argument("action", EnumArgument.enumArgument(CommandAction.class))
                                        .then(Commands.argument("ability", RelicAbilityArgument.ability())
                                                .then(Commands.argument("stat", RelicAbilityStatArgument.abilityStat())
                                                        .then(Commands.argument("quality", IntegerArgumentType.integer())
                                                                .executes(context -> {
                                                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                                                    ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                                                    if (!(stack.getItem() instanceof IRelicItem relic)) {
                                                                        context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                                        return 0;
                                                                    }

                                                                    CommandAction action = context.getArgument("action", CommandAction.class);

                                                                    String ability = RelicAbilityArgument.getAbility(context, "ability");
                                                                    String stat = RelicAbilityStatArgument.getAbilityStat(context, "stat");
                                                                    int quality = IntegerArgumentType.getInteger(context, "quality");

                                                                    if (ability.equals("all")) {
                                                                        for (String abilityEntry : relic.getRelicData().getAbilities().getAbilities().keySet()) {
                                                                            if (stat.equals("all")) {
                                                                                for (String statEntry : relic.getAbilityData(abilityEntry).getStats().keySet()) {
                                                                                    double value = relic.getStatByQuality(abilityEntry, statEntry, quality);

                                                                                    switch (action) {
                                                                                        case SET -> relic.setAbilityValue(stack, abilityEntry, statEntry, value);
                                                                                        case ADD -> relic.addAbilityValue(stack, abilityEntry, statEntry, value);
                                                                                        case TAKE -> relic.addAbilityValue(stack, abilityEntry, statEntry, -value);
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                double value = relic.getStatByQuality(abilityEntry, stat, quality);

                                                                                switch (action) {
                                                                                    case SET -> relic.setAbilityValue(stack, abilityEntry, stat, value);
                                                                                    case ADD -> relic.addAbilityValue(stack, abilityEntry, stat, value);
                                                                                    case TAKE -> relic.addAbilityValue(stack, abilityEntry, stat, -value);
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        if (stat.equals("all")) {
                                                                            for (String statEntry : relic.getAbilityData(ability).getStats().keySet()) {
                                                                                double value = relic.getStatByQuality(ability, statEntry, quality);

                                                                                switch (action) {
                                                                                    case SET -> relic.setAbilityValue(stack, ability, statEntry, value);
                                                                                    case ADD -> relic.addAbilityValue(stack, ability, statEntry, value);
                                                                                    case TAKE -> relic.addAbilityValue(stack, ability, statEntry, -value);
                                                                                }
                                                                            }
                                                                        } else {
                                                                            double value = relic.getStatByQuality(ability, stat, quality);

                                                                            switch (action) {
                                                                                case SET -> relic.setAbilityValue(stack, ability, stat, value);
                                                                                case ADD -> relic.addAbilityValue(stack, ability, stat, value);
                                                                                case TAKE -> relic.addAbilityValue(stack, ability, stat, -value);
                                                                            }
                                                                        }
                                                                    }

                                                                    return Command.SINGLE_SUCCESS;
                                                                }))))))
                        .then(Commands.literal("randomize")
                                .then(Commands.argument("ability", RelicAbilityArgument.ability())
                                        .then(Commands.argument("stat", RelicAbilityStatArgument.abilityStat())
                                                .executes(context -> {
                                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                                    ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                                    if (!(stack.getItem() instanceof IRelicItem relic)) {
                                                        context.getSource().sendFailure(new TranslatableComponent("command.relics.base.not_relic"));

                                                        return 0;
                                                    }

                                                    String ability = RelicAbilityArgument.getAbility(context, "ability");
                                                    String stat = RelicAbilityStatArgument.getAbilityStat(context, "stat");

                                                    if (ability.equals("all")) {
                                                        for (String abilityEntry : relic.getRelicData().getAbilities().getAbilities().keySet()) {
                                                            if (stat.equals("all")) {
                                                                for (String statEntry : relic.getAbilityData(abilityEntry).getStats().keySet())
                                                                    relic.randomizeStat(stack, abilityEntry, statEntry);
                                                            } else {
                                                                relic.randomizeStat(stack, abilityEntry, stat);
                                                            }
                                                        }
                                                    } else {
                                                        if (stat.equals("all")) {
                                                            for (String statEntry : relic.getAbilityData(ability).getStats().keySet())
                                                                relic.randomizeStat(stack, ability, statEntry);
                                                        } else {
                                                            relic.randomizeStat(stack, ability, stat);
                                                        }
                                                    }

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                )
        );
    }

    public enum CommandAction {
        SET,
        ADD,
        TAKE
    }
}