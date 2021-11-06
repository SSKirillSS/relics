package it.hurts.sskirillss.relics.api.leveling;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LevelingData {
    private int maxLevel;
    private int initialCost;
    private int costRatio;
}