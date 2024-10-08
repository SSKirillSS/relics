package it.hurts.sskirillss.relics.level;

import com.google.common.base.Suppliers;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.hurts.sskirillss.relics.init.LootCodecRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Supplier;
import java.util.regex.PatternSyntaxException;

public class RelicLootModifier extends LootModifier {
    public static final Supplier<MapCodec<RelicLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, RelicLootModifier::new)));

    public static final Multimap<String, Pair<IRelicItem, Float>> LOOT_TABLES = LinkedHashMultimap.create();

    public RelicLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        String lootId = context.getQueriedLootTableId().toString();

        for (var entry : LOOT_TABLES.get(lootId))
            if (context.getRandom().nextFloat() <= entry.getValue())
                generatedLoot.add(entry.getKey().getItem().getDefaultInstance());

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return LootCodecRegistry.RELIC_LOOT.get();
    }

    public static void processRelicCache(IRelicItem relic) {
        var server = ServerLifecycleHooks.getCurrentServer();

        if (server == null)
            return;

        for (var key : LOOT_TABLES.keys())
            LOOT_TABLES.get(key).removeIf(pair -> pair.getKey().equals(relic));

        for (var entry : relic.getLootData().getCollection().getEntries().entrySet()) {
            for (var lootTable : server.reloadableRegistries().getKeys(Registries.LOOT_TABLE)) {
                var id = lootTable.toString();
                var pattern = entry.getKey();
                var chance = entry.getValue();

                boolean isValid;

                try {
                    isValid = id.matches(pattern);
                } catch (PatternSyntaxException exception) {
                    isValid = id.equals(pattern);
                }

                if (isValid) {
                    LOOT_TABLES.put(id, Pair.of(relic, chance));
                }
            }
        }
    }

    @EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onServerStarted(ServerStartedEvent event) {
            if (!LOOT_TABLES.isEmpty())
                return;

            for (var entry : BuiltInRegistries.ITEM.entrySet()) {
                if (!(entry.getValue() instanceof IRelicItem relic))
                    continue;

                processRelicCache(relic);
            }
        }
    }
}