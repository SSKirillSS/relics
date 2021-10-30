package it.hurts.sskirillss.relics.api.leveling;

import lombok.Data;

@Data
public class LevelingData {
    public LevelingData(int maxLevel, int initialCost, int costRatio) {
        this.maxLevel = maxLevel;
        this.initialCost = initialCost;
        this.costRatio = costRatio;
    }

    private int maxLevel;
    private int initialCost;
    private int costRatio;
}