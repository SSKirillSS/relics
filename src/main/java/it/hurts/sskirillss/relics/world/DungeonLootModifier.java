package it.hurts.sskirillss.relics.world;

import com.google.gson.JsonObject;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.utils.CompatibilityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;;
import net.minecraft.loot.LootTables;
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
        if (RelicsConfig.RelicsWorldgen.RELICS_WORLDGEN_ENABLED.get() && CompatibilityUtils.isValidForgeVersion()) {
            ResourceLocation id = context.getQueriedLootTableId();
            if (id.equals(LootTables.CHESTS_DESERT_PYRAMID) || id.equals(LootTables.CHESTS_VILLAGE_VILLAGE_DESERT_HOUSE)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.SCARAB_TALISMAN_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.SCARAB_TALISMAN.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.HOLY_LOCKET_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.HOLY_LOCKET.get()));
            }

            if (id.equals(LootTables.CHESTS_ABANDONED_MINESHAFT)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.SPATIAL_SIGN_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.SPATIAL_SIGN.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.FRAGRANT_FLOWER_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.FRAGRANT_FLOWER.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.MAGIC_MIRROR_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.MAGIC_MIRROR.get()));
            }

            if (id.equals(LootTables.CHESTS_STRONGHOLD_CORRIDOR) || id.equals(LootTables.CHESTS_STRONGHOLD_CROSSING)
                    || id.equals(LootTables.CHESTS_STRONGHOLD_LIBRARY)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.SPATIAL_SIGN_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.SPATIAL_SIGN.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.ENDERS_HAND_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.ENDERS_HAND.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.MAGIC_MIRROR_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.MAGIC_MIRROR.get()));
            }

            if (id.equals(LootTables.BASTION_BRIDGE) || id.equals(LootTables.BASTION_OTHER)
                    || id.equals(LootTables.BASTION_TREASURE) || id.equals(LootTables.BASTION_HOGLIN_STABLE)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.BASTION_RING_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.BASTION_RING.get()));
            }

            if (id.equals(LootTables.CHESTS_NETHER_BRIDGE)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.REFLECTION_NECKLACE_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.REFLECTION_NECKLACE.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.MAGMA_WALKER_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.MAGMA_WALKER.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.RAGE_GLOVE_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.RAGE_GLOVE.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.BASTION_RING_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.BASTION_RING.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.BLAZING_FLASK_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.BLAZING_FLASK.get()));
            }

            if (id.equals(LootTables.RUINED_PORTAL)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.MAGMA_WALKER_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.MAGMA_WALKER.get()));
            }

            if (id.equals(LootTables.CHESTS_END_CITY_TREASURE)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.MIDNIGHT_ROBE_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.MIDNIGHT_ROBE.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.STELLAR_CATALYST_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.STELLAR_CATALYST.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.DELAY_RING_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.DELAY_RING.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.CHORUS_INHIBITOR_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.CHORUS_INHIBITOR.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.SPACE_DISSECTOR_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.SPACE_DISSECTOR.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.SOUL_DEVOURER_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.SOUL_DEVOURER.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.ENDERS_HAND_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.ENDERS_HAND.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.ELYTRA_BOOSTER_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.ELYTRA_BOOSTER.get()));
            }

            if (id.equals(LootTables.CHESTS_UNDERWATER_RUIN_BIG) || id.equals(LootTables.CHESTS_UNDERWATER_RUIN_SMALL)
                    || id.equals(LootTables.CHESTS_SHIPWRECK_TREASURE)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.DROWNED_BELT_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.DROWNED_BELT.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.JELLYFISH_NECKLACE_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.JELLYFISH_NECKLACE.get()));
            }

            if (id.equals(LootTables.CHESTS_VILLAGE_VILLAGE_FLETCHER)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.ARROW_QUIVER_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.ARROW_QUIVER.get()));
            }

            if (id.equals(LootTables.CHESTS_VILLAGE_VILLAGE_BUTCHER)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.HUNTER_BELT_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.HUNTER_BELT.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.ARROW_QUIVER_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.ARROW_QUIVER.get()));
            }

            if (id.equals(LootTables.CHESTS_VILLAGE_VILLAGE_SHEPHERD)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.LUCKY_HORSESHOE_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.LUCKY_HORSESHOE.get()));
            }

            if (id.equals(LootTables.CHESTS_VILLAGE_VILLAGE_SNOWY_HOUSE) || id.equals(LootTables.CHESTS_VILLAGE_VILLAGE_TAIGA_HOUSE)
                    || id.equals(LootTables.CHESTS_IGLOO_CHEST)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.ICE_SKATES_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.ICE_SKATES.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.ICE_BREAKER_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.ICE_BREAKER.get()));
            }

            if (id.equals(LootTables.CHESTS_JUNGLE_TEMPLE)) {
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.CAMOUFLAGE_RING_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.CAMOUFLAGE_RING.get()));
                if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.FRAGRANT_FLOWER_GEN_CHANCE.get())
                    generatedLoot.add(new ItemStack(ItemRegistry.FRAGRANT_FLOWER.get()));
            }
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
            event.getRegistry().register(new DungeonLootModifier.Serializer().setRegistryName(new ResourceLocation(Reference.MODID, "dungeon_loot_modifier")));
        }
    }
}