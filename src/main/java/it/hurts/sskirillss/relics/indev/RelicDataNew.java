package it.hurts.sskirillss.relics.indev;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RelicDataNew {
    public RelicAbilityData abilityData;
    public RelicLevelingData levelingData;
    public RelicStyleData styleData;
}