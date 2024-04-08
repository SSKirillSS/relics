package it.hurts.sskirillss.relics.items.relics.base.data.leveling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LevelingData {
    @Builder.Default
    private int initialCost = 100;

    @Builder.Default
    private int maxLevel = 10;

    @Builder.Default
    private int step = 100;
}