package it.hurts.sskirillss.relics.items.relics.belt;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.apache.commons.lang3.tuple.Pair;

public class LeatherBeltItem extends RelicItem<RelicStats> {
    public LeatherBeltItem() {
        super(RelicData.builder()
                .rarity(Rarity.COMMON)
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#a24f1d", "#4b1c00")
                .build();
    }

    @Override
    public RelicConfigData<RelicStats> getConfigData() {
        return RelicConfigData.builder()
                .build();
    }

    @Override
    public RelicSlotModifier getSlotModifiers(ItemStack stack) {
        return RelicSlotModifier.builder()
                .entry(Pair.of("talisman", 3))
                .build();
    }
}