package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.List;

public class MoonRuneItem extends RuneItem {
    public MoonRuneItem() {
        super (new Color(110, 255, 230));
    }

    @Override
    public List<Item> getIngredients() {
        return Lists.newArrayList(Items.PHANTOM_MEMBRANE, Items.CHORUS_FRUIT);
    }
}