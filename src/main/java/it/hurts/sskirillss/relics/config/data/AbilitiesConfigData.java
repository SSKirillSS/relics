package it.hurts.sskirillss.relics.config.data;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbilitiesConfigData {
    private Map<String, AbilityConfigData> abilities = new LinkedHashMap<>();

    public AbilitiesData toData(IRelicItem relic) {
        AbilitiesData data = relic.getAbilitiesData();

        data.setAbilities(abilities.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toData(relic, e.getKey()), (o1, o2) -> o1, LinkedHashMap::new)));

        return data;
    }
}