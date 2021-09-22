package it.hurts.sskirillss.relics.items.relics.base.data;

import lombok.Getter;

public class RelicDurability {
    @Getter
    int maxDurability;

    public RelicDurability(int maxDurability) {
        this.maxDurability = maxDurability;
    }
}