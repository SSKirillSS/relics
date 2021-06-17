package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.List;

public class RedstoneRuneItem extends RuneItem {
    public RedstoneRuneItem() {
        super(new Color(255, 0, 0));
    }

    @Override
    public List<Item> getIngredients() {
        return Lists.newArrayList(Items.REDSTONE, Items.COMPARATOR, Items.REDSTONE_LAMP,
                Items.REDSTONE_TORCH, Items.REPEATER, Items.DISPENSER, Items.DROPPER);
    }
}