package it.hurts.sskirillss.relics.config.data;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class AbilitiesConfigData {
    private Map<String, AbilityConfigData> abilities;

    public AbilitiesData toData(IRelicItem relic) {
        AbilitiesData data = relic.getAbilitiesData();

        data.setAbilities(abilities.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toData(relic, e.getKey()))));

        return data;
    }
}