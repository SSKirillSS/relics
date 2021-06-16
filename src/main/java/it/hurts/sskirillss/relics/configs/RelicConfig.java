package it.hurts.sskirillss.relics.configs;

import com.google.gson.JsonObject;
import it.hurts.sskirillss.relics.configs.variables.durability.RelicDurability;
import it.hurts.sskirillss.relics.configs.variables.level.RelicLevel;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RelicLoot;

public class RelicConfig {
    private final JsonObject stats;
    private final RelicLoot loot;
    private final RelicLevel level;
    private final RelicDurability durability;

    public RelicConfig(JsonObject stats, RelicLoot loot, RelicLevel level, RelicDurability durability) {
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

    public JsonObject getStats() {
        return stats;
    }

    public RelicDurability getDurability() {
        return durability;
    }
}