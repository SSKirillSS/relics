package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.data.runes.RuneConfigData;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.world.item.Items;

import java.awt.*;

public class RedstoneRuneItem extends RuneItem {
    public RedstoneRuneItem() {
        super(new Color(255, 0, 0));
    }

    @Override
    public RuneConfigData getConfigData() {
        return RuneConfigData.builder()
                .ingredients(Lists.newArrayList(Items.REDSTONE, Items.COMPARATOR, Items.REDSTONE_LAMP,
                        Items.REDSTONE_TORCH, Items.REPEATER, Items.DISPENSER, Items.DROPPER))
                .build();
    }
}