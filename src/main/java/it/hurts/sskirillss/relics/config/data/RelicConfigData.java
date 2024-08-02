package it.hurts.sskirillss.relics.config.data;

import it.hurts.sskirillss.octolib.config.impl.OctoConfig;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelicConfigData implements OctoConfig {
    public RelicConfigData(RelicData data) {
        this(data.getAbilities().toConfigData(), data.getLeveling().toConfigData(), data.getLoot().toConfigData());
    }

    public RelicData toData(IRelicItem relic) {
        RelicData data = relic.getRelicData();

        data.setAbilities(abilitiesData.toData(relic));
        data.setLeveling(levelingData.toData(relic));
        data.setLoot(lootData.toData(relic));

        return data;
    }

    private AbilitiesConfigData abilitiesData;

    private LevelingConfigData levelingData;

    private LootConfigData lootData;
}