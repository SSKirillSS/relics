package it.hurts.sskirillss.relics.items.relics.base.data;

import it.hurts.sskirillss.relics.utils.RelicsTab;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

@Data
@Builder
public class RelicData {
    @Builder.Default
    private Item.Properties properties = new Item.Properties()
            .tab(RelicsTab.RELICS_TAB)
            .stacksTo(1);

    @Builder.Default
    private Rarity rarity = Rarity.COMMON;

    @Accessors(fluent = true)
    private boolean hasAbility;

    public static class RelicDataBuilder {
        public RelicDataBuilder hasAbility() {
            hasAbility = true;

            return this;
        }
    }
}