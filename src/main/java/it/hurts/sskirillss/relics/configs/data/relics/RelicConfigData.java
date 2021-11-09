package it.hurts.sskirillss.relics.configs.data.relics;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelicConfigData<T extends RelicStats> {
    private T stats;

    private RelicLevelingData level;

    private RelicDurabilityData durability;

    @Singular("loot")
    private List<RelicLootData> loot;

    public void setStats(RelicStats stats) {
        this.stats = (T) stats;
    }
}