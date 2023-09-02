package it.hurts.sskirillss.relics.client.tooltip.base;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Data
@Builder
public class RelicStyleData {
    @Singular("ability")
    private List<AbilityTooltip> abilities;

    private String particles;

    private Pair<String, String> borders;

    public static class RelicStyleDataBuilder {
        public RelicStyleDataBuilder borders(String top, String bottom) {
            borders = Pair.of(top, bottom);

            return this;
        }
    }
}