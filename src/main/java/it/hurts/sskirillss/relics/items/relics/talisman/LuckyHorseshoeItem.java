package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigDataOld;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class LuckyHorseshoeItem extends RelicItem<LuckyHorseshoeItem.Stats> {
    public LuckyHorseshoeItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());
    }

    @Override
    public RelicStyleData getStyle(ItemStack stack) {
        return RelicStyleData.builder()
                .borders("#ffdd00", "#eaa000")
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.lootingChance * 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.fortuneChance * 100) + "%")
                        .build())
                .build();
    }

    @Override
    public RelicConfigDataOld<Stats> getConfigData() {
        return RelicConfigDataOld.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Override
    public int getLootingBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return !DurabilityUtils.isBroken(curio) && livingEntity.getRandom().nextFloat() <= stats.lootingChance ? stats.additionalLooting : 0;
    }

    @Override
    public int getFortuneBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return !DurabilityUtils.isBroken(curio) && livingEntity.getRandom().nextFloat() <= stats.fortuneChance ? stats.additionalFortune : 0;
    }

    public static class Stats extends RelicStats {
        public float lootingChance = 0.25F;
        public float fortuneChance = 0.25F;
        public int additionalLooting = 3;
        public int additionalFortune = 3;
    }
}