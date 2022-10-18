package it.hurts.sskirillss.relics.items.relics.base.data.leveling;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

@Data
@Builder
public class RelicAbilityEntry {
    @Singular("stat")
    public Map<String, RelicAbilityStat> stats;

    @Builder.Default
    private int maxLevel = -1;

    @Builder.Default
    private int requiredLevel = 0;

    @Builder.Default
    private int requiredPoints = 1;

    private Type type;

    public enum Type {
        POSITIVE,
        NEUTRAL,
        NEGATIVE;
    }
}