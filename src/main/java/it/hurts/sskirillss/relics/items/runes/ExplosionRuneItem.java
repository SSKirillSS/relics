package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.data.runes.RuneConfigData;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Items;

import java.awt.*;

public class ExplosionRuneItem extends RuneItem {
    public ExplosionRuneItem() {
        super(new Color(50, 255, 0));
    }

    @Override
    public RuneConfigData getConfigData() {
        return RuneConfigData.builder()
                .ingredients(Lists.newArrayList(Items.TNT, Items.TNT_MINECART, Items.GUNPOWDER))
                .build();
    }
}