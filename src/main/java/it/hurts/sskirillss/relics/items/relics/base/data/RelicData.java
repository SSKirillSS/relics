package it.hurts.sskirillss.relics.items.relics.base.data;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RelicData {
    private AbilitiesData abilities;
    private LevelingData leveling;
    private RelicStyleData style;
}