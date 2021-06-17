package it.hurts.sskirillss.relics.items;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;

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

    public List<Item> getIngredients() {
        return Lists.newArrayList();
    }

    public void applyAbility(World world, BlockPos pos) {

    }
}