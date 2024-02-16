package it.hurts.sskirillss.relics.items.relics.base.data;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RelicData {
    @Builder.Default
    private AbilitiesData abilities = AbilitiesData.builder().build();

    @Builder.Default
    private LevelingData leveling = LevelingData.builder().build();

    @Builder.Default
    private RelicStyleData style = RelicStyleData.builder().build();

    @Builder.Default
    private LootData loot = LootData.builder().build();
}