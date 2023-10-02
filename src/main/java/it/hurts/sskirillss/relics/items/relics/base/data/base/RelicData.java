package it.hurts.sskirillss.relics.items.relics.base.data.base;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RelicData {
    private RelicAbilityData abilityData;
    private RelicLevelingData levelingData;
    private RelicStyleData styleData;
}