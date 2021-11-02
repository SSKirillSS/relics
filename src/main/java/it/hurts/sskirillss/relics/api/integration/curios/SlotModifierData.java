package it.hurts.sskirillss.relics.api.integration.curios;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Data
@Builder
public class SlotModifierData {
    @Singular("entry")
    private List<Pair<String, Integer>> modifiers;
}