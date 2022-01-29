package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class RunicHammerItem extends SwordItem {
    public RunicHammerItem() {
        super(Tiers.GOLD, 9, -3.5F, new Properties().durability(100).rarity(Rarity.UNCOMMON).tab(RelicsTab.RELICS_TAB));
    }
}