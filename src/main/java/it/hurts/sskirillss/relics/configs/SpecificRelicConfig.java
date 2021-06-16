package it.hurts.sskirillss.relics.configs;

import it.hurts.sskirillss.relics.configs.variables.durability.RelicDurability;
import it.hurts.sskirillss.relics.configs.variables.level.RelicLevel;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RelicLoot;

public class SpecificRelicConfig<T extends RelicStats> {
    private final T stats;
    private final RelicLoot loot;
    private final RelicLevel level;
    private final RelicDurability durability;

    public SpecificRelicConfig(T stats, RelicLoot loot, RelicLevel level, RelicDurability durability) {
        this.stats = stats;
        this.loot = loot;
        this.level = level;
        this.durability = durability;
    }

    public RelicLoot getLoot() {
        return loot;
    }

    public RelicLevel getLevel() {
        return level;
    }

    public T getStats() {
        return stats;
    }

    public RelicDurability getDurability() {
        return durability;
    }
}