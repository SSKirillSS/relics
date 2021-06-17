package it.hurts.sskirillss.relics.items.runes;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.List;

public class WaterRuneItem extends RuneItem {
    public WaterRuneItem() {
        super(new Color(0, 255, 215));
    }

    @Override
    public List<Item> getIngredients() {
        return Lists.newArrayList(Items.LILY_PAD, Items.FISHING_ROD, Items.WATER_BUCKET, Items.COD, Items.SALMON,
                Items.PUFFERFISH, Items.TROPICAL_FISH, Items.SPONGE, Items.KELP, Items.INK_SAC);
    }
}