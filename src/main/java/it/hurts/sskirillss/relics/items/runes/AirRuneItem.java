package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.List;

public class AirRuneItem extends RuneItem {
    public AirRuneItem() {
        super (new Color(255, 255, 255));
    }

    @Override
    public List<Item> getIngredients() {
        return Lists.newArrayList(Items.FEATHER, Items.SUGAR, Items.SUGAR_CANE, Items.STRING);
    }
}