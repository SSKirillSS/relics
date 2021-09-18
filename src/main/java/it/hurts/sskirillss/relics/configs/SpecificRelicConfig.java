package it.hurts.sskirillss.relics.configs;

import it.hurts.sskirillss.relics.configs.variables.durability.RelicDurability;
import it.hurts.sskirillss.relics.configs.variables.level.RelicLevel;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import lombok.Getter;

import java.util.List;

public class SpecificRelicConfig<T extends RelicStats> {
    @Getter
    private final T stats;
    @Getter
    private final List<RelicLoot> loot;
    @Getter
    private final RelicLevel level;
    @Getter
    private final RelicDurability durability;

    public SpecificRelicConfig(T stats, List<RelicLoot> loot, RelicLevel level, RelicDurability durability) {
        this.stats = stats;
        this.loot = loot;
        this.level = level;
        this.durability = durability;
    }
}