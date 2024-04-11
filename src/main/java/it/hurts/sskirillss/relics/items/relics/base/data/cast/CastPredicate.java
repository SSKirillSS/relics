package it.hurts.sskirillss.relics.items.relics.base.data.cast;

import it.hurts.sskirillss.relics.items.relics.base.data.cast.predicate.PredicateEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.predicate.misc.PredicateData;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Data
@Builder
public class CastPredicate {
    @Builder.Default
    private Map<String, PredicateEntry> predicates;

    public static class CastPredicateBuilder {
        private Map<String, PredicateEntry> predicates = new HashMap<>();

        public CastPredicateBuilder predicate(String name, Function<PredicateData, Boolean> predicate) {
            predicates.put(name, new PredicateEntry(name, predicate));

            return this;
        }
    }
}