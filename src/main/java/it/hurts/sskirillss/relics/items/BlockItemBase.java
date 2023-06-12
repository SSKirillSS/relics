package it.hurts.sskirillss.relics.items;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.relics.base.ICreativeTabEntry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BlockItemBase extends BlockItem implements ICreativeTabEntry {
    public BlockItemBase(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public List<ItemStack> processCreativeTab() {
        return Lists.newArrayList(this.getDefaultInstance());
    }
}