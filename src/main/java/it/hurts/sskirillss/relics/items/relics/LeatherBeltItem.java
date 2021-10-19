package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttribute;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import org.apache.commons.lang3.tuple.MutablePair;

public class LeatherBeltItem extends RelicItem<RelicStats> {
    public LeatherBeltItem() {
        super(RelicData.builder()
                .rarity(Rarity.COMMON)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.VILLAGE)
                        .chance(0.05F)
                        .build())
                .build());
    }

    @Override
    public RelicAttribute getAttributes(ItemStack stack) {
        return RelicAttribute.builder()
                .slot(new MutablePair<>("talisman", 3))
                .build();
    }
}