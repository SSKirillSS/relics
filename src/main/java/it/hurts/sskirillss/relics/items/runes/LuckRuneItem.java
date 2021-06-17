package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.List;

public class LuckRuneItem extends RuneItem {
    public LuckRuneItem() {
        super (new Color(200, 250, 0));
    }

    @Override
    public List<Item> getIngredients() {
        return Lists.newArrayList(Items.RABBIT_FOOT, Items.EMERALD, Items.FERN);
    }
}