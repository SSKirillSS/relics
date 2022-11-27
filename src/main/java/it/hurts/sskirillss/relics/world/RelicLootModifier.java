package it.hurts.sskirillss.relics.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootModifierManager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class RelicLootModifier extends LootModifier {
    private final LootItem entry;
    private final LootItemFunction[] functions;

    public RelicLootModifier(LootItemCondition[] conditionsIn, LootItem entry, LootItemFunction[] functions) {
        super(conditionsIn);

        this.entry = entry;
        this.functions = functions;
    }

    @Nonnull
    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        entry.expand(context, generator -> generator.createItemStack(LootItemFunction.decorate(
                LootItemFunctions.compose(this.functions), generatedLoot::add, context), context));

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    private static final Codec<LootItem> LOOT_ITEM_CODEC = Codec.PASSTHROUGH.flatXmap(
            d ->
            {
                try {
                    return DataResult.success(LootModifierManager.GSON_INSTANCE.fromJson(IGlobalLootModifier.getJson(d), LootItem.class));
                } catch (JsonSyntaxException e) {
                    LootModifierManager.LOGGER.warn("Unable to decode loot item", e);

                    return DataResult.error(e.getMessage());
                }
            },
            item ->
            {
                try {
                    JsonElement element = LootModifierManager.GSON_INSTANCE.toJsonTree(item);

                    return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, element));
                } catch (JsonSyntaxException e) {
                    LootModifierManager.LOGGER.warn("Unable to encode loot item", e);

                    return DataResult.error(e.getMessage());
                }
            }
    );

    private static final Codec<LootItemFunction[]> LOOT_FUNCTION_CODEC = Codec.PASSTHROUGH.flatXmap(
            d ->
            {
                try {
                    return DataResult.success(LootModifierManager.GSON_INSTANCE.fromJson(IGlobalLootModifier.getJson(d), LootItemFunction[].class));
                } catch (JsonSyntaxException e) {
                    LootModifierManager.LOGGER.warn("Unable to decode loot functions", e);

                    return DataResult.error(e.getMessage());
                }
            },
            function ->
            {
                try {
                    JsonElement element = LootModifierManager.GSON_INSTANCE.toJsonTree(function);

                    return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, element));
                } catch (JsonSyntaxException e) {
                    LootModifierManager.LOGGER.warn("Unable to encode loot functions", e);

                    return DataResult.error(e.getMessage());
                }
            }
    );

    public static Codec<RelicLootModifier> CODEC = RecordCodecBuilder.create(
            inst -> LootModifier.codecStart(inst).and(
                    inst.group(
                            RelicLootModifier.LOOT_ITEM_CODEC.fieldOf("entry").forGetter(m -> m.entry),
                            RelicLootModifier.LOOT_FUNCTION_CODEC.fieldOf("functions").forGetter(m -> m.functions)
                    )
            ).apply(inst, RelicLootModifier::new));
}