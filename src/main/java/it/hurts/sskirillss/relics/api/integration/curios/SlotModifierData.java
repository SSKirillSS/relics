package it.hurts.sskirillss.relics.api.integration.curios;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Data
@Builder
public class SlotModifierData {
    final List<Pair<String, Integer>> modifiers;

    public static class SlotModifierDataBuilder {
        public SlotModifierDataBuilder entry(String identifier, int amount) {
            modifiers.add(Pair.of(identifier, amount));

            return this;
        }
    }
}