package it.hurts.sskirillss.relics.items.relics.head;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class FragrantFlowerItem extends RelicItem {
    private static final String TAG_BEES = "bees";

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        int bees = NBTUtils.getInt(stack, TAG_BEES, 0);
    }
}