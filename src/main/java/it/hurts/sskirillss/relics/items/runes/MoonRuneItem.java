package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.data.runes.RuneConfigData;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Items;

import java.awt.*;

public class MoonRuneItem extends RuneItem {
    public MoonRuneItem() {
        super(new Color(110, 255, 230));
    }

    @Override
    public RuneConfigData getConfigData() {
        return RuneConfigData.builder()
                .ingredients(Lists.newArrayList(Items.PHANTOM_MEMBRANE, Items.CHORUS_FRUIT))
                .build();
    }
}