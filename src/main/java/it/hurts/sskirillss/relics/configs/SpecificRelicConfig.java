package it.hurts.sskirillss.relics.configs;

import it.hurts.sskirillss.relics.configs.variables.level.RelicLevel;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RelicLoot;

public class SpecificRelicConfig<T extends RelicStats> {
    private final T stats;
    private final RelicLoot loot;
    private final RelicLevel level;

    public SpecificRelicConfig(T stats, RelicLoot loot, RelicLevel level) {
        this.stats = stats;
        this.loot = loot;
        this.level = level;
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
}