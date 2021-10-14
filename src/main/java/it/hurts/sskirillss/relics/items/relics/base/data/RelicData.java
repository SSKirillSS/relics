package it.hurts.sskirillss.relics.items.relics.base.data;

import it.hurts.sskirillss.relics.utils.RelicsTab;
import lombok.*;
import lombok.experimental.Accessors;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

import java.util.List;

@Builder
public class RelicData {
    @Getter
    @Setter
    @Builder.Default
    private Item.Properties properties = new Item.Properties()
            .tab(RelicsTab.RELICS_TAB)
            .stacksTo(1);

    @Getter
    @Setter
    @Builder.Default
    private Rarity rarity = Rarity.COMMON;

    @Getter
    @Setter
    @Builder.Default
    private RelicLevel level = new RelicLevel(10, 100, 250);

    @Getter
    @Setter
    @Builder.Default
    private RelicDurability durability = new RelicDurability(100);

    @Getter
    @Setter
    @Builder.Default
    private BipedModel<LivingEntity> model = null;

    @Getter
    @Setter
    @Builder.Default
    @Accessors(fluent = true)
    private boolean hasAbility = false;

    @Getter
    @Setter
    @Builder.Default
    private Class<? extends RelicStats> config = RelicStats.class;

    @Getter
    @Setter
    @Singular("loot")
    private List<RelicLoot> loot;

    @SneakyThrows
    public RelicConfigData<?> toConfigData() {
        return new RelicConfigData<>(config.newInstance(), level, durability, loot);
    }
}