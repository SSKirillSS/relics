package it.hurts.sskirillss.relics.client.tooltip.base;

import it.hurts.sskirillss.relics.items.relics.base.data.utils.RelicStyle;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

@Data
@Builder
public class RelicStyleData {
    @Builder.Default
    private RelicStyle style = RelicStyle.DEFAULT;

    private Pair<String, String> borders;

    private Triple<String, String, String> durability;

    public static class RelicStyleDataBuilder {
        public RelicStyleDataBuilder borders(String top, String bottom) {
            borders = Pair.of(top, bottom);

            return this;
        }

        public RelicStyleDataBuilder durability(String full, String half, String low) {
            durability = Triple.of(full, half, low);

            return this;
        }
    }
}