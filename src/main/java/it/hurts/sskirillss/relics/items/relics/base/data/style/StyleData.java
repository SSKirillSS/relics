package it.hurts.sskirillss.relics.items.relics.base.data.style;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

@Data
@Builder
public class StyleData {
    @Builder.Default
    private String style = "default";

    @Nullable
    private Pair<Integer, Integer> borders;

    public static class StyleDataBuilder {
        public StyleDataBuilder borders(Integer top, Integer bottom) {
            borders = Pair.of(top, bottom);

            return this;
        }
    }
}