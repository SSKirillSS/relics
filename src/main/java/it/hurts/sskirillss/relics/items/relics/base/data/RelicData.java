package it.hurts.sskirillss.relics.items.relics.base.data;

import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

import java.util.List;

@Builder
public class RelicData {
    @Getter
    @Builder.Default
    private Item.Properties properties = new Item.Properties()
            .tab(RelicsTab.RELICS_TAB)
            .stacksTo(1);

    @Getter
    private Rarity rarity;

    @Getter
    @Builder.Default
    private int maxLevel = 10;
    @Getter
    @Builder.Default
    private int initialExp = 100;
    @Getter
    @Builder.Default
    private int expRatio = 250;

    @Getter
    @Builder.Default
    private int durability = 100;

    @Getter
    @Builder.Default
    private boolean hasAbility = false;

    @Getter
    @Builder.Default
    private Class<? extends RelicStats> config = RelicStats.class;

    @Getter
    @Singular("loot")
    private List<RelicLoot> loot;
}