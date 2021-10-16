package it.hurts.sskirillss.relics.items.relics.base.data;

import it.hurts.sskirillss.relics.utils.RelicsTab;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

import java.util.List;

@Data
@Builder
public class RelicData {
    @Builder.Default
    private Item.Properties properties = new Item.Properties()
            .tab(RelicsTab.RELICS_TAB)
            .stacksTo(1);

    @Builder.Default
    private Rarity rarity = Rarity.COMMON;

    @Builder.Default
    private RelicLevel level = new RelicLevel(10, 100, 250);

    @Builder.Default
    private RelicDurability durability = new RelicDurability(100);

    @Accessors(fluent = true)
    private boolean hasAbility;

    @Builder.Default
    private Class<? extends RelicStats> config = RelicStats.class;

    @Singular("loot")
    private List<RelicLoot> loot;

    @SneakyThrows
    public RelicConfigData<?> toConfigData() {
        return new RelicConfigData<>(config.newInstance(), level, durability, loot);
    }

    public static class RelicDataBuilder {
        public RelicDataBuilder hasAbility() {
            hasAbility = true;

            return this;
        }
    }
}