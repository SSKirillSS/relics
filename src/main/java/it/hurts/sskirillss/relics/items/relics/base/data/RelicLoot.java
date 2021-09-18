package it.hurts.sskirillss.relics.items.relics.base.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Builder
public class RelicLoot {
    @Getter
    @Singular("table")
    private List<String> table;

    @Getter
    @Builder.Default
    private float chance = 0.1F;

    @Getter
    @Builder.Default
    private int baseCount = 1;
    @Getter
    private int additionalCount;
}