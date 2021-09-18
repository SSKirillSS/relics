package it.hurts.sskirillss.relics.configs;

import com.google.gson.JsonObject;
import it.hurts.sskirillss.relics.configs.variables.durability.RelicDurability;
import it.hurts.sskirillss.relics.configs.variables.level.RelicLevel;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import lombok.Getter;

import java.util.List;

public class RelicConfig {
    @Getter
    private final JsonObject stats;
    @Getter
    private final List<RelicLoot> loot;
    @Getter
    private final RelicLevel level;
    @Getter
    private final RelicDurability durability;

    public RelicConfig(JsonObject stats, List<RelicLoot> loot, RelicLevel level, RelicDurability durability) {
        this.stats = stats;
        this.loot = loot;
        this.level = level;
        this.durability = durability;
    }
}