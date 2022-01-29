package it.hurts.sskirillss.relics.world;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.hurts.sskirillss.relics.configs.data.runes.RuneLootData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RuneItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootSerializers;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class RelicLootModifier extends LootModifier {
    private static final Gson GSON = LootSerializers.createFunctionSerializer().create();

    private final LootEntry entry;
    private final ILootFunction[] functions;

    public RelicLootModifier(ILootCondition[] conditionsIn, LootEntry entry, ILootFunction[] functions) {
        super(conditionsIn);

        this.entry = entry;
        this.functions = functions;
    }

    @Nonnull
    @Override
    public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        entry.expand(context, generator -> generator.createItemStack(ILootFunction.decorate(
                LootFunctionManager.compose(this.functions), generatedLoot::add, context), context));

        Random random = context.getRandom();

        for (RuneItem rune : ItemRegistry.getRegisteredRunes()) {
            RuneLootData loot = rune.getLoot();
            ItemStack stack = new ItemStack(rune);

            stack.setCount(random.nextInt(5) + 1);

            if (loot.getTable().contains(context.getQueriedLootTableId().toString())
                    && random.nextFloat() <= loot.getChance())
                generatedLoot.add(stack);
        }

        return generatedLoot;
    }

    private static class Serializer extends GlobalLootModifierSerializer<RelicLootModifier> {
        @Override
        public RelicLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
            return new RelicLootModifier(conditions, GSON.fromJson(JSONUtils.getAsJsonObject(object, "entry"), LootEntry.class),
                    object.has("functions") ? GSON.fromJson(JSONUtils.getAsJsonArray(object,
                            "functions"), ILootFunction[].class) : new ILootFunction[0]);
        }

        @Override
        public JsonObject write(RelicLootModifier instance) {
            JsonObject object = makeConditions(instance.conditions);

            object.add("entry", GSON.toJsonTree(instance.entry, LootEntry.class));
            object.add("functions", GSON.toJsonTree(instance.functions, ILootFunction[].class));

            return object;
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EventHandler {
        @SubscribeEvent
        public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
            event.getRegistry().register(new RelicLootModifier.Serializer().setRegistryName(
                    new ResourceLocation(Reference.MODID, "relic_gen")));
        }
    }
}