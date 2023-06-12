package it.hurts.sskirillss.relics.items;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.relics.base.ICreativeTabEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemBase extends Item implements ICreativeTabEntry {
    public ItemBase(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> processCreativeTab() {
        return Lists.newArrayList(this.getDefaultInstance());
    }
}