package it.hurts.sskirillss.relics.configs;

import it.hurts.sskirillss.relics.configs.variables.level.RelicLevel;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RelicLoot;

public class RelicStat {
    private final RelicLoot loot;
    private final RelicLevel level;

    public RelicStat(RelicLoot loot, RelicLevel level) {
        this.loot = loot;
        this.level = level;
    }

    public RelicLoot getLoot() {
        return loot;
    }

    public RelicLevel getLevel() {
        return level;
    }
}