package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.data.runes.RuneConfigData;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.world.item.Items;

import java.awt.*;

public class SunRuneItem extends RuneItem {
    public SunRuneItem() {
        super(new Color(255, 212, 0));
    }

    @Override
    public RuneConfigData getConfigData() {
        return RuneConfigData.builder()
                .ingredients(Lists.newArrayList(Items.GLASS_PANE, Items.SUNFLOWER))
                .build();
    }
}