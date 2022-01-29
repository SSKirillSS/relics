package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.data.runes.RuneConfigData;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.world.item.Items;

import java.awt.*;

public class LuckRuneItem extends RuneItem {
    public LuckRuneItem() {
        super(new Color(200, 250, 0));
    }

    @Override
    public RuneConfigData getConfigData() {
        return RuneConfigData.builder()
                .ingredients(Lists.newArrayList(Items.RABBIT_FOOT, Items.EMERALD, Items.FERN))
                .build();
    }
}