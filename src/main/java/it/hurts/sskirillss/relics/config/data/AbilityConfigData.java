package it.hurts.sskirillss.relics.config.data;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AbilityConfigData {
    public AbilityConfigData(int requiredPoints, int requiredLevel, int maxLevel) {
        this.requiredPoints = requiredPoints;
        this.requiredLevel = requiredLevel;
        this.maxLevel = maxLevel;
    }

    private int requiredPoints;
    private int requiredLevel;
    private int maxLevel;

    private Map<String, StatConfigData> stats = new HashMap<>();
}