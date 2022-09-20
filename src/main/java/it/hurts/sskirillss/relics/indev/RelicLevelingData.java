package it.hurts.sskirillss.relics.indev;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RelicLevelingData {
    public int initialCost;
    public int maxLevel;
    public int step;
}