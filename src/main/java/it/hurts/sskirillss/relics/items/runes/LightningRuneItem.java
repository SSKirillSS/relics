package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.List;

public class LightningRuneItem extends RuneItem {
    public LightningRuneItem() {
        super(new Color(255, 245, 0));
    }

    @Override
    public List<Item> getIngredients() {
        return Lists.newArrayList(Items.GUNPOWDER, ItemRegistry.RUNE_OF_AIR.get(),
                ItemRegistry.RUNE_OF_EARTH.get(), ItemRegistry.RUNE_OF_EXPLOSION.get());
    }
}