package it.hurts.sskirillss.relics.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RelicAbilityStatArgument implements ArgumentType<String> {
    public static RelicAbilityStatArgument abilityStat() {
        return new RelicAbilityStatArgument();
    }

    public static String getAbilityStat(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    @SneakyThrows
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null)
            return Suggestions.empty();

        Item item = player.getMainHandItem().getItem();
        String ability = StringArgumentType.getString(context, "ability");

        List<String> result = new ArrayList<>();

        if (ability.equals("all")) {
            RelicAbilityData data = AbilityUtils.getRelicAbilityData(item);

            if (data == null)
                return Suggestions.empty();

            for (RelicAbilityEntry abilityEntry : data.getAbilities().values()) {
                result.addAll(abilityEntry.getStats().keySet());
            }
        } else {
            RelicAbilityEntry data = AbilityUtils.getRelicAbilityEntry(item, ability);

            if (data == null)
                return Suggestions.empty();

            result.addAll(data.getStats().keySet());
        }

        result.add("all");

        return SharedSuggestionProvider.suggest(result, builder);
    }
}