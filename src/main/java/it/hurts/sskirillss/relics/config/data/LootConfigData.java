package it.hurts.sskirillss.relics.config.data;

import it.hurts.sskirillss.octolib.config.annotations.Prop;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class LootConfigData {
    @Prop(comment = "List of key-value pairs where the key is a textual identifier for the loot table (supporting regular expressions) and the value represents the probability of generating the relic within the specified bounds")
    private Map<String, Float> entries;

    public LootData toData(IRelicItem relic) {
        LootData data = relic.getLootData();

        data.setCollection(LootCollection.builder()
                .entries(entries)
                .build());

        return data;
    }
}