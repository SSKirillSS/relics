package it.hurts.sskirillss.relics.items.relics.base.data;

import lombok.Getter;

import java.util.List;

public class RelicConfigData<T extends RelicStats> {
    @Getter
    private final T config;
    @Getter
    private final List<RelicLoot> loot;

    public RelicConfigData(T config, List<RelicLoot> loot) {
        this.config = config;
        this.loot = loot;
    }
}