package it.hurts.sskirillss.relics.items.relics.base.data;

import it.hurts.sskirillss.relics.utils.RelicUtils;
import lombok.Builder;
import lombok.Getter;

@Builder
public class RelicLevel {
    @Getter
    @Builder.Default
    private int maxLevel = 10;
    @Getter
    @Builder.Default
    private int initialExp = 100;
    @Getter
    @Builder.Default
    private int expRatio = 250;

    public int getMaxExperience() {
        return RelicUtils.Level.getTotalExperienceForLevel(this, getMaxLevel());
    }
}