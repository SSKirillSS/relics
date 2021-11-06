package it.hurts.sskirillss.relics.configs.data;

import it.hurts.sskirillss.relics.api.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigData<T extends RelicStats> {
    @Builder.Default
    private T stats = (T) new RelicStats();

    @Builder.Default
    private LevelingData level = new LevelingData();

    @Builder.Default
    private DurabilityData durability = new DurabilityData();

    @Singular("loot")
    private List<LootData> loot = new ArrayList<>();

    public void setStats(RelicStats stats) {
        this.stats = (T) stats;
    }
}