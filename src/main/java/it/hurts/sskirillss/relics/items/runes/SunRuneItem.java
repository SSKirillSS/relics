package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.List;

public class SunRuneItem extends RuneItem {
    public SunRuneItem() {
        super (new Color(255, 212, 0));
    }

    @Override
    public List<Item> getIngredients() {
        return Lists.newArrayList(Items.GLASS_PANE, Items.SUNFLOWER);
    }
}