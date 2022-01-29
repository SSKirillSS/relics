package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.data.runes.RuneConfigData;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.world.item.Items;

import java.awt.*;

public class AirRuneItem extends RuneItem {
    public AirRuneItem() {
        super(new Color(255, 255, 255));
    }

    @Override
    public RuneConfigData getConfigData() {
        return RuneConfigData.builder()
                .ingredients(Lists.newArrayList(Items.FEATHER, Items.SUGAR, Items.SUGAR_CANE, Items.STRING))
                .build();
    }
}