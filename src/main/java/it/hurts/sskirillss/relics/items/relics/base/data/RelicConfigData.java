package it.hurts.sskirillss.relics.items.relics.base.data;

import lombok.Getter;

import java.util.List;

public class RelicConfigData<T extends RelicStats> {
    @Getter
    private final T config;
    @Getter
    private final RelicDurability durability;
    @Getter
    private final List<RelicLoot> loot;

    public RelicConfigData(T config, RelicDurability durability, List<RelicLoot> loot) {
        this.durability = durability;
        this.config = config;
        this.loot = loot;
    }
}