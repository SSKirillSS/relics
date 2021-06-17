package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.List;

public class ExplosionRuneItem extends RuneItem {
    public ExplosionRuneItem() {
        super (new Color(50, 255, 0));
    }

    @Override
    public List<Item> getIngredients() {
        return Lists.newArrayList(Items.TNT, Items.TNT_MINECART, Items.GUNPOWDER);
    }
}