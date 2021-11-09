package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.data.runes.RuneConfigData;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Items;

import java.awt.*;

public class LoveRuneItem extends RuneItem {
    public LoveRuneItem() {
        super(new Color(255, 0, 240));
    }

    @Override
    public RuneConfigData getConfigData() {
        return RuneConfigData.builder()
                .ingredients(Lists.newArrayList(Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.TOTEM_OF_UNDYING))
                .build();
    }
}