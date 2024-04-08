package it.hurts.sskirillss.relics.items.relics.base.data;

import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
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
    private StyleData style = StyleData.builder().build();

    @Builder.Default
    private LootData loot = LootData.builder().build();
}