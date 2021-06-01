package it.hurts.sskirillss.relics.configs.variables.level;

import it.hurts.sskirillss.relics.utils.RelicUtils;

public class RelicLevel {
    private final int maxLevel;
    private final int initialExp;
    private final int expRatio;

    public RelicLevel(int maxLevel, int initialExp, int expRatio) {
        this.maxLevel = maxLevel;
        this.initialExp = initialExp;
        this.expRatio = expRatio;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getMaxExperience() {
        return RelicUtils.Level.getTotalExperienceForLevel(this, getMaxLevel());
    }

    public int getInitialExp() {
        return initialExp;
    }

    public int getExpRatio() {
        return expRatio;
    }
}