package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RelicContractItem extends Item {
    public static final String TAG_BLOOD = "blood";
    public static final String TAG_DATE = "date";

    public RelicContractItem() {
        super(new Item.Properties()
                .tab(RelicsTab.RELICS_TAB)
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Player owner = RelicUtils.Owner.getOwner(stack, worldIn);

        if (owner != null) {
            tooltip.add(new TranslatableComponent("tooltip.relics.relic_contract.tooltip_1", owner.getDisplayName()));
            tooltip.add(new TranslatableComponent("tooltip.relics.relic_contract.tooltip_2", NBTUtils.getInt(stack, TAG_BLOOD, 0) + 1));
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}