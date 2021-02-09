package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.init.BlockRegistry;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

import java.util.UUID;

public class ChalkItem extends Item {
    public ChalkItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .maxDamage(100));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        System.out.println(UUID.randomUUID());
        if (context.getWorld().getBlockState(context.getPos()).isSolid()
                && context.getWorld().getBlockState(context.getPos().up()).getBlock() == Blocks.AIR) {
            context.getWorld().setBlockState(context.getPos().up(), BlockRegistry.CHALK_BLOCK.get().getDefaultState());
            if (!context.getPlayer().isCreative()) context.getItem().damageItem(1, context.getPlayer(), (player) -> context.getPlayer().sendBreakAnimation(context.getHand()));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}