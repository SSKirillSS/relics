package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.List;

public class ColdRuneItem extends RuneItem {
    public ColdRuneItem() {
        super (new Color(0, 125, 255));
    }

    @Override
    public List<Item> getIngredients() {
        return Lists.newArrayList(Items.ICE, Items.BLUE_ICE, Items.PACKED_ICE, Items.SNOW_BLOCK, Items.SNOWBALL);
    }
}