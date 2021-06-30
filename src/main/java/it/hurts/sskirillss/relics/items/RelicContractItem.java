package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        PlayerEntity owner = RelicUtils.Owner.getOwner(stack, worldIn);
        if (owner != null) {
            tooltip.add(new TranslationTextComponent("tooltip.relics.relic_contract.tooltip_1", owner.getDisplayName()));
            tooltip.add(new TranslationTextComponent("tooltip.relics.relic_contract.tooltip_2", NBTUtils.getInt(stack, TAG_BLOOD, 0) + 1));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}