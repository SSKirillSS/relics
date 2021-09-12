package it.hurts.sskirillss.relics.world;

import com.google.gson.JsonObject;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RelicLoot;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RuneLoot;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.RuneItem;
import it.hurts.sskirillss.relics.utils.CompatibilityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.*;

public class DungeonLootModifier extends LootModifier {
    public DungeonLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (!CompatibilityUtils.isValidForgeVersion()) return generatedLoot;
        Random random = context.getRandom();
        for (RelicItem relic : RelicUtils.Worldgen.LOOT.keySet()) {
            RelicLoot loot = RelicUtils.Worldgen.LOOT.get(relic);
            if (loot.getLootChests().contains(context.getQueriedLootTableId().toString())
                    && random.nextFloat() <= loot.getChance()) generatedLoot.add(new ItemStack(relic));
        }
        for (RuneItem rune : RelicUtils.RunesWorldgen.LOOT.keySet()) {
            RuneLoot loot = RelicUtils.RunesWorldgen.LOOT.get(rune);
            ItemStack stack = new ItemStack(rune);
            stack.setCount(random.nextInt(5) + 1);
            if (loot.getLootChests().contains(context.getQueriedLootTableId().toString())
                    && random.nextFloat() <= loot.getChance()) generatedLoot.add(stack);
        }
        return generatedLoot;
    }

    private static class Serializer extends GlobalLootModifierSerializer<DungeonLootModifier> {
        @Override
        public DungeonLootModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            return new DungeonLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(DungeonLootModifier instance) {
            return this.makeConditions(instance.conditions);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EventHandler {
        @SubscribeEvent
        public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
            if (!CompatibilityUtils.isValidForgeVersion()) return;
            event.getRegistry().register(new DungeonLootModifier.Serializer().setRegistryName(
                    new ResourceLocation(Reference.MODID, "dungeon_loot_modifier")));
        }
    }
}