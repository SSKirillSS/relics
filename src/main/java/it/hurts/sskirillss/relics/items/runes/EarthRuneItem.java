package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.data.runes.RuneConfigData;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Items;

import java.awt.*;

public class EarthRuneItem extends RuneItem {
    public EarthRuneItem() {
        super(new Color(80, 255, 0));
    }

    @Override
    public RuneConfigData getConfigData() {
        return RuneConfigData.builder()
                .ingredients(Lists.newArrayList(Items.OBSIDIAN, Items.STONE, Items.GRASS_BLOCK, Items.DIRT))
                .build();
    }
}