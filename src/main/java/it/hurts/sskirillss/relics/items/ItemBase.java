package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.item.Item;

public class ItemBase extends Item {
    public ItemBase() {
        super(new Item.Properties().tab(RelicsTab.RELICS_TAB));
    }
}