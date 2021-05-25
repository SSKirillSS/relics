package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;

public class RuneItem extends Item {
    private final Color color;

    public RuneItem(Color color) {
        super(new Item.Properties()
                .tab(RelicsTab.RELICS_TAB)
                .stacksTo(8)
                .rarity(Rarity.UNCOMMON));
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void applyAbility(World world, BlockPos pos) {

    }
}