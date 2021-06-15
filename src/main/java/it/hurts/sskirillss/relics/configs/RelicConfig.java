package it.hurts.sskirillss.relics.configs;

import com.google.gson.JsonObject;
import it.hurts.sskirillss.relics.configs.variables.level.RelicLevel;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RelicLoot;

public class RelicConfig {
    private final JsonObject stats;
    private final RelicLoot loot;
    private final RelicLevel level;

    public RelicConfig(JsonObject stats, RelicLoot loot, RelicLevel level) {
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

    public JsonObject getStats() {
        return stats;
    }
}