package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.init.BlockRegistry;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public class ChalkItem extends Item {
    public ChalkItem() {
        super(new Item.Properties()
                .tab(RelicsTab.RELICS_TAB)
                .stacksTo(1)
                .durability(100));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).canOcclude()
                && context.getLevel().getBlockState(context.getClickedPos().above()).getBlock() == Blocks.AIR) {
            context.getLevel().setBlockAndUpdate(context.getClickedPos().above(), BlockRegistry.CHALK_BLOCK.get().defaultBlockState());
            if (!context.getPlayer().isCreative()) context.getItemInHand().hurtAndBreak(1, context.getPlayer(), (player) -> context.getPlayer().broadcastBreakEvent(context.getHand()));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}