package it.hurts.sskirillss.relics.configs.data;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigData<T extends RelicStats> {
    private T stats;

    @Singular("loot")
    private List<LootData> loot = new ArrayList<>();
}