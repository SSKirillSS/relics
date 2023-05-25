package it.hurts.sskirillss.relics.items.relics.base.data.cast;

import it.hurts.sskirillss.relics.items.relics.base.data.cast.data.PredicateData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.data.PredicateEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.data.PredicateInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Data
@AllArgsConstructor
public class AbilityCastPredicate {
    private final Map<String, PredicateEntry> predicates = new HashMap<>();

    public static AbilityCastPredicate builder() {
        return new AbilityCastPredicate();
    }

    public AbilityCastPredicate predicate(String name, Function<PredicateData, PredicateInfo> predicate) {
        predicates.put(name, new PredicateEntry(name, predicate));

        return this;
    }
}