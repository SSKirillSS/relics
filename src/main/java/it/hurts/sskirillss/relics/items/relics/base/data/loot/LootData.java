package it.hurts.sskirillss.relics.items.relics.base.data.loot;

import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LootData {
    @Builder.Default
    private LootCollection collection;

    public static class LootDataBuilder {
        private LootCollection collection = LootCollection.builder().build();

        public LootDataBuilder entry(String lootTable, float chance) {
            this.collection.getEntries().put(lootTable, chance);

            return this;
        }

        public LootDataBuilder entry(LootCollection collection) {
            this.collection.getEntries().putAll(collection.getEntries());

            return this;
        }
    }
}