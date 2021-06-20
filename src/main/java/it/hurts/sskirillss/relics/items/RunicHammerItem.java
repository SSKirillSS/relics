package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;

public class RunicHammerItem extends SwordItem {
    public RunicHammerItem() {
        super(ItemTier.GOLD, 9, -3.5F, new Properties().durability(100).rarity(Rarity.UNCOMMON).tab(RelicsTab.RELICS_TAB));
    }
}