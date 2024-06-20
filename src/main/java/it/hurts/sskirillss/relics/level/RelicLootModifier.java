package it.hurts.sskirillss.relics.level;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.hurts.sskirillss.relics.init.LootCodecRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStorage;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.PatternSyntaxException;

public class RelicLootModifier extends LootModifier {
    public static final Supplier<MapCodec<RelicLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, RelicLootModifier::new)));

    public RelicLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        String lootId = context.getQueriedLootTableId().toString();

        boolean isValid;

        for (IRelicItem relic : RelicStorage.RELICS.keySet()) {
            for (Map.Entry<String, Float> entry : relic.getLootData().getCollection().getEntries().entrySet()) {
                String pattern = entry.getKey();
                float chance = entry.getValue();

                try {
                    isValid = lootId.matches(pattern);
                } catch (PatternSyntaxException exception) {
                    isValid = lootId.equals(pattern);
                }

                if (isValid) {
                    if (context.getRandom().nextFloat() <= chance)
                        generatedLoot.add(relic.getItem().getDefaultInstance());

                    break;
                }
            }
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return LootCodecRegistry.RELIC_LOOT.get();
    }
}