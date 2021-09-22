package it.hurts.sskirillss.relics.items.relics.base.data;

import lombok.Getter;

import java.util.List;

public class RelicConfigData<T extends RelicStats> {
    @Getter
    private final T config;
    @Getter
    private final RelicDurability durability;
    @Getter
    private final RelicLevel level;
    @Getter
    private final List<RelicLoot> loot;

    public RelicConfigData(T config, RelicLevel level, RelicDurability durability, List<RelicLoot> loot) {
        this.durability = durability;
        this.config = config;
        this.level = level;
        this.loot = loot;
    }
}