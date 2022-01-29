package it.hurts.sskirillss.relics.configs.data.relics;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelicConfigData<T extends RelicStats> {
    private T stats;

    private RelicLevelingData level;

    private RelicDurabilityData durability;

    public void setStats(RelicStats stats) {
        this.stats = (T) stats;
    }
}