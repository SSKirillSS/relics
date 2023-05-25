package it.hurts.sskirillss.relics.items.relics.base.data.leveling;

import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastPredicate;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

@Data
@Builder
public class RelicAbilityEntry {
    @Singular("stat")
    public Map<String, RelicAbilityStat> stats;

    @Builder.Default
    private int maxLevel = 10;

    @Builder.Default
    private int requiredLevel = 0;

    @Builder.Default
    private int requiredPoints = 1;

    @Builder.Default
    private Pair<AbilityCastType, AbilityCastPredicate> castData;

    public static class RelicAbilityEntryBuilder {
        private Pair<AbilityCastType, AbilityCastPredicate> castData = Pair.of(AbilityCastType.NONE, new AbilityCastPredicate());

        public RelicAbilityEntryBuilder active(AbilityCastType type) {
            castData = Pair.of(type, new AbilityCastPredicate());

            return this;
        }

        public RelicAbilityEntryBuilder active(AbilityCastType type, AbilityCastPredicate predicates) {
            castData = Pair.of(type, predicates);

            return this;
        }
    }
}