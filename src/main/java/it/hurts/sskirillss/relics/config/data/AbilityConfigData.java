package it.hurts.sskirillss.relics.config.data;

import it.hurts.sskirillss.octolib.config.annotations.Prop;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class AbilityConfigData {
    @Prop(comment = "Number of leveling points needed to increase the ability level")
    private int requiredPoints;
    @Prop(comment = "Relic level at which the ability becomes unlocked")
    private int requiredLevel;
    @Prop(comment = "Highest level to which the ability can be upgraded")
    private int maxLevel;

    private Map<String, StatConfigData> stats;

    public AbilityData toData(IRelicItem relic, String ability) {
        AbilityData data = relic.getAbilityData(ability);

        data.setRequiredPoints(requiredPoints);
        data.setRequiredLevel(requiredLevel);
        data.setMaxLevel(maxLevel);
        data.setStats(stats.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toData(relic, ability, e.getKey()))));

        return data;
    }
}