package it.hurts.sskirillss.relics.configs.data.relics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelicLevelingData {
    private int maxLevel;
    private int initialCost;
    private int costRatio;
}