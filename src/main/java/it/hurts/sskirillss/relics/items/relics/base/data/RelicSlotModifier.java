package it.hurts.sskirillss.relics.items.relics.base.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RelicSlotModifier {
    @Builder.Default
    private Multimap<String, Integer> modifiers;

    public static class RelicSlotModifierBuilder {
        Multimap<String, Integer> modifiers = ArrayListMultimap.create();

        public RelicSlotModifierBuilder modifier(String id, int count) {
            this.modifiers.put(id, count);

            return this;
        }
    }
}