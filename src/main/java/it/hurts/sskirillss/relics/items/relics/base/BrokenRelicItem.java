package it.hurts.sskirillss.relics.items.relics.base;

import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BrokenRelicItem extends Item {
    public BrokenRelicItem(ItemStack source) {
        super(new Item.Properties()
                .tab(RelicsTab.RELICS_TAB)
                .stacksTo(source.getMaxStackSize())
                .rarity(source.getRarity()));
    }
}