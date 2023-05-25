package it.hurts.sskirillss.relics.items.relics.base.data.cast.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;

@Data
@AllArgsConstructor
public class PredicateEntry {
    private final String name;
    private final Function<PredicateData, PredicateInfo> predicate;
}