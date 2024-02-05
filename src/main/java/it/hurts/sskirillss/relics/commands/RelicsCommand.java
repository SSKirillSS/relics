package it.hurts.sskirillss.relics.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import it.hurts.sskirillss.relics.commands.arguments.RelicAbilityArgument;
import it.hurts.sskirillss.relics.commands.arguments.RelicAbilityStatArgument;
import it.hurts.sskirillss.relics.config.ConfigHelper;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.DataUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.QualityUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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

                            if (!(stack.getItem() instanceof RelicItem)) {
                                context.getSource().sendFailure(Component.translatable("command.relics.base.not_relic"));

                                return 0;
                            }

                            RelicData relicData = DataUtils.getRelicData(stack.getItem());

                            LevelingUtils.setLevel(stack, relicData.getLevelingData().getMaxLevel());

                            for (Map.Entry<String, RelicAbilityEntry> abilityEntry : relicData.getAbilityData().getAbilities().entrySet()) {
                                String abilityId = abilityEntry.getKey();
                                RelicAbilityEntry abilityInfo = abilityEntry.getValue();

                                AbilityUtils.setAbilityPoints(stack, abilityId, abilityInfo.getMaxLevel());

                                for (Map.Entry<String, RelicAbilityStat> statEntry : abilityInfo.getStats().entrySet()) {
                                    String statId = statEntry.getKey();

                                    AbilityUtils.setAbilityValue(stack, abilityId, statId, QualityUtils.getStatByQuality(stack.getItem(), abilityId, statId, QualityUtils.MAX_QUALITY));
                                }
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("minimize")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                            if (!(stack.getItem() instanceof RelicItem)) {
                                context.getSource().sendFailure(Component.translatable("command.relics.base.not_relic"));

                                return 0;
                            }

                            RelicData relicData = DataUtils.getRelicData(stack.getItem());

                            LevelingUtils.setLevel(stack, relicData.getLevelingData().getMaxLevel());

                            for (Map.Entry<String, RelicAbilityEntry> abilityEntry : relicData.getAbilityData().getAbilities().entrySet()) {
                                String abilityId = abilityEntry.getKey();

                                AbilityUtils.setAbilityPoints(stack, abilityId, 0);

                                for (Map.Entry<String, RelicAbilityStat> statEntry : abilityEntry.getValue().getStats().entrySet()) {
                                    String statId = statEntry.getKey();

                                    AbilityUtils.setAbilityValue(stack, abilityId, statId, QualityUtils.getStatByQuality(stack.getItem(), abilityId, statId, 0));
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

                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                context.getSource().sendFailure(Component.translatable("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            CommandAction action = context.getArgument("action", CommandAction.class);

                                            int level = IntegerArgumentType.getInteger(context, "level");

                                            switch (action) {
                                                case SET -> LevelingUtils.setLevel(stack, level);
                                                case ADD -> LevelingUtils.addLevel(stack, level);
                                                case TAKE -> LevelingUtils.addLevel(stack, -level);
                                            }

                                            LevelingUtils.setLevel(stack, IntegerArgumentType.getInteger(context, "level"));

                                            return Command.SINGLE_SUCCESS;
                                        }))))
                .then(Commands.literal("experience")
                        .then(Commands.argument("action", EnumArgument.enumArgument(CommandAction.class))
                                .then(Commands.argument("experience", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                context.getSource().sendFailure(Component.translatable("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            CommandAction action = context.getArgument("action", CommandAction.class);

                                            int experience = IntegerArgumentType.getInteger(context, "experience");

                                            switch (action) {
                                                case SET -> LevelingUtils.setExperience(stack, experience);
                                                case ADD -> LevelingUtils.addExperience(stack, experience);
                                                case TAKE -> LevelingUtils.addExperience(stack, -experience);
                                            }

                                            return Command.SINGLE_SUCCESS;
                                        }))))
                .then(Commands.literal("points")
                        .then(Commands.argument("action", EnumArgument.enumArgument(CommandAction.class))
                                .then(Commands.argument("points", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                context.getSource().sendFailure(Component.translatable("command.relics.base.not_relic"));

                                                return 0;
                                            }

                                            CommandAction action = context.getArgument("action", CommandAction.class);

                                            int points = IntegerArgumentType.getInteger(context, "points");

                                            switch (action) {
                                                case SET -> LevelingUtils.setPoints(stack, points);
                                                case ADD -> LevelingUtils.addPoints(stack, points);
                                                case TAKE -> LevelingUtils.addPoints(stack, -points);
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

                                                            if (!(stack.getItem() instanceof RelicItem)) {
                                                                context.getSource().sendFailure(Component.translatable("command.relics.base.not_relic"));

                                                                return 0;
                                                            }

                                                            CommandAction action = context.getArgument("action", CommandAction.class);

                                                            String ability = RelicAbilityArgument.getAbility(context, "ability");
                                                            int points = IntegerArgumentType.getInteger(context, "points");

                                                            if (ability.equals("all")) {
                                                                RelicAbilityData data = AbilityUtils.getRelicAbilityData(stack.getItem());

                                                                if (data == null)
                                                                    return 0;

                                                                for (String entry : data.getAbilities().keySet()) {
                                                                    switch (action) {
                                                                        case SET -> AbilityUtils.setAbilityPoints(stack, entry, points);
                                                                        case ADD -> AbilityUtils.addAbilityPoints(stack, entry, points);
                                                                        case TAKE -> AbilityUtils.addAbilityPoints(stack, entry, -points);
                                                                    }
                                                                }
                                                            } else {
                                                                switch (action) {
                                                                    case SET -> AbilityUtils.setAbilityPoints(stack, ability, points);
                                                                    case ADD -> AbilityUtils.addAbilityPoints(stack, ability, points);
                                                                    case TAKE -> AbilityUtils.addAbilityPoints(stack, ability, -points);
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

                                                                    if (!(stack.getItem() instanceof RelicItem)) {
                                                                        context.getSource().sendFailure(Component.translatable("command.relics.base.not_relic"));

                                                                        return 0;
                                                                    }

                                                                    CommandAction action = context.getArgument("action", CommandAction.class);

                                                                    String ability = RelicAbilityArgument.getAbility(context, "ability");
                                                                    String stat = RelicAbilityStatArgument.getAbilityStat(context, "stat");
                                                                    double value = DoubleArgumentType.getDouble(context, "value");

                                                                    if (ability.equals("all")) {
                                                                        RelicAbilityData data = AbilityUtils.getRelicAbilityData(stack.getItem());

                                                                        if (data == null)
                                                                            return 0;

                                                                        for (String abilityEntry : data.getAbilities().keySet()) {
                                                                            if (stat.equals("all")) {
                                                                                for (String statEntry : AbilityUtils.getRelicAbilityEntry(stack.getItem(), abilityEntry).getStats().keySet()) {
                                                                                    switch (action) {
                                                                                        case SET -> AbilityUtils.setAbilityValue(stack, abilityEntry, statEntry, value);
                                                                                        case ADD -> AbilityUtils.addAbilityValue(stack, abilityEntry, statEntry, value);
                                                                                        case TAKE -> AbilityUtils.addAbilityValue(stack, abilityEntry, statEntry, -value);
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                switch (action) {
                                                                                    case SET -> AbilityUtils.setAbilityValue(stack, abilityEntry, stat, value);
                                                                                    case ADD -> AbilityUtils.addAbilityValue(stack, abilityEntry, stat, value);
                                                                                    case TAKE -> AbilityUtils.addAbilityValue(stack, abilityEntry, stat, -value);
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        if (stat.equals("all")) {
                                                                            for (String statEntry : AbilityUtils.getRelicAbilityEntry(stack.getItem(), ability).getStats().keySet()) {
                                                                                switch (action) {
                                                                                    case SET -> AbilityUtils.setAbilityValue(stack, ability, statEntry, value);
                                                                                    case ADD -> AbilityUtils.addAbilityValue(stack, ability, statEntry, value);
                                                                                    case TAKE -> AbilityUtils.addAbilityValue(stack, ability, statEntry, -value);
                                                                                }
                                                                            }
                                                                        } else {
                                                                            switch (action) {
                                                                                case SET -> AbilityUtils.setAbilityValue(stack, ability, stat, value);
                                                                                case ADD -> AbilityUtils.addAbilityValue(stack, ability, stat, value);
                                                                                case TAKE -> AbilityUtils.addAbilityValue(stack, ability, stat, -value);
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

                                                                    if (!(stack.getItem() instanceof RelicItem)) {
                                                                        context.getSource().sendFailure(Component.translatable("command.relics.base.not_relic"));

                                                                        return 0;
                                                                    }

                                                                    CommandAction action = context.getArgument("action", CommandAction.class);

                                                                    String ability = RelicAbilityArgument.getAbility(context, "ability");
                                                                    String stat = RelicAbilityStatArgument.getAbilityStat(context, "stat");
                                                                    int quality = IntegerArgumentType.getInteger(context, "quality");

                                                                    if (ability.equals("all")) {
                                                                        RelicAbilityData data = AbilityUtils.getRelicAbilityData(stack.getItem());

                                                                        if (data == null)
                                                                            return 0;

                                                                        for (String abilityEntry : data.getAbilities().keySet()) {
                                                                            if (stat.equals("all")) {
                                                                                for (String statEntry : AbilityUtils.getRelicAbilityEntry(stack.getItem(), abilityEntry).getStats().keySet()) {
                                                                                    double value = QualityUtils.getStatByQuality(stack.getItem(), abilityEntry, statEntry, quality);

                                                                                    switch (action) {
                                                                                        case SET -> AbilityUtils.setAbilityValue(stack, abilityEntry, statEntry, value);
                                                                                        case ADD -> AbilityUtils.addAbilityValue(stack, abilityEntry, statEntry, value);
                                                                                        case TAKE -> AbilityUtils.addAbilityValue(stack, abilityEntry, statEntry, -value);
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                double value = QualityUtils.getStatByQuality(stack.getItem(), abilityEntry, stat, quality);

                                                                                switch (action) {
                                                                                    case SET -> AbilityUtils.setAbilityValue(stack, abilityEntry, stat, value);
                                                                                    case ADD -> AbilityUtils.addAbilityValue(stack, abilityEntry, stat, value);
                                                                                    case TAKE -> AbilityUtils.addAbilityValue(stack, abilityEntry, stat, -value);
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        if (stat.equals("all")) {
                                                                            for (String statEntry : AbilityUtils.getRelicAbilityEntry(stack.getItem(), ability).getStats().keySet()) {
                                                                                double value = QualityUtils.getStatByQuality(stack.getItem(), ability, statEntry, quality);

                                                                                switch (action) {
                                                                                    case SET -> AbilityUtils.setAbilityValue(stack, ability, statEntry, value);
                                                                                    case ADD -> AbilityUtils.addAbilityValue(stack, ability, statEntry, value);
                                                                                    case TAKE -> AbilityUtils.addAbilityValue(stack, ability, statEntry, -value);
                                                                                }
                                                                            }
                                                                        } else {
                                                                            double value = QualityUtils.getStatByQuality(stack.getItem(), ability, stat, quality);

                                                                            switch (action) {
                                                                                case SET -> AbilityUtils.setAbilityValue(stack, ability, stat, value);
                                                                                case ADD -> AbilityUtils.addAbilityValue(stack, ability, stat, value);
                                                                                case TAKE -> AbilityUtils.addAbilityValue(stack, ability, stat, -value);
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

                                                    if (!(stack.getItem() instanceof RelicItem)) {
                                                        context.getSource().sendFailure(Component.translatable("command.relics.base.not_relic"));

                                                        return 0;
                                                    }

                                                    String ability = RelicAbilityArgument.getAbility(context, "ability");
                                                    String stat = RelicAbilityStatArgument.getAbilityStat(context, "stat");

                                                    if (ability.equals("all")) {
                                                        RelicAbilityData data = AbilityUtils.getRelicAbilityData(stack.getItem());

                                                        if (data == null)
                                                            return 0;

                                                        for (String abilityEntry : data.getAbilities().keySet()) {
                                                            if (stat.equals("all")) {
                                                                for (String statEntry : AbilityUtils.getRelicAbilityEntry(stack.getItem(), abilityEntry).getStats().keySet())
                                                                    AbilityUtils.randomizeStat(stack, abilityEntry, statEntry);
                                                            } else {
                                                                AbilityUtils.randomizeStat(stack, abilityEntry, stat);
                                                            }
                                                        }
                                                    } else {
                                                        if (stat.equals("all")) {
                                                            for (String statEntry : AbilityUtils.getRelicAbilityEntry(stack.getItem(), ability).getStats().keySet())
                                                                AbilityUtils.randomizeStat(stack, ability, statEntry);
                                                        } else {
                                                            AbilityUtils.randomizeStat(stack, ability, stat);
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