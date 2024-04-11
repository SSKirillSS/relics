package it.hurts.sskirillss.relics.level;

import com.google.gson.JsonObject;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStorage;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RelicLootModifier extends LootModifier {
    public RelicLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
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

    @SubscribeEvent
    public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().register(new Serializer().setRegistryName(new ResourceLocation(Reference.MODID, "relic_gen")));
    }

    private static class Serializer extends GlobalLootModifierSerializer<RelicLootModifier> {
        @Override
        public RelicLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
            return new RelicLootModifier(conditions);
        }

        @Override
        public JsonObject write(RelicLootModifier instance) {
            return makeConditions(instance.conditions);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EventHandler {
        @SubscribeEvent
        public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
            event.getRegistry().register(new RelicLootModifier.Serializer().setRegistryName(
                    new ResourceLocation(Reference.MODID, "relic_loot")));
        }
    }
}