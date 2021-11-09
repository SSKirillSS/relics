package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.data.runes.RuneConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Items;

import java.awt.*;

public class LightningRuneItem extends RuneItem {
    public LightningRuneItem() {
        super(new Color(255, 245, 0));
    }

    @Override
    public RuneConfigData getConfigData() {
        return RuneConfigData.builder()
                .ingredients(Lists.newArrayList(Items.GUNPOWDER, ItemRegistry.RUNE_OF_AIR.get(),
                        ItemRegistry.RUNE_OF_EARTH.get(), ItemRegistry.RUNE_OF_EXPLOSION.get()))
                .build();
    }
}