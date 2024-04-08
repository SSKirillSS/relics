package it.hurts.sskirillss.relics.items.relics.base.data.leveling;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
public class AbilitiesData {
    @Builder.Default
    private Map<String, AbilityData> abilities;

    public static class AbilitiesDataBuilder {
        private Map<String, AbilityData> abilities = new LinkedHashMap<>();

        public AbilitiesDataBuilder ability(AbilityData ability) {
            abilities.put(ability.getId(), ability);

            return this;
        }
    }
}