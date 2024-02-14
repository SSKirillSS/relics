package it.hurts.sskirillss.relics.items.relics.base.data.leveling;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LevelingData {
    private int initialCost;
    private int maxLevel;
    private int step;
}