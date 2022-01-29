package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.data.runes.RuneConfigData;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.world.item.Items;

import java.awt.*;

public class FireRuneItem extends RuneItem {
    public FireRuneItem() {
        super(new Color(255, 0, 0));
    }

    @Override
    public RuneConfigData getConfigData() {
        return RuneConfigData.builder()
                .ingredients(Lists.newArrayList(Items.FIRE_CHARGE, Items.MAGMA_BLOCK, Items.MAGMA_CREAM, Items.NETHERITE_SCRAP,
                        Items.BLAZE_ROD, Items.BLAZE_POWDER, Items.NETHERRACK, Items.NETHER_BRICK, Items.CRYING_OBSIDIAN))
                .build();
    }
}