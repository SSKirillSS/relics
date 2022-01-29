package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.world.item.Item;

public class RelicScrapItem extends Item {
    protected int replenishedVolume;

    public RelicScrapItem(int replenishedVolume) {
        super(new Item.Properties().stacksTo(16)
                .tab(RelicsTab.RELICS_TAB));

        this.replenishedVolume = replenishedVolume;
    }

    public int getReplenishedVolume() {
        return replenishedVolume;
    }
}