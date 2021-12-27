package it.hurts.sskirillss.relics.items.relics.base.data;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Data
@Builder
public class RelicSlotModifier {
    @Singular("entry")
    private List<Pair<String, Integer>> modifiers;
}