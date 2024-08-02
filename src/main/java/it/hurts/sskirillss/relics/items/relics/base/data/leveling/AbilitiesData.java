package it.hurts.sskirillss.relics.items.relics.base.data.leveling;

import it.hurts.sskirillss.relics.config.data.AbilitiesConfigData;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
public class AbilitiesData {
    @Builder.Default
    private Map<String, AbilityData> abilities;

    public AbilitiesConfigData toConfigData() {
        return new AbilitiesConfigData(abilities.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toConfigData(), (o1, o2) -> o1, LinkedHashMap::new)));
    }

    public static class AbilitiesDataBuilder {
        private Map<String, AbilityData> abilities = new LinkedHashMap<>();

        public AbilitiesDataBuilder ability(AbilityData ability) {
            abilities.put(ability.getId(), ability);

            return this;
        }
    }
}