package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.List;

public class LoveRuneItem extends RuneItem {
    public LoveRuneItem() {
        super (new Color(255, 0, 240));
    }

    @Override
    public List<Item> getIngredients() {
        return Lists.newArrayList(Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.TOTEM_OF_UNDYING);
    }
}