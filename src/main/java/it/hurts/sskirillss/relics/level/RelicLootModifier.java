package it.hurts.sskirillss.relics.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.hurts.sskirillss.relics.init.CodecRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStorage;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

public class RelicLootModifier extends LootModifier {
    public static final Codec<RelicLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, RelicLootModifier::new));

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
    public Codec<? extends IGlobalLootModifier> codec() {
        return CodecRegistry.RELIC_LOOT.get();
    }
}