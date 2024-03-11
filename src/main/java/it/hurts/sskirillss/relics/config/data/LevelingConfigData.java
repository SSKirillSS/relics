package it.hurts.sskirillss.relics.config.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LevelingConfigData {
    private int initialCost;
    private int maxLevel;
    private int step;
}