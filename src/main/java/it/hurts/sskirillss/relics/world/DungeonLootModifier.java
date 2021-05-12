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
        if (!RelicsConfig.RelicsWorldgen.RELICS_WORLDGEN_ENABLED.get() || !CompatibilityUtils.isValidForgeVersion()) return generatedLoot;

        ResourceLocation id = context.getQueriedLootTableId();
        if (id.equals(LootTables.DESERT_PYRAMID) || id.equals(LootTables.VILLAGE_DESERT_HOUSE)) {
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.SCARAB_TALISMAN_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.SCARAB_TALISMAN.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.HOLY_LOCKET_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.HOLY_LOCKET.get()));
        }

        if (id.equals(LootTables.ABANDONED_MINESHAFT)) {
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.SPATIAL_SIGN_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.SPATIAL_SIGN.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.FRAGRANT_FLOWER_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.FRAGRANT_FLOWER.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.MAGIC_MIRROR_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.MAGIC_MIRROR.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.SPIDER_NECKLACE_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.SPIDER_NECKLACE.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.OUT_RUNNER_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.OUT_RUNNER.get()));
        }

        if (id.equals(LootTables.STRONGHOLD_CORRIDOR) || id.equals(LootTables.STRONGHOLD_CROSSING)
                || id.equals(LootTables.STRONGHOLD_LIBRARY)) {
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.SPATIAL_SIGN_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.SPATIAL_SIGN.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.ENDERS_HAND_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.ENDERS_HAND.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.MAGIC_MIRROR_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.MAGIC_MIRROR.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.SPIDER_NECKLACE_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.SPIDER_NECKLACE.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.OUT_RUNNER_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.OUT_RUNNER.get()));
        }

        if (id.equals(LootTables.BASTION_BRIDGE) || id.equals(LootTables.BASTION_OTHER) || id.equals(LootTables.BASTION_TREASURE)
                || id.equals(LootTables.BASTION_HOGLIN_STABLE) || id.equals(LootTables.NETHER_BRIDGE)) {
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

        if (id.equals(LootTables.END_CITY_TREASURE)) {
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
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.SHADOW_GLAIVE_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.SHADOW_GLAIVE.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.OUT_RUNNER_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.OUT_RUNNER.get()));
        }

        if (id.equals(LootTables.UNDERWATER_RUIN_BIG) || id.equals(LootTables.UNDERWATER_RUIN_SMALL)
                || id.equals(LootTables.SHIPWRECK_TREASURE)) {
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.DROWNED_BELT_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.DROWNED_BELT.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.JELLYFISH_NECKLACE_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.JELLYFISH_NECKLACE.get()));
        }

        if (id.equals(LootTables.VILLAGE_FLETCHER)) {
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.ARROW_QUIVER_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.ARROW_QUIVER.get()));
        }

        if (id.equals(LootTables.VILLAGE_BUTCHER)) {
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.HUNTER_BELT_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.HUNTER_BELT.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.ARROW_QUIVER_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.ARROW_QUIVER.get()));
        }

        if (id.equals(LootTables.VILLAGE_SHEPHERD)) {
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.LUCKY_HORSESHOE_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.LUCKY_HORSESHOE.get()));
        }

        if (id.equals(LootTables.VILLAGE_SNOWY_HOUSE) || id.equals(LootTables.VILLAGE_TAIGA_HOUSE)
                || id.equals(LootTables.IGLOO_CHEST)) {
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.ICE_SKATES_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.ICE_SKATES.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.ICE_BREAKER_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.ICE_BREAKER.get()));
        }

        if (id.equals(LootTables.JUNGLE_TEMPLE)) {
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.CAMOUFLAGE_RING_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.CAMOUFLAGE_RING.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.FRAGRANT_FLOWER_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.FRAGRANT_FLOWER.get()));
            if (context.getRandom().nextFloat() <= RelicsConfig.RelicsWorldgen.SPORE_SACK_GEN_CHANCE.get())
                generatedLoot.add(new ItemStack(ItemRegistry.SPORE_SACK.get()));
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
            if (RelicsConfig.RelicsWorldgen.RELICS_WORLDGEN_ENABLED.get() && CompatibilityUtils.isValidForgeVersion())
                event.getRegistry().register(new DungeonLootModifier.Serializer().setRegistryName(
                        new ResourceLocation(Reference.MODID, "dungeon_loot_modifier")));
        }
    }
}