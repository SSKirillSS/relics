package it.hurts.sskirillss.relics.items.relics.base.data.loot.misc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Data
@Builder
@AllArgsConstructor
public class LootCollection {
    public static LootCollectionBuilder builder() {
        return new LootCollectionBuilder() {
            @Override
            public LootCollection build() {
                LootCollection info = super.build();

                info.getApplicator().accept(info);

                return info;
            }
        };
    }

    @Builder.Default
    public Map<String, Float> entries;

    @Builder.Default
    private Consumer<LootCollection> applicator = (builder) -> {

    };

    public static class LootCollectionBuilder {
        public Map<String, Float> entries = new HashMap<>();

        public LootCollectionBuilder entry(String lootId, float chance) {
            this.entries.put(lootId, chance);

            return this;
        }

        public LootCollectionBuilder entry(LootCollection collection) {
            this.entries.putAll(collection.getEntries());

            return this;
        }

        public LootCollectionBuilder entry(String... lootIds) {
            for (String lotId : lootIds)
                this.entries.put(lotId, 0F);

            return this;
        }
    }
}