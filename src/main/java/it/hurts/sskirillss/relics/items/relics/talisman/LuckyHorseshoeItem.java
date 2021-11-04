package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.ConfigData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.configs.data.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;

public class LuckyHorseshoeItem extends RelicItem<LuckyHorseshoeItem.Stats> {
    public LuckyHorseshoeItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.lootingChance * 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.fortuneChance * 100) + "%")
                        .build())
                .build();
    }

    @Override
    public ConfigData<Stats> getConfigData() {
        return ConfigData.<Stats>builder()
                .stats(new Stats())
                .loot(LootData.builder()
                        .table(LootTables.VILLAGE_BUTCHER.toString())
                        .chance(0.2F)
                        .build())
                .loot(LootData.builder()
                        .table(EntityType.HORSE.getDefaultLootTable().toString())
                        .chance(0.01F)
                        .build())
                .loot(LootData.builder()
                        .table(EntityType.ZOMBIE_HORSE.getDefaultLootTable().toString())
                        .chance(0.075F)
                        .build())
                .build();
    }

    @Override
    public int getLootingBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return !IRepairableItem.isBroken(curio) && livingEntity.getRandom().nextFloat() <= stats.lootingChance ? stats.additionalLooting : 0;
    }

    @Override
    public int getFortuneBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return !IRepairableItem.isBroken(curio) && livingEntity.getRandom().nextFloat() <= stats.fortuneChance ? stats.additionalFortune : 0;
    }

    public static class Stats extends RelicStats {
        public float lootingChance = 0.25F;
        public float fortuneChance = 0.25F;
        public int additionalLooting = 3;
        public int additionalFortune = 3;
    }
}